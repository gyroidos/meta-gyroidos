# Test PKI bootstrap
#
# p11-signing.bbclass ties signing key/cert file content into task signatures
# via [file-checksums]. BitBake evaluates those checksums while preparing the
# runqueue, i.e. before any task has run. On a pristine TOPDIR the PKI does
# not exist yet at that point, so every fresh workspace would hash the certs
# as "absent" — identically across CI jobs — and sstate would keep serving
# artifacts signed with a previous job's PKI. Generating the PKI from the
# BuildStarted event closes that window: it fires after the configuration is
# parsed but before task signatures are computed, so already the first
# bitbake invocation hashes the real cert content.
#
# Inherited globally via INHERIT (conf/distro/cml-base.conf).
#
# Generation is split in two phases because the event handler runs in the
# bitbake server process with the host PATH, where only bash and openssl may
# be assumed — native tools built by this very build are not available there:
#  - ssig PKI (everything the file-checksums hash): host openssl, generated
#    here (or by pki-native:do_compile as fallback — both are thin wrappers
#    around gen_dev_certs.sh, which owns all generation logic).
#  - UEFI platform keys (PKI_UEFI_KEYS, PK/KEK/DB): need efitools, which is
#    only staged in pki-native's recipe sysroot (efitools-native via the BSP
#    bbappend) — generated exclusively in pki-native:do_compile. They feed no
#    task signature, so generating them after hash computation is fine.

TEST_CERT_DIR ??= "${TOPDIR}/test_certificates"
# PKI_KEY_SIZE is kept for existing local.confs; gen_dev_certs.sh takes a
# full key type (e.g. "rsa:4096", "ec:prime256v1") since the -k rework.
PKI_KEY_SIZE ??= "4096"
PKI_KEY_TYPE ??= "rsa:${PKI_KEY_SIZE}"

# Mirror of P11_SIGNING_VARS (p11-signing.bbclass, not available in the
# global config the event handler runs against) plus the kernel signing
# variables from linux-gyroidos.inc.
PKI_BOOTSTRAP_CHECK_VARS ?= "\
    SECURE_BOOT_SIG_KEY SECURE_BOOT_SIG_CERT \
    FIRMWARE_SIG_KEY FIRMWARE_SIG_CERT \
    GUESTOS_SIG_KEY GUESTOS_SIG_CERT GUESTOS_SIG_ROOT_CERT \
    KERNEL_IMA_SIG_KEY KERNEL_IMA_SIG_CERT \
    KERNEL_MODULE_SIG_KEY_CERT KERNEL_SYSTEM_TRUSTED_KEYS \
"

def _pki_run(cmd, env=None):
    import subprocess
    result = subprocess.run(cmd, env=env, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, universal_newlines=True)
    if result.returncode != 0:
        bb.fatal('pki-bootstrap: %s failed (exit %d):\n%s'
                 % (' '.join(cmd), result.returncode, result.stdout))
    return result.stdout

def gyroidos_generate_test_pki(d):
    import os

    cert_dir = d.getVar('TEST_CERT_DIR')
    provisioning_dir = d.getVar('PROVISIONING_DIR') or \
        os.path.normpath(os.path.join(d.getVar('TOPDIR'), '..', 'gyroidos', 'build', 'device_provisioning'))

    # gen_dev_certs.sh owns all generation logic: serialization (genlock),
    # idempotence (published-dir check) and atomic publish. Skip only real
    # dirs: a symlink is a seeded release PKI, for which the script derives
    # missing kernel-format exports without generating anything.
    if os.path.isdir(cert_dir) and not os.path.islink(cert_dir):
        return
    bb.note('pki-bootstrap: generating test PKI in %s' % cert_dir)
    env = dict(os.environ)
    env['DO_PLATFORM_KEYS'] = ''
    _pki_run(['bash', os.path.join(provisioning_dir, 'gen_dev_certs.sh'),
              cert_dir, d.getVar('PKI_KEY_TYPE') or ''],
             env=env)

addhandler pki_bootstrap_eventhandler
pki_bootstrap_eventhandler[eventmask] = "bb.event.BuildStarted"

python pki_bootstrap_eventhandler() {
    d = e.data
    cert_dir = d.getVar('TEST_CERT_DIR')
    # Only bootstrap when the build actually signs with files from the test
    # PKI — not for pure pkcs11/HSM setups or an externally provided PKI.
    for v in (d.getVar('PKI_BOOTSTRAP_CHECK_VARS') or '').split():
        for val in (d.getVar(v) or '').split():
            if val.startswith(cert_dir + '/'):
                gyroidos_generate_test_pki(d)
                return
}

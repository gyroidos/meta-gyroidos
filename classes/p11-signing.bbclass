P11_SIGNING_VARS = "\
    SECURE_BOOT_SIG_KEY SECURE_BOOT_SIG_CERT \
    FIRMWARE_SIG_KEY FIRMWARE_SIG_CERT \
    GUESTOS_SIG_KEY GUESTOS_SIG_CERT GUESTOS_SIG_ROOT_CERT \
    KERNEL_IMA_SIG_KEY KERNEL_IMA_SIG_CERT \
"

def _any_signing_var_matches(d, predicate):
    for v in (d.getVar('P11_SIGNING_VARS') or '').split():
        val = d.getVar(v)
        if val and predicate(val.strip()):
            return True
    return False

def _uses_pkcs11(d):
    return _any_signing_var_matches(d, lambda v: v.startswith('pkcs11:'))

def _uses_local_files(d):
    return _any_signing_var_matches(d, lambda v: not v.startswith('pkcs11:'))

# PKCS#11 infrastructure
#
# Only pulled when at least one signing variable is a pkcs11: URI.
# File-based signing (the common case for dev/test builds) needs none of
# these — openssl-native is the only unconditional dep.
def p11_deps(d):
    if _uses_pkcs11(d):
        return 'libp11-native opensc-native p11-kit-native softhsm-native gnutls-native'
    return ''

# Have this DEPENDS be conditional, such that we can inherit p11-signing for PKI generation
# without adding unnecessary DEPENDS.
DEPENDS += "openssl-native ${@p11_deps(d)}"

def get_pkcs11_module_path(d):
    backend = d.getVar('PKCS11_BACKEND')
    if backend == "opensc":
        return "${RECIPE_SYSROOT_NATIVE}/usr/lib/opensc-pkcs11.so"
    elif backend == "softhsm":
        return "${RECIPE_SYSROOT_NATIVE}/usr/lib/softhsm/libsofthsm2.so"
    else:
        import bb
        bb.fatal(f"PKCS#11 backend \"{backend}\" not set or unsupported.")

# PKCS#11 backend
PKCS11_BACKEND ?= "opensc"
PKCS11_MODULE_PATH = "${@get_pkcs11_module_path(d)}"

# sbsign and evmctl can't handle certificates passed as PKCS#11 URIs.
# This command provides measures to extract a certificate from a PKCS#11 token.
extract_cert () {
        p11tool --provider ${PKCS11_MODULE_PATH} --export-chain $1 > $2
}

# convert_cert <in> <out> <pem|der>
# Extracts the first certificate of a PEM (chain) file, converted to the
# given format. The single funnel for cert format conversions in recipes, so
# openssl invocation details live in one place.
convert_cert () {
	openssl x509 -in "$1" -outform "$3" -out "$2"
}

is_pkcs11_uri () {
	if [ "${1#pkcs11:}" != "${1}" ]; then
	    return 0 # this is TRUE in shell script. trust|me i know
	else
	    return 1
	fi
}

# variables passed to OpenSSL
export OPENSSL_ENGINES = "${RECIPE_SYSROOT_NATIVE}/usr/lib/engines-3"
export PKCS11_MODULE_PATH
export SOFTHSM2_CONF = "${RECIPE_SYSROOT_NATIVE}/etc/softhsm2.conf"

# default key overrides
# if no dedicated certificate is specified, default to the key file/URI
GUESTOS_SIG_CERT ?= "${GUESTOS_SIG_KEY}"
KERNEL_IMA_SIG_CERT ?= "${GUESTOS_SIG_KEY}"
SECURE_BOOT_SIG_CERT ?= "${SECURE_BOOT_SIG_KEY}"

# Build-order dependency on the PKI producer
#
# pki-native:do_compile produces the signing keys/certs into ${TEST_CERT_DIR}.
# Every p11-signing consumer that signs from a local file must wait for it.
# Pure pkcs11/HSM builds (all signing vars are pkcs11: URIs) need no dep.
def pki_native_dep(d):
    if _uses_local_files(d):
        return 'pki-native:do_compile'
    return ''

do_rootfs[depends]   += "${@pki_native_dep(d)}"
do_deploy[depends]   += "${@pki_native_dep(d)}"
do_install[depends]  += "${@pki_native_dep(d)}"

# Tie cert/key file content into the sstate hash so that a workspace wipe
# followed by cert regeneration invalidates cached signed artifacts instead
# of silently serving binaries signed with the previous CA.
# Also used by consumers with signing variables outside P11_SIGNING_VARS
# (e.g. the kernel module signing key in linux-gyroidos.inc).
def signing_file_checksums(d, varnames=None):
    paths = set()
    for v in (varnames or d.getVar('P11_SIGNING_VARS') or '').split():
        # a variable may hold a list of paths, e.g. a GUESTOS_SIG_CERT chain
        for val in (d.getVar(v) or '').split():
            if not val.startswith('pkcs11:'):
                paths.add(val)
    return ' '.join('%s:True' % p for p in sorted(paths))

do_rootfs[file-checksums]   += "${@signing_file_checksums(d)}"
do_deploy[file-checksums]   += "${@signing_file_checksums(d)}"
do_install[file-checksums]  += "${@signing_file_checksums(d)}"

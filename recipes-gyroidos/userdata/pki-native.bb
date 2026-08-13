LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${TOPDIR}/../gyroidos/build/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit externalsrc

SRC = "${TOPDIR}/../gyroidos/build/"
EXTERNALSRC = "${SRC}"

CFG_OVERLAY_DIR = "${S}/config_overlay"
CONFIG_CREATOR_DIR = "${S}/config_creator"
PROVISIONING_DIR = "${S}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

DEPENDS = "openssl-native"

inherit native


SSTATE_SKIP_CREATION = "1"

PKI_KEY_SIZE ?= "4096"
PKI_KEY_TYPE ?= "rsa:${PKI_KEY_SIZE}"

# gen_dev_certs.sh owns all generation logic: serialization (genlock),
# idempotence, atomic publish and the derived exports (kernel module
# signing key, PK.cer). The ssig PKI normally already exists from
# pki-bootstrap's BuildStarted handler (created before task signatures were
# computed, see pki-bootstrap.bbclass). This task is the fallback for it,
# the anchor for the [depends] ordering in p11-signing.bbclass, and the
# only place the UEFI platform keys are generated: those need efitools,
# which is staged into this recipe's sysroot (efitools-native via the BSP
# bbappend) but unavailable to the event handler on the host PATH.
# Deliberately a shell task, not python: BSP layers append shell fragments
# to it (e.g. meta-gyroidos-nxp).
do_compile() {
    DO_PLATFORM_KEYS="${PKI_UEFI_KEYS}" bash "${PROVISIONING_DIR}/gen_dev_certs.sh" "${TEST_CERT_DIR}" "${PKI_KEY_TYPE}"
}

do_clean() {
    rm -f "${TEST_CERT_DIR}.genlock" "${TEST_CERT_DIR}.lock"
    rm -rf "${TEST_CERT_DIR}" "${TEST_CERT_DIR}".tmp.*
    if [ -n "`ls ${ENROLLMENT_DIR}/certificates/ | egrep *.txt*`" ]; then
        for txt in ${ENROLLMENT_DIR}/certificates/*.txt*; do
            rm ${txt}
        done
    fi
}

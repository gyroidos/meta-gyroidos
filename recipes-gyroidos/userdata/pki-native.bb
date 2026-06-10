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

do_compile() {
    # Serialize across multiconfigs: ${TEST_CERT_DIR} is shared (TOPDIR-scoped),
    # so a parallel mc's pki-native:do_compile would otherwise race here.
    # flock makes the second invocation BLOCK until the first finishes, then the
    # .generated stamp lets it skip without redoing work — previously the second
    # invocation returned fake success and downstream consumers ran before certs existed.
    (
        flock 9
        if [ ! -f "${TEST_CERT_DIR}/.generated" ]; then
            export DO_PLATFORM_KEYS="${PKI_UEFI_KEYS}"
            export KEY_SIZE="${PKI_KEY_SIZE}"
            bash "${PROVISIONING_DIR}/gen_dev_certs.sh" "${TEST_CERT_DIR}"
            if [ ! -d "${TEST_CERT_DIR}/certs" ]; then
                mkdir -p "${TEST_CERT_DIR}/certs"
            fi
            openssl x509 -in "${TEST_CERT_DIR}/ssig_subca.cert" -outform DER -out "${TEST_CERT_DIR}/certs/signing_key.x509"
            if [ -f "${TEST_CERT_DIR}/ssig_subca.key" ]; then
                    cp "${TEST_CERT_DIR}/ssig_subca.key" "${TEST_CERT_DIR}/certs/signing_key.pem"
                    openssl x509 -in "${TEST_CERT_DIR}/ssig_subca.cert" -outform PEM >> "${TEST_CERT_DIR}/certs/signing_key.pem"
            fi
            if [ -f "${TEST_CERT_DIR}/PK.crt" ]; then
                openssl x509 -in "${TEST_CERT_DIR}/PK.crt" -outform DER -out "${TEST_CERT_DIR}/PK.cer"
            fi
            touch "${TEST_CERT_DIR}/.generated"
        fi
    ) 9>"${TEST_CERT_DIR}.lock"
}

do_clean() {
    if [ -f "${TEST_CERT_DIR}.lock" ]; then
        rm "${TEST_CERT_DIR}.lock"
    fi
    if [ -d ${TEST_CERT_DIR} ]; then
        rm -r ${TEST_CERT_DIR}
    fi
    if [ -n "`ls ${ENROLLMENT_DIR}/certificates/ | egrep *.txt*`" ]; then
        for txt in ${ENROLLMENT_DIR}/certificates/*.txt*; do
            rm ${txt}
        done
    fi
}

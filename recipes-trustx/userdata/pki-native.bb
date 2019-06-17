LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${TOPDIR}/../trustme/build/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC = "${TOPDIR}/../trustme/build/"

CFG_OVERLAY_DIR = "${SRC}/config_overlay"
CONFIG_CREATOR_DIR = "${SRC}/config_creator"
PROTO_FILE_DIR = "${WORKDIR}/cml/daemon"
PROVISIONING_DIR = "${SRC}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

DEPENDS = "sbsigntool-native efitools-native openssl-native"

inherit native

do_compile() {
   if [ ! -d ${TEST_CERT_DIR}/certs ]; then
        mkdir -p ${TEST_CERT_DIR}/certs
        bash ${PROVISIONING_DIR}/gen_dev_certs.sh ${TEST_CERT_DIR}
        openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform DER -out ${TEST_CERT_DIR}/certs/signing_key.x509
        cp ${TEST_CERT_DIR}/ssig_subca.key ${TEST_CERT_DIR}/certs/signing_key.pem
        openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform PEM >> ${TEST_CERT_DIR}/certs/signing_key.pem
   fi
}

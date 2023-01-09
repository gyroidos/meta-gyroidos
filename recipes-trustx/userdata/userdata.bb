LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${TOPDIR}/../trustme/build/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC = "${TOPDIR}/../trustme/build/"

CFG_OVERLAY_DIR = "${SRC}/config_overlay"
CONFIG_CREATOR_DIR = "${SRC}/config_creator"
PROTO_FILE_DIR = "${WORKDIR}/cml/daemon"
PROVISIONING_DIR = "${SRC}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

#DEPENDS = "pki cmld"
DEPENDS = "sbsigntool-native efitools-native"

do_install() {
  if [ ! -d ${TEST_CERT_DIR} ]; then
     bash ${PROVISIONING_DIR}/gen_dev_certs.sh ${TEST_CERT_DIR}
  fi
  install -d ${D}/cml/tokens
  cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/device.conf ${D}/cml/
  cp ${TEST_CERT_DIR}/ssig_rootca.cert ${D}/cml/tokens/
  cp ${TEST_CERT_DIR}/gen_rootca.cert ${D}/cml/tokens/

  mkdir -p ${DEPLOY_DIR_IMAGE}
  mkdir -p ${D}/cml/operatingsystems/
  cp -r ${DEPLOY_DIR_IMAGE}/trustx-guests/* ${D}/cml/operatingsystems/
}

FILES:${PN} += "/cml/* "

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://trustx.cfg" 
SRC_URI += "file://module_signing.cfg" 
SRC_URI += "file://trustx-intel.cfg" 

DEPENDS += " sbsigntool-native efitools-native pki-native"

SIGNING_BINARIES ?= "*.bin"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

do_uefi_sign() {
    for i in `find ${DEPLOY_DIR}/ -name '${SIGNING_BINARIES}'`; do
        if [ -L $i ]; then
            link=`readlink ${i}`
            ln -sf ${link}.signed ${i}.signed
        fi
        sbsign --key ${SECURE_BOOT_SIGNING_KEY} --cert ${SECURE_BOOT_SIGNING_CERT} --output ${i}.signed ${i}
    done
}

do_copy_ssig_certs() {
      openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform DER -out ${S}/certs/signing_key.x509
      
      cp ${TEST_CERT_DIR}/ssig_subca.key ${S}/certs/signing_key.pem
      openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform PEM >> ${S}/certs/signing_key.pem
}

addtask do_copy_ssig_certs after do_configure before do_build
addtask uefi_sign after do_deploy before do_build

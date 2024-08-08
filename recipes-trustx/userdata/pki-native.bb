LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${TOPDIR}/../trustme/build/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit externalsrc

SRC = "${TOPDIR}/../trustme/build/"
EXTERNALSRC = "${SRC}"

CFG_OVERLAY_DIR = "${S}/config_overlay"
CONFIG_CREATOR_DIR = "${S}/config_creator"
PROVISIONING_DIR = "${S}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

DEPENDS = "openssl-native gnutls-native"

inherit p11-signing native


SSTATE_SKIP_CREATION = "1"

do_compile() {
    if [ ! -f ${TEST_CERT_DIR}.generating ]; then
        touch ${TEST_CERT_DIR}.generating
        export DO_PLATFORM_KEYS=${PKI_UEFI_KEYS}
        bash ${PROVISIONING_DIR}/gen_dev_certs.sh ${TEST_CERT_DIR}
        if [ ! -d ${TEST_CERT_DIR}/certs ]; then
            mkdir -p ${TEST_CERT_DIR}/certs
        fi
        openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform DER -out ${TEST_CERT_DIR}/certs/signing_key.x509
        cp ${TEST_CERT_DIR}/ssig_subca.key ${TEST_CERT_DIR}/certs/signing_key.pem
        openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform PEM >> ${TEST_CERT_DIR}/certs/signing_key.pem
        if [ -f ${TEST_CERT_DIR}/PK.crt ]; then 
            openssl x509 -in ${TEST_CERT_DIR}/PK.crt -outform DER -out ${TEST_CERT_DIR}/PK.cer
        fi
        rm ${TEST_CERT_DIR}.generating
    fi
}

do_install () {
        mkdir -p "${D}/${localstatedir}/lib/softhsm/tokens"

	# need to change the softhsm2.conf for this recipes as the token store must point to
	# the install directory which is subsequently copied to the sysroot.
        cp ${SOFTHSM2_CONF} .
        sed -i "/directories.tokendir/d" softhsm2.conf
        echo "directories.tokendir = ${D}/${localstatedir}/lib/softhsm/tokens" > softhsm2.conf
        export SOFTHSM2_CONF=softhsm2.conf

        softhsm2-util --module ${PKCS11_MODULE} --init-token --force --so-pin 12345 --pin 1234 --label ssig --slot 0

	p11tool --provider ${PKCS11_MODULE} --login --set-pin 1234 --write "pkcs11:token=ssig" --load-privkey ${TEST_CERT_DIR}/ssig_subca.key --label ssig_subca --id 01
	p11tool --provider ${PKCS11_MODULE} --login --set-pin 1234 --write "pkcs11:token=ssig" --load-certificate ${TEST_CERT_DIR}/ssig_subca.cert --label ssig_subca --id 01

	p11tool --provider ${PKCS11_MODULE} --login --set-pin 1234 --write "pkcs11:token=ssig" --load-privkey ${TEST_CERT_DIR}/ssig_cml.key --label ssig_cml --id 02
	p11tool --provider ${PKCS11_MODULE} --login --set-pin 1234 --write "pkcs11:token=ssig" --load-certificate ${TEST_CERT_DIR}/ssig_cml.cert --label ssig_cml --id 02
}

FILES:${PN} += "/*"

do_clean() {
    if [ -f ${TEST_CERT_DIR}.generating ]; then
        rm ${TEST_CERT_DIR}.generating
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

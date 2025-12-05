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
    if [ ! -f ${TEST_CERT_DIR}.generating ]; then
        touch ${TEST_CERT_DIR}.generating
        export DO_PLATFORM_KEYS=${PKI_UEFI_KEYS}
        export KEY_SIZE=${PKI_KEY_SIZE}
        bash ${PROVISIONING_DIR}/gen_dev_certs.sh ${TEST_CERT_DIR}
        if [ ! -d ${TEST_CERT_DIR}/certs ]; then
            mkdir -p ${TEST_CERT_DIR}/certs
        fi

        openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform DER -out ${TEST_CERT_DIR}/certs/signing_key.x509
        if [ -f ${TEST_CERT_DIR}/ssig_subca.key ]; then
                cp ${TEST_CERT_DIR}/ssig_subca.key ${TEST_CERT_DIR}/certs/signing_key.pem
                openssl x509 -in ${TEST_CERT_DIR}/ssig_subca.cert -outform PEM >> ${TEST_CERT_DIR}/certs/signing_key.pem
        fi

        # Perform basic validity checks to provide early feedback during development
        bbwarn "Performing basic certificate validity checks"
        bbwarn "openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} ${KERNEL_MODULE_SIG_KEY_CERT}"
        openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} ${KERNEL_MODULE_SIG_KEY_CERT}

        bbwarn "openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} -untrusted ${GUESTOS_SIG_CERT} ${GUESTOS_SIG_CERT}"
        openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} -untrusted ${GUESTOS_SIG_CERT} ${GUESTOS_SIG_CERT}

        bbwarn "openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} -untrusted ${FIRMWARE_SIG_CERT} ${FIRMWARE_SIG_CERT}"
        openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} -untrusted ${FIRMWARE_SIG_CERT} ${FIRMWARE_SIG_CERT}

        bbwarn "openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} -untrusted ${SECURE_BOOT_SIG_CERT} ${SECURE_BOOT_SIG_CERT}"
        openssl verify -CAfile ${GUESTOS_SIG_ROOT_CERT} -untrusted ${SECURE_BOOT_SIG_CERT} ${SECURE_BOOT_SIG_CERT}

        if [ -f ${TEST_CERT_DIR}/PK.crt ]; then 
            openssl x509 -in ${TEST_CERT_DIR}/PK.crt -outform DER -out ${TEST_CERT_DIR}/PK.cer
        fi
        rm ${TEST_CERT_DIR}.generating
    fi
}

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

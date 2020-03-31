SCRIPT_DIR = "${TOPDIR}/../trustme/build"

CFG_OVERLAY_DIR = "${SCRIPT_DIR}/config_overlay"
CONFIG_CREATOR_DIR = "${SCRIPT_DIR}/config_creator"
PROTO_FILE_DIR = "${TOPDIR}/../trustme/cml/daemon"
PROVISIONING_DIR = "${SCRIPT_DIR}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

GUESTS_OUT ?= "${DEPLOY_DIR_IMAGE}/trustx-guests"
CLEAN_GUEST_OUT ?= "1"

TRUSTME_VERSION ?= "${@'${IMAGE_VERSION_SUFFIX}'.replace('-','')}"

DEPENDS += " pki-native protobuf-c-native cmld"

OS_NAME ?= "${PN}os"

do_sign_guestos () {
    trustme_version=${TRUSTME_VERSION}
    protoc --python_out=${ENROLLMENT_DIR}/config_creator -I${PROTO_FILE_DIR} ${PROTO_FILE_DIR}/guestos.proto
    mkdir -p ${GUESTS_OUT}

    if [ "${CLEAN_GUEST_OUT}" = "1" ]; then
        for i in ${GUESTS_OUT}/${OS_NAME}*; do
            rm -rf ${i}
        done
    fi

    if [ -f ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.squashfs ]; then
	trustme_version="$(readlink ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.squashfs | sed -e 's/${IMAGE_BASENAME}-${MACHINE}-//' | sed -e 's/.rootfs.squashfs//' )"
        mkdir -p ${GUESTS_OUT}/${OS_NAME}-${trustme_version}/
        cp ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.squashfs ${GUESTS_OUT}/${OS_NAME}-${trustme_version}/root.img
    fi


    python ${ENROLLMENT_DIR}/config_creator/guestos_config_creator.py \
            -b ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/${OS_NAME}.conf -v ${trustme_version} \
            -c ${GUESTS_OUT}/${OS_NAME}-${trustme_version}.conf \
            -i ${GUESTS_OUT}/${OS_NAME}-${trustme_version}/ -n ${OS_NAME}
    bash ${ENROLLMENT_DIR}/config_creator/sign_config.sh ${GUESTS_OUT}/${OS_NAME}-${trustme_version}.conf \
            ${TEST_CERT_DIR}/ssig.key ${TEST_CERT_DIR}/ssig.cert

    rm ${ENROLLMENT_DIR}/config_creator/guestos_pb2.py*
}

addtask do_sign_guestos after do_image_complete before do_build

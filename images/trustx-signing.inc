SCRIPT_DIR = "${TOPDIR}/../trustme/build"

CFG_OVERLAY_DIR = "${SCRIPT_DIR}/config_overlay"
CONFIG_CREATOR_DIR = "${SCRIPT_DIR}/config_creator"
PROTO_FILE_DIR = "${DEPLOY_DIR_IMAGE}/proto"
PROVISIONING_DIR = "${SCRIPT_DIR}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

GUESTS_OUT = "${DEPLOY_DIR_IMAGE}/trustx-guests"
CONFIGS_OUT = "${DEPLOY_DIR_IMAGE}/trustx-configs"

## todo: set this in local.conf
TRUSTME_VERSION = "1"

DEPENDS += " pki-native protobuf-c-native cmld"

do_sign_guestos () {
    protoc --python_out=${ENROLLMENT_DIR}/config_creator -I${PROTO_FILE_DIR} ${PROTO_FILE_DIR}/guestos.proto
    mkdir -p ${GUESTS_OUT}
    mkdir -p ${CONFIGS_OUT}
    mkdir -p ${CONFIGS_OUT}/container

    if [ -d ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION} ]; then
	rm -r ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION}
    fi
    mkdir -p ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION}/
    cp ${DEPLOY_DIR_IMAGE}/${IMAGE_BASENAME}-${MACHINE}.squashfs ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION}/root.img

    python ${ENROLLMENT_DIR}/config_creator/guestos_config_creator.py \
            -b ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/${PN}os.conf -v ${TRUSTME_VERSION} \
            -c ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION}.conf \
            -i ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION}/ -n ${PN}os
    bash ${ENROLLMENT_DIR}/config_creator/sign_config.sh ${GUESTS_OUT}/${PN}os-${TRUSTME_VERSION}.conf \
            ${TEST_CERT_DIR}/ssig.key ${TEST_CERT_DIR}/ssig.cert

    rm ${ENROLLMENT_DIR}/config_creator/guestos_pb2.py*

    cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/00000000-0000-0000-0000-000000000000.conf ${CONFIGS_OUT}/container
    sed -i '/guest_os:*/c\guest_os: \"${PN}os\"' ${CONFIGS_OUT}/container/00000000-0000-0000-0000-000000000000.conf

    cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/device.conf ${CONFIGS_OUT}
    sed -i '/c0os:*/c\c0os: \"${PN}os\"' ${CONFIGS_OUT}/device.conf
}

addtask do_sign_guestos after do_image_complete before do_build

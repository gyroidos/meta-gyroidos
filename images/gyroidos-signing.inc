SCRIPT_DIR = "${TOPDIR}/../gyroidos/build"

CFG_OVERLAY_DIR = "${SCRIPT_DIR}/config_overlay"
CONFIG_CREATOR_DIR = "${SCRIPT_DIR}/config_creator"
PROTO_FILE_DIR = "${WORKDIR}/recipe-sysroot/${includedir}/proto"
PROVISIONING_DIR = "${SCRIPT_DIR}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"
TEST_CERT_DIR = "${TOPDIR}/test_certificates"

inherit p11-signing

GUESTOS_SIG_KEY ?= "${TEST_CERT_DIR}/ssig_cml.key"
GUESTOS_SIG_CERT ?= "${TEST_CERT_DIR}/ssig_cml.cert"

GUESTS_OUT ?= "${B}/gyroidos-guests"
CLEAN_GUEST_OUT ?= "1"

CONFIGS_OUT ?= ""

GYROIDOS_VERSION ?= "${@'${IMAGE_VERSION_SUFFIX}'.replace('-','')}"

DEPENDS += " pki-native python3-native protobuf-native python3-protobuf-native protobuf-c-native cryptsetup-native cmld"

OS_NAME ?= "${PN}os"
OS_CONFIG ?= "undefined"

IMAGE_POSTPROCESS_COMMAND:append = " do_sign_guestos; do_deploy_guestos; "

do_sign_guestos () {
    if [ "${OS_CONFIG}" = "undefined" ]; then
        bbfatal "Variable \"OS_CONFIG\" for guestos ${PN} is not set. Please set \"OS_CONFIG\" in the image recipe."
    fi

    gyroidos_version=${GYROIDOS_VERSION}
    protoc --python_out=${ENROLLMENT_DIR}/config_creator -I${PROTO_FILE_DIR} ${PROTO_FILE_DIR}/guestos.proto
    mkdir -p ${GUESTS_OUT}

    if [ "${CLEAN_GUEST_OUT}" = "1" ]; then
        for i in ${GUESTS_OUT}/${OS_NAME}*; do
            rm -rf ${i}
        done
    fi

    if [ -f ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.squashfs ]; then

        gyroidos_version="$(readlink ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.squashfs | sed -e 's/${IMAGE_LINK_NAME}-// ; s/.rootfs// ; s/.squashfs//' )"
        mkdir -p ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}/

        dd if=/dev/zero of=${B}/${IMAGE_LINK_NAME}.hash bs=1M count=10

        root_hash=$(veritysetup format ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.squashfs \
            ${B}/${IMAGE_LINK_NAME}.hash | \
            tail -n 1 | \
            cut -d ":" -f2 | \
            tr -d '[:space:]')

        cp ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.squashfs ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}/root.img
        cp ${B}/${IMAGE_LINK_NAME}.hash ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}/root.hash.img

        python3 ${ENROLLMENT_DIR}/config_creator/guestos_config_creator.py \
            -b ${OS_CONFIG} -v ${gyroidos_version} \
            -c ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}.conf \
            -i ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}/ -n ${OS_NAME} \
            -u "${UPSTREAM_VERSION}" -d ${root_hash}
    else
        python3 ${ENROLLMENT_DIR}/config_creator/guestos_config_creator.py \
            -b ${OS_CONFIG} -v ${gyroidos_version} \
            -c ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}.conf \
            -i ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}/ -n ${OS_NAME} \
            -u "${UPSTREAM_VERSION}"
    fi

    bash ${ENROLLMENT_DIR}/config_creator/sign_config.sh ${GUESTS_OUT}/${OS_NAME}-${gyroidos_version}.conf \
            "${GUESTOS_SIG_KEY}" "${GUESTOS_SIG_CERT}"

    rm ${ENROLLMENT_DIR}/config_creator/guestos_pb2.py*
}

do_deploy_guestos () {
    cp -r "${GUESTS_OUT}" "${IMGDEPLOYDIR}"
    if [ -n "${CONFIGS_OUT}" ]; then
        cp -r "${CONFIGS_OUT}" "${IMGDEPLOYDIR}"
    fi
}

inherit image
inherit gyroidosgeneric

LICENSE = "GPL-2.0-only"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="${GYROIDOS_FSTYPES}"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "gyroidos-cml-initramfs"

PACKAGE_CLASSES = "package_ipk"

GYROIDOS_DATAPART_LABEL = "gyroidos"

prepare_device_conf () {
    cp "${THISDIR}/${PN}/device.conf" "${WORKDIR}"

    if [ "y" = "${DEVELOPMENT_BUILD}" ] &&  [ "n" = "${CC_MODE}" ];then
        if [ -z "$(grep 'signed_configs' ${WORKDIR}/device.conf)" ];then
            bbwarn "Disabling signature enforcement for container configuration in dev build"
            echo "signed_configs: false" >> ${WORKDIR}/device.conf
        else
            bbwarn "Setting for signed_configs already specified, leaving unchanged..."
        fi
    fi
}
#IMAGE_PREPROCESS_COMMAND:append = " prepare_device_conf;"

##### provide a tarball for cml update
include images/gyroidos-signing.inc
deltask do_sign_guestos
IMAGE_POSTPROCESS_COMMAND:remove = "do_sign_guestos;"

do_rootfs:prepend () {
	prepare_kernel_conf
	prepare_device_conf
	do_sign_guestos
}

GUESTS_OUT = "${B}/cml_updates"
CLEAN_GUEST_OUT = ""
OS_NAME = "kernel"
UPDATE_OUT_GENERIC="${GUESTS_OUT}/${OS_NAME}"
UPDATE_OUT="${UPDATE_OUT_GENERIC}-${GYROIDOS_VERSION}"
UPDATE_FILES="${UPDATE_OUT_GENERIC} ${UPDATE_OUT_GENERIC}.conf ${UPDATE_OUT_GENERIC}.sig ${UPDATE_OUT_GENERIC}.cert"

do_sign_guestos:prepend () {
        mkdir -p "${UPDATE_OUT}"
        cp "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGE_FILE}" "${UPDATE_OUT}/kernel.img"
        cp "${DEPLOY_DIR_IMAGE}/gyroidos-cml-firmware-${MACHINE}.squashfs" "${UPDATE_OUT}/firmware.img"
        cp "${DEPLOY_DIR_IMAGE}/gyroidos-cml-modules-${MACHINE}.squashfs" "${UPDATE_OUT}/modules.img"
        cp "${WORKDIR}/device.conf" "${UPDATE_OUT}/device.img"
}

do_sign_guestos:append () {
        tar cf "${UPDATE_OUT}.tar" -C "${GUESTS_OUT}" \
                "${OS_NAME}-${GYROIDOS_VERSION}" \
                "${OS_NAME}-${GYROIDOS_VERSION}.conf" \
                "${OS_NAME}-${GYROIDOS_VERSION}.sig" \
                "${OS_NAME}-${GYROIDOS_VERSION}.cert"

        ln -sf "$(basename ${UPDATE_OUT})" "${UPDATE_OUT_GENERIC}"
        ln -sf "$(basename ${UPDATE_OUT}.conf)" "${UPDATE_OUT_GENERIC}.conf"
        ln -sf "$(basename ${UPDATE_OUT}.cert)" "${UPDATE_OUT_GENERIC}.cert"
        ln -sf "$(basename ${UPDATE_OUT}.sig)" "${UPDATE_OUT_GENERIC}.sig"
}

OS_CONFIG = "${WORKDIR}/${OS_NAME}.conf"
prepare_kernel_conf () {
    cp "${OS_CONFIG_IN}" "${OS_CONFIG}"
}

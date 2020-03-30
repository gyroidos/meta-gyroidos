SUMMARY = "Firmware Image for CML."

PACKAGE_INSTALL = "linux-firmware"
IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""
ROOTFS_BOOTSTRAP_INSTALL = ""

IMAGE_FSTYPES = "squashfs"

IMAGE_FEATURES_remove += "package-management"

inherit image

move_firmware() {
	mv ${IMAGE_ROOTFS}/lib/firmware ${IMAGE_ROOTFS}/firmware
}

cleanup_root() {
	rm -rf ${IMAGE_ROOTFS}/etc
	rm -rf ${IMAGE_ROOTFS}/run
	rm -rf ${IMAGE_ROOTFS}/var
	rmdir ${IMAGE_ROOTFS}/lib
}

ROOTFS_POSTPROCESS_COMMAND_append = " move_firmware; "
IMAGE_PREPROCESS_COMMAND_append = " cleanup_root; "

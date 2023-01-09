SUMMARY = "Firmware Image for CML."

PACKAGE_INSTALL = "linux-firmware wireless-regdb-static"
IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""
ROOTFS_BOOTSTRAP_INSTALL = ""

IMAGE_FSTYPES = "squashfs"

IMAGE_FEATURES:remove += "package-management"

inherit image

TEST_CERT_DIR = "${TOPDIR}/test_certificates"

DEPENDS += "ima-evm-utils-native"

move_firmware() {
	mv ${IMAGE_ROOTFS}/lib/firmware/* ${IMAGE_ROOTFS}/
	evmctl sign -r --imasig --hashalgo sha256 --key ${TEST_CERT_DIR}/ssig_subca.key ${IMAGE_ROOTFS}/
}

cleanup_root() {
	rm -rf ${IMAGE_ROOTFS}/etc
	rm -rf ${IMAGE_ROOTFS}/run
	rm -rf ${IMAGE_ROOTFS}/var
	rmdir ${IMAGE_ROOTFS}/lib/firmware
	rmdir ${IMAGE_ROOTFS}/lib
}

ROOTFS_POSTPROCESS_COMMAND:append = " move_firmware; "
IMAGE_PREPROCESS_COMMAND:append = " cleanup_root; "

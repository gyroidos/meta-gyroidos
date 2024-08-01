SUMMARY = "Firmware Image for CML."

PACKAGE_INSTALL = "linux-firmware wireless-regdb-static"
IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""
ROOTFS_BOOTSTRAP_INSTALL = ""

IMAGE_FSTYPES = "squashfs"

IMAGE_FEATURES:remove = "package-management"

inherit image

TEST_CERT_DIR = "${TOPDIR}/test_certificates"

DEPENDS += "ima-evm-utils-native"

inherit p11-signing
EVMCTL_CMD = "evmctl ima_sign -r --hashalgo sha256 --key ${TEST_CERT_DIR}/ssig_subca.key ${IMAGE_ROOTFS}/"
EVMCTL_CMD:pkcs11-sign = "evmctl ima_sign -r --hashalgo sha256 --engine pkcs11 --key '${KERNEL_MODULE_SIG_KEY}' --keyid-from-cert '${STAGING_KERNEL_BUILDDIR}/certs/signing_key.x509' ${IMAGE_ROOTFS}/"
move_firmware() {
	mv ${IMAGE_ROOTFS}/lib/firmware/* ${IMAGE_ROOTFS}/
	${EVMCTL_CMD}
}

cleanup_root() {
	rm -rf ${IMAGE_ROOTFS}/etc
	rm -rf ${IMAGE_ROOTFS}/run
	rm -rf ${IMAGE_ROOTFS}/var
	rm -rf ${IMAGE_ROOTFS}/lib/firmware
	rm -rf ${IMAGE_ROOTFS}/lib
}

ROOTFS_POSTPROCESS_COMMAND:append = " move_firmware; "
IMAGE_PREPROCESS_COMMAND:append = " cleanup_root; "

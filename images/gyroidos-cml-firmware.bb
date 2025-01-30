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

move_firmware() {
	mv ${IMAGE_ROOTFS}/lib/firmware/* ${IMAGE_ROOTFS}/

	certpath="${FIRMWARE_SIG_CERT}"
	if is_pkcs11_uri ${FIRMWARE_SIG_CERT}; then
		certpath="${WORKDIR}/FIRMWARE_SIG_CERT"
		extract_cert "${FIRMWARE_SIG_CERT}" "${certpath}.pem"
		openssl x509 -in "${certpath}.pem" -outform DER -out "${certpath}.der"
	fi

	if is_pkcs11_uri ${FIRMWARE_SIG_KEY}; then
		evmctl ima_sign -r --hashalgo sha256 --engine pkcs11 --key "${FIRMWARE_SIG_KEY}" --keyid-from-cert "${certpath}.der" "${IMAGE_ROOTFS}/"
	else
		evmctl ima_sign -r --hashalgo sha256 --key "${FIRMWARE_SIG_KEY}" "${IMAGE_ROOTFS}/"
	fi
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

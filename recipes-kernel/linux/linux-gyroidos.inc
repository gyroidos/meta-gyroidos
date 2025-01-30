SRC_URI += " \
	file://0001-video-logo-GyroidOS-specific-logo.patch \
	file://0001-audit-allow-logging-of-user-events-in-non-initial-na.patch \
        file://0001-tools-lib-subcmd-Fixed-uninitialized-use-of-variable.patch \
	file://module_signing.cfg \
	file://gyroidos.cfg \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/generic:"

DEPENDS += "squashfs-tools-native openssl-native"

MODULE_IMAGE_SUFFIX = "squashfs"

EXTRA_OEMAKE += " INSTALL_MOD_STRIP=1"
KERNEL_EXTRA_ARGS:prepend = "${PARALLEL_MAKE} "

KERNEL_MODULE_SIG_KEY_CERT ?= ""
KERNEL_SYSTEM_TRUSTED_KEYS ?= ""

inherit p11-signing

kernel_do_configure:prepend() {
	# replace or add the config option depending of if it already exists in .config
	# sed command reference: https://superuser.com/a/976712
	if [ "" != "${KERNEL_MODULE_SIG_KEY_CERT}" ]; then
		sed -i '/^CONFIG_MODULE_SIG_KEY=/{h;s|=.*|="${KERNEL_MODULE_SIG_KEY_CERT}"|};${x;/^$/{s||CONFIG_MODULE_SIG_KEY="${KERNEL_MODULE_SIG_KEY_CERT}"|;H};x}' ${B}/.config
	fi

	# if KERNEL_SYSTEM_TRUSTED_KEYS is a pkcs#11 URI, extract the certificate
	if [ "" != "${KERNEL_SYSTEM_TRUSTED_KEYS}" ]; then
		if is_pkcs11_uri ${KERNEL_SYSTEM_TRUSTED_KEYS}; then
			extract_cert "${KERNEL_SYSTEM_TRUSTED_KEYS}" "${WORKDIR}/KERNEL_SYSTEM_TRUSTED_KEYS.pem"
			sed -i '/^CONFIG_SYSTEM_TRUSTED_KEYS=/{h;s|=.*|="${WORKDIR}/KERNEL_SYSTEM_TRUSTED_KEYS.pem"|};${x;/^$/{s||CONFIG_SYSTEM_TRUSTED_KEYS="${WORKDIR}/KERNEL_SYSTEM_TRUSTED_KEYS.pem"|;H};x}' ${B}/.config
		else
			sed -i '/^CONFIG_SYSTEM_TRUSTED_KEYS=/{h;s|=.*|="${KERNEL_SYSTEM_TRUSTED_KEYS}"|};${x;/^$/{s||CONFIG_SYSTEM_TRUSTED_KEYS="${KERNEL_SYSTEM_TRUSTED_KEYS}"|;H};x}' ${B}/.config
		fi
	fi
}

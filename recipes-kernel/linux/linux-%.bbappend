SRC_URI += " \
	file://module_signing.cfg \
	file://trustx.cfg \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/generic:"

DEPENDS += "squashfs-tools-native openssl-native"

MODULE_IMAGE_SUFFIX = "squashfs"

EXTRA_OEMAKE += " INSTALL_MOD_STRIP=1"

KERNEL_MODULE_SIG_KEY ?= ""
KERNEL_SYSTEM_TRUSTED_KEYS ?= ""

kernel_do_configure:prepend() {
	# replace or add the config option depending of if it already exists in .config
	# sed command reference: https://superuser.com/a/976712
	if [ "" != "${KERNEL_MODULE_SIG_KEY}" ]; then
		sed -i '/^CONFIG_MODULE_SIG_KEY=/{h;s|=.*|="${KERNEL_MODULE_SIG_KEY}"|};${x;/^$/{s||CONFIG_MODULE_SIG_KEY="${KERNEL_MODULE_SIG_KEY}"|;H};x}' ${B}/.config
	fi

	if [ "" != "${KERNEL_SYSTEM_TRUSTED_KEYS}" ]; then
		sed -i '/^CONFIG_SYSTEM_TRUSTED_KEYS=/{h;s|=.*|="${KERNEL_SYSTEM_TRUSTED_KEYS}"|};${x;/^$/{s||CONFIG_SYSTEM_TRUSTED_KEYS="${KERNEL_SYSTEM_TRUSTED_KEYS}"|;H};x}' ${B}/.config
	fi
}

kernel_do_deploy:append() {
	bbwarn "kernel do deploy"
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		kernelabiversion="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"
		bbnote "Updating modules dependencies for kernel $kernelabiversion"
		sh -c "cd \"${D}${root_prefix}\" && depmod --basedir \"${D}${root_prefix}\" ${kernelabiversion}"
		mksquashfs ${D}${root_prefix}/lib/modules $deployDir/modules-${MODULE_TARBALL_NAME}.${MODULE_IMAGE_SUFFIX}
		ln -sf modules-${MODULE_TARBALL_NAME}.${MODULE_IMAGE_SUFFIX} $deployDir/modules-${MODULE_TARBALL_LINK_NAME}.${MODULE_IMAGE_SUFFIX}
	fi
}

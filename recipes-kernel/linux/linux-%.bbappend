SRC_URI += " \
	file://module_signing.cfg \
	file://trustx.cfg \
"
FILESEXTRAPATHS_prepend := "${THISDIR}/generic:"

DEPENDS += "squashfs-tools-native openssl-native"

MODULE_IMAGE_SUFFIX = "squashfs"

kernel_do_deploy_append() {
	bbwarn "kernel do deploy"
	if (grep -q -i -e '^CONFIG_MODULES=y$' .config); then
		kernelabiversion="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"
		bbnote "Updating modules dependencies for kernel $kernelabiversion"
		sh -c "cd \"${D}${root_prefix}\" && depmod --basedir \"${D}${root_prefix}\" ${kernelabiversion}"
		mksquashfs ${D}${root_prefix}/lib/modules $deployDir/modules-${MODULE_TARBALL_NAME}.${MODULE_IMAGE_SUFFIX}
		ln -sf modules-${MODULE_TARBALL_NAME}.${MODULE_IMAGE_SUFFIX} $deployDir/modules-${MODULE_TARBALL_LINK_NAME}.${MODULE_IMAGE_SUFFIX}
	fi
}

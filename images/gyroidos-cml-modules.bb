SUMMARY = "Kernel Modules Image for CML."

PACKAGE_INSTALL = "kernel-modules"
IMAGE_INSTALL = ""
IMAGE_LINGUAS = ""
ROOTFS_BOOTSTRAP_INSTALL = ""

IMAGE_FSTYPES = "squashfs"
IMAGE_NAME_SUFFIX = ""

IMAGE_FEATURES:remove = "package-management"

inherit image

move_modules() {
	kernelabiversion="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"
	bbnote "Updating modules dependencies for kernel $kernelabiversion"
	sh -c "cd \"${IMAGE_ROOTFS}\" && depmod --basedir \"${IMAGE_ROOTFS}\" ${kernelabiversion}"
	mv ${IMAGE_ROOTFS}/lib/modules/* ${IMAGE_ROOTFS}/
}

cleanup_root() {
	rm -rf ${IMAGE_ROOTFS}/boot
	rm -rf ${IMAGE_ROOTFS}/etc
	rm -rf ${IMAGE_ROOTFS}/run
	rm -rf ${IMAGE_ROOTFS}/var
	rm -rf ${IMAGE_ROOTFS}/lib
}

ROOTFS_POSTPROCESS_COMMAND:append = " move_modules; "
IMAGE_PREPROCESS_COMMAND:append = " cleanup_root; "

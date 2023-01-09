inherit image

LICENSE = "GPL-2.0-only"

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="${TRUSTME_FSTYPES}"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "trustx-cml-initramfs"

PACKAGE_CLASSES = "package_ipk"

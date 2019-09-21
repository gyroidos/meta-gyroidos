inherit image
inherit trustmezynq
inherit trustmex86
inherit trustmerpi

LICENSE = "GPLv2"

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="${TRUSTME_FSTYPES}"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "trustx-cml-initramfs"

PACKAGE_CLASSES = "package_ipk"

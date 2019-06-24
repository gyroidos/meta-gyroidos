inherit image
inherit trustmeimage

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="trustmeimage"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "trustx-cml-initramfs"

do_bundle_initramfs[nostamp] = "1"

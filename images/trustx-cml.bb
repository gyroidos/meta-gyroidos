inherit image
inherit trustmeimage

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="trustmeimage"

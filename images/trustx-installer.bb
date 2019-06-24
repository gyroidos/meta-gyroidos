inherit image
inherit trustmeinstaller

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"
KERNEL_IMAGETYPE="installer"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="trustmeinstaller"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "trustx-installer-initramfs"

do_bundle_initramfs[nostamp] = "1"

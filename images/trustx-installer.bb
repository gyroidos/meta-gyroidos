inherit image
inherit trustmeinstaller

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="trustmeinstaller"

INITRAMFS_IMAGE_BUNDLE = "1"
INITRAMFS_IMAGE = "trustx-installer-initramfs"

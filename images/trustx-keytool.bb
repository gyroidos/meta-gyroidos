DECRIPTION = "KeyTool image"

DEPENDS= "efitools-native"

IMAGE_LINUGUAS = " "

LICENSE = "GPLv2"

IMAGE_FEATURES = ""

export IMAGE_BASENAME = "trustx-keytool"
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit image

IMAGE_ROOTFS_SIZE = "4096"

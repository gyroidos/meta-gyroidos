require recipes-core/images/core-image-minimal.bb

include images/trustx-signing.inc

IMAGE_INSTALL += "strace"
IMAGE_INSTALL += "service"

IMAGE_FSTYPES = "squashfs ext4"

EXTRA_IMAGE_FEATURES += "allow-empty-password"
EXTRA_IMAGE_FEATURES += "empty-root-password"
EXTRA_IMAGE_FEATURES += "ssh-server-dropbear"

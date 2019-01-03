SUMMARY = "Skeleton for installing a Debian based container."

LICENSE = "GPLv2"

include images/trustx-signing.inc

#IMAGE_INSTALL = "debian-rootfs"
IMAGE_INSTALL = "\
	busybox \
	apt \
	gnupg \
	perl \
	debian-archive-keyring \
	debootstrap \
	service \
	debian-boot \
"

IMAGE_LINGUAS = ""

IMAGE_FSTYPES = "squashfs"

inherit image

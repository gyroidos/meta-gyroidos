SUMMARY = "Skeleton for installing a Debian based container."

LICENSE = "GPL-2.0-only"

include images/gyroidos-signing.inc

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

MKNOD_BIN = "${IMAGE_ROOTFS}${base_bindir}/mknod"

replace_mknod() {
    rm ${MKNOD_BIN}
    echo "#!/bin/sh" > ${MKNOD_BIN}
    echo "exit 0" >> ${MKNOD_BIN}
    chmod 755 ${MKNOD_BIN}
}

ROOTFS_POSTPROCESS_COMMAND:append = " replace_mknod; "

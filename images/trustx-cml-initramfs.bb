DECRIPTION = "Minimal initramfs-based root file system for CML"

PACKAGE_INSTALL = "\
	${VIRTUAL-RUNTIME_base-utils} \
	udev \
	base-files \
	cmld \
	service-static \
	scd \
	iptables \
	ibmtss2 \
	tpm2d \
	openssl-tpm2-engine \
	sc-hsm-embedded \
	e2fsprogs-mke2fs \
	e2fsprogs-e2fsck \
	btrfs-tools \
	${ROOTFS_BOOTSTRAP_INSTALL} \
	cml-boot \
	iproute2 \
	iw \
	lxcfs \
	pv \
	uid-wrapper \
"

# For debug purpose image install additional packages if debug-tweaks is set in local.conf
DEBUG_PACKAGES = "\
	base-passwd \
	shadow \
	stunnel \
	control \
	mingetty \
	rattestation \
	openssl-bin \
	gptfdisk \
	parted \
	util-linux-sfdisk \
"

PACKAGE_INSTALL_append = '${@bb.utils.contains_any("EXTRA_IMAGE_FEATURES", [ 'debug-tweaks' ], "${DEBUG_PACKAGES}", "",d)}'


#PACKAGE_INSTALL += "\
#	strace \
#	kvmtool \
#"

IMAGE_LINUGUAS = " "

LICENSE = "GPLv2"

IMAGE_FEATURES = ""

export IMAGE_BASENAME = "trustx-cml-initramfs"
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit image

IMAGE_FEATURES_remove += "package-management"

IMAGE_ROOTFS_SIZE = "4096"

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

update_inittab () {
    echo "tty12::respawn:${base_sbindir}/mingetty --autologin root tty12" >> ${IMAGE_ROOTFS}/etc/inittab

    mkdir -p ${IMAGE_ROOTFS}/dev
    mknod -m 622 ${IMAGE_ROOTFS}/dev/tty12 c 4 12
}

update_inittab_release () {
    # clean out any serial consoles
    sed -i "/ttyS[[:digit:]]\+/d" ${IMAGE_ROOTFS}/etc/inittab
}

#TODO modsigning option in image fstype?
TEST_CERT_DIR = "${TOPDIR}/test_certificates"
install_ima_cert () {
	mkdir -p ${IMAGE_ROOTFS}/etc/keys
	cp ${TEST_CERT_DIR}/certs/signing_key.x509 ${IMAGE_ROOTFS}/etc/keys/x509_ima.der
}

update_modules_dep () {
	if [ -d "${IMAGE_ROOTFS}/lib/modules" ];then
		sh -c 'cd "${IMAGE_ROOTFS}" && depmod --basedir "${IMAGE_ROOTFS}" --config "${IMAGE_ROOTFS}/etc/depmod.d" ${KERNELVERSION}'
	else
		bbwarn "no /lib/modules directory in initramfs - is this intended?"
		mkdir -p "${IMAGE_ROOTFS}/lib/modules"
	fi
}

update_hostname () {
    echo "trustx-cml" > ${IMAGE_ROOTFS}/etc/hostname
}

cleanup_boot () {
	rm -f ${IMAGE_ROOTFS}/boot/*
}

ROOTFS_POSTPROCESS_COMMAND_append = " update_modules_dep; "
ROOTFS_POSTPROCESS_COMMAND_append = " update_hostname; "
ROOTFS_POSTPROCESS_COMMAND_append = " cleanup_boot; "
ROOTFS_POSTPROCESS_COMMAND_append = " install_ima_cert; "

# For debug purpose allow login if debug-tweaks is set in local.conf
ROOTFS_POSTPROCESS_COMMAND_append = '${@bb.utils.contains_any("EXTRA_IMAGE_FEATURES", [ 'debug-tweaks' ], " update_inittab ; ", " update_inittab_release ; ",d)}'

inherit extrausers
EXTRA_USERS_PARAMS = '${@bb.utils.contains_any("EXTRA_IMAGE_FEATURES", [ 'debug-tweaks' ], "usermod -P root root; ", "",d)}'

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
	lxcfs \
	pv \
"

# Install additional packages for debugging purposes if DEVELOPMENT_BUILD is set
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
	util-linux \
	openssh-sshd \
	ssh-keys \
	tcpdump \
	binutils \
	gdb \
	gdbserver \
	strace \
	cmld-dbg \
"

PACKAGE_INSTALL:append = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', "${DEBUG_PACKAGES}", "",d)}'

# shadow-base is removed by poky-tiny, yet in our dev build it is required by openssh
# the required CONFIG_MULTIUSER is set in our kernel config
PACKAGE_EXCLUDE:remove = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', "shadow-base", "",d)}'

IMAGE_LINUGUAS = " "

LICENSE = "GPL-2.0-only"

IMAGE_FEATURES = ""

export IMAGE_BASENAME = "gyroidos-cml-initramfs"
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit image

IMAGE_FEATURES:remove = "package-management"

IMAGE_ROOTFS_SIZE = "4096"

IMAGE_NAME_SUFFIX=""

do_rootfs[depends] += "virtual/kernel:do_shared_workdir"
KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

# In development builds, a debugging shell is available on tty12 for debugging purposes
update_tabs () {
	echo "tty12::respawn:${base_sbindir}/mingetty --autologin root tty12" >> ${IMAGE_ROOTFS}/etc/inittab

	mkdir -p ${IMAGE_ROOTFS}/dev
	mknod -m 622 ${IMAGE_ROOTFS}/dev/tty12 c 4 12
}

update_tabs_release () {
	# clean out any serial consoles
	sed -i "/ttyS[[:digit:]]\+/d" ${IMAGE_ROOTFS}/etc/inittab
}

#TODO modsigning option in image fstype?
inherit p11-signing
TEST_CERT_DIR = "${TOPDIR}/test_certificates"
install_ima_cert () {
	if [ -z "${FIRMWARE_SIG_CERT}" ];then
		bbfatal "GUESTOS_SIG_ROOT_CERT is not set. Set GUESTOS_SIG_ROOT_CERT in local.conf."
	fi

	mkdir -p ${IMAGE_ROOTFS}/etc/keys

	if is_pkcs11_uri ${FIRMWARE_SIG_CERT}; then
		extract_cert "${FIRMWARE_SIG_CERT}" "${WORKDIR}/FIRMWARE_SIG_CERT.pem"
		openssl x509 -in "${WORKDIR}/FIRMWARE_SIG_CERT.pem" -outform DER -out "${IMAGE_ROOTFS}/etc/keys/x509_ima.der"
	else
		if ! [ -f "${FIRMWARE_SIG_CERT}" ];then
			bbfatal_log "IMA certificate not present at ${GUESTOS_SIG_ROOT_CERT}."
			exit 1
		fi
		openssl x509 -in "${FIRMWARE_SIG_CERT}" -outform DER -out "${IMAGE_ROOTFS}/etc/keys/x509_ima.der"
	fi
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
    echo "cml" > ${IMAGE_ROOTFS}/etc/hostname
}

cleanup_boot () {
	rm -f ${IMAGE_ROOTFS}/boot/*
}

ROOTFS_POSTPROCESS_COMMAND:append = " update_modules_dep; "
ROOTFS_POSTPROCESS_COMMAND:append = " update_hostname; "
ROOTFS_POSTPROCESS_COMMAND:append = " cleanup_boot; "
ROOTFS_POSTPROCESS_COMMAND:append = " install_ima_cert; "

# For debug purpose allow login if debug-tweaks is set in local.conf
ROOTFS_POSTPROCESS_COMMAND:append = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', " update_tabs ; ", " update_tabs_release ; ",d)}'

inherit extrausers
# password for root is 'root'
EXTRA_USERS_PARAMS = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', "usermod -p '\\$1\\$1234\\$XJI6P4bccABjEC1v6k64V1' root; ", "",d)}'

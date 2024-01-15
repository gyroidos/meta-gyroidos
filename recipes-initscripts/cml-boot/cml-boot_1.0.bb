SUMMARY = "init script to start trustx environment"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:${THISDIR}/files:"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "\
	file://init_ascii \
	file://start_sshd \
	file://cml-boot-script.stub \
"


PR = "r2"

S = "${WORKDIR}"

CML_START_MSG = '${@bb.utils.contains_any("EXTRA_IMAGE_FEATURES", [ 'debug-tweaks' ], "-- cml debug console on tty12 [ready]", "-- cml in release mode [ready]",d)}'

do_install() {
	echo "#!/bin/sh" >> ${D}/init
	echo "# Machine ${MACHINE}" >> ${D}/init
	echo "LOGTTY=\"${TRUSTME_LOGTTY}\"" >> ${D}/init
	echo "CML_START_MSG=\"${CML_START_MSG}\"" >> ${D}/init

	cat ${WORKDIR}/cml-boot-script.stub >> ${D}/init

	if [ "y" != "${DEVELOPMENT_BUILD}" ];then
		sed -i '/^mkdir -p \/mnt\/extdata/,/^fi/d' ${D}/init
		sed -i 's|mkdir -p /data/logs|mount -o bind,nosuid,nodev,noexec \/mnt\/userdata \/data\n\nmkdir -p /data/logs|' ${D}/init
	fi

	chmod 755 ${D}/init

	install -d ${D}/${sysconfdir}
	install -m 0755 ${WORKDIR}/init_ascii ${D}${sysconfdir}/init_ascii
	install -d ${D}/dev
	mknod -m 622 ${D}/dev/console c 5 1
	mknod -m 622 ${D}/dev/tty0 c 4 0
	mknod -m 622 ${D}/dev/tty11 c 4 11
}

# Development builds include a ssh server in the cml layer.
# Add the starting command to the init script.
do_install:append () {
	if [ "y" = "${DEVELOPMENT_BUILD}" ];then
		bbwarn "Patching /init script to start SSH server in cml layer"
		sed -i '\|#DEV_START_SSHD#|e cat ${WORKDIR}/start_sshd' ${D}/init
	fi
	sed -i '/#DEV_START_SSHD#/d' ${D}/init
}

FILES:${PN} += " /init /dev ${sysconfdir}/init_ascii"

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"

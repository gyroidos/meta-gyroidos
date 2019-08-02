SUMMARY = "init script to start trustx environment"
LICENSE = "MIT"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:${THISDIR}/files:"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "\
	file://init_ascii \
	file://cml-boot-script.stub \
"


PR = "r2"

S = "${WORKDIR}"

do_install() {
	echo "#!/bin/sh" >> ${D}/init
	echo "# Machine ${MACHINE}" >> ${D}/init
	if [ "${MACHINE}" = "trustx-corei7-64" ];then
		echo "LOGTTY=\"tty11\"" >> ${D}/init
	elif [ "${MACHINE}" = "zcu104-zynqmp" ];then
		echo "LOGTTY=\"ttyPS0\"" >> ${D}/init
	fi

	cat ${WORKDIR}/cml-boot-script.stub >> ${D}/init

	chmod 755 ${D}/init

	install -d ${D}/${sysconfdir}
	install -m 0755 ${WORKDIR}/init_ascii ${D}${sysconfdir}/init_ascii
	install -d ${D}/dev
	# mknod -m 622 ${D}/dev/console c 5 1
	mknod -m 622 ${D}/dev/tty0 c 4 0
	mknod -m 622 ${D}/dev/tty11 c 4 11
}

FILES_${PN} += " /init /dev ${sysconfdir}/init_ascii"

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"

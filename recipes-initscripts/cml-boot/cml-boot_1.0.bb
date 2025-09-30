SUMMARY = "init script to start gyroidos environment"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:${THISDIR}/files:"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# With DEVELOPMENT_BUILD=y or GYROIDOS_PLAIN_DATAPART=y,
# mounting an unencrypted partiton on /data is allowed for debugging purposes
CML_MOUNT_PLAIN_DATAPART = "${@ oe.utils.vartrue('DEVELOPMENT_BUILD', True, oe.utils.vartrue('GYROIDOS_PLAIN_DATAPART', True, False, d), d) }"

# For debugging purposes, development builds include a SSH server
# in the cml layer, options to mount plain file systems on /data
# and we enable core dumps.
SRC_URI = "\
	file://init_ascii \
	file://10-cml-boot-script-early.fragment \
	file://30-mount-tpm-crypt-part.fragment \
	${@oe.utils.vartrue('CML_MOUNT_PLAIN_DATAPART', 'file://40-dev-mount-plain-cml-part.fragment', 'file://40-disable-mount-plain-cml-part.fragment', d)} \
	file://50-mount-all.fragment \
	${@oe.utils.vartrue('DEVELOPMENT_BUILD', 'file://51-dev-enable-extdata.fragment', '', d)} \
	${@oe.utils.vartrue('DEVELOPMENT_BUILD', 'file://52-dev-enable-extcontainers.fragment', '', d)} \
	${@oe.utils.vartrue('DEVELOPMENT_BUILD', 'file://55-dev-enable-core-dumps.fragment', 'file://55-disable-core-dumps.fragment', d)} \
	file://60-cml-boot-script.fragment \
	${@oe.utils.vartrue('DEVELOPMENT_BUILD', 'file://80-dev-start-sshd.fragment', '', d)} \
	${@oe.utils.vartrue('DEVELOPMENT_BUILD', 'file://85-mnt-ext9pfs.fragment', '', d)} \
	file://90-start-cmld.fragment \
	${@oe.utils.vartrue('DEVELOPMENT_BUILD', 'file://95-mnt-ext9pfs-again.fragment', '', d)} \
	file://99-start-init.fragment \
"


PR = "r2"

CML_START_MSG = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', "-- cml debug console on tty12 [ready]", "-- cml in release mode [ready]",d)}'

do_configure () {
	# dev build warnings
	if [ "y" = "${DEVELOPMENT_BUILD}" ];then
		bbwarn "Patching /init script to mount external data fs and containers fs for debugging purposes"
		bbwarn "Patching /init script to start SSH server in cml layer"
		bbwarn "Patching /init script to mount 9p file systems during early boot"
		bbwarn "Patching /init script to mount 9p file systems during boot"
		bbwarn "Enabling core dumps for debugging purposes"
	fi

	if [ "y" = "${DEVELOPMENT_BUILD}" ] || [ "y" = "${GYROIDOS_PLAIN_DATAPART}" ];then
		bbwarn "Patching /init script to mount plain CML data parition for development purposes"
	else
		bbnote "Production build: Forbid un-encrypted CML data partition"
	fi

	for f in ${SRC_URI}; do
		cp ${WORKDIR}/${f#file://} ${B}
	done
}
do_configure[cleandirs] = "${B}"

do_compile:prepend () {
	echo "#!/bin/sh" > ${B}/00-preamble.fragment
	echo "# Machine ${MACHINE}" >> ${B}/00-preamble.fragment
	echo "LOGTTY=\"${GYROIDOS_LOGTTY}\"" >> ${B}/00-preamble.fragment
	echo "CML_START_MSG=\"${CML_START_MSG}\"" >> ${B}/00-preamble.fragment
}

do_compile () {
	# use ls to ensure that fragments are assembled in correct order
	cat $(ls ${B}/*.fragment) > ${B}/init
}

do_install() {
	install ${B}/init ${D}/init

	chmod 755 ${D}/init

	install -d ${D}/${sysconfdir}
	install -m 0755 ${WORKDIR}/init_ascii ${D}${sysconfdir}/init_ascii
	install -d ${D}/dev
	mknod -m 622 ${D}/dev/console c 5 1
	mknod -m 622 ${D}/dev/tty0 c 4 0
	mknod -m 622 ${D}/dev/tty11 c 4 11
}

FILES:${PN} += " /init /dev ${sysconfdir}/init_ascii"

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"

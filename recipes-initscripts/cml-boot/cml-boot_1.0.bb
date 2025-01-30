SUMMARY = "init script to start trustx environment"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:${THISDIR}/files:"

LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "\
	file://init_ascii \
	file://dev_start_sshd \
	file://dev_enable_extdata \
	file://dev_enable_extcontainers \
	file://dev_mount_plain_cml_part \
	file://cml-boot-script.stub \
"


PR = "r2"

S = "${WORKDIR}"

CML_START_MSG = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', "-- cml debug console on tty12 [ready]", "-- cml in release mode [ready]",d)}'

do_install() {
	echo "#!/bin/sh" >> ${D}/init
	echo "# Machine ${MACHINE}" >> ${D}/init
	echo "LOGTTY=\"${GYROIDOS_LOGTTY}\"" >> ${D}/init
	echo "CML_START_MSG=\"${CML_START_MSG}\"" >> ${D}/init

	cat ${WORKDIR}/cml-boot-script.stub >> ${D}/init

	chmod 755 ${D}/init

	install -d ${D}/${sysconfdir}
	install -m 0755 ${WORKDIR}/init_ascii ${D}${sysconfdir}/init_ascii
	install -d ${D}/dev
	mknod -m 622 ${D}/dev/console c 5 1
	mknod -m 622 ${D}/dev/tty0 c 4 0
	mknod -m 622 ${D}/dev/tty11 c 4 11

	# With DEVELOPMENT_BUILD=y or GYROIDOS_PLAIN_DATAPART=y,
	# mounting an unencrypted partiton on /data is allowed for debugging purposes
	if [ "y" = "${DEVELOPMENT_BUILD}" ] || [ "y" = "${GYROIDOS_PLAIN_DATAPART}" ];then
		bbwarn "Patching /init script to mount plain CML data parition for development purposes"
		sed -i '\|#DEV_MOUNT_PLAIN_CML_PART#|e cat ${WORKDIR}/dev_mount_plain_cml_part' ${D}/init
		sed -i '/#DEV_MOUNT_PLAIN_CML_PART#/d' ${D}/init
	else
		bbnote "Production build: Forbid un-encrypted CML data partition"
		sed -i 's|#DEV_MOUNT_PLAIN_CML_PART#|exit 1|' ${D}/init
	fi


	# For debugging purposes, development builds include a SSH server
	# in the cml layer, options to mount plain file systems on /data
	# and we enable core dumps.
	if [ "y" = "${DEVELOPMENT_BUILD}" ];then
		bbwarn "Patching /init script to mount external data fs and containers fs for debugging purposes"
		sed -i '\|#DEV_ENABLE_EXTFS#|e cat ${WORKDIR}/dev_enable_extdata' ${D}/init
		sed -i '\|#DEV_ENABLE_EXTFS#|e cat ${WORKDIR}/dev_enable_extcontainers' ${D}/init

		bbwarn "Patching /init script to start SSH server in cml layer"
		sed -i '\|#DEV_START_SSHD#|e cat ${WORKDIR}/dev_start_sshd' ${D}/init

		bbwarn "Enabling core dumps for debugging purposes"
		sed -i 's|ulimit -c 0|ulimit -c 102400|' ${D}/init
		sed -i 's|.*/proc/sys/kernel/core_pattern|mkdir -p /data/core\n&|' ${D}/init
		sed -i 's|/data/core/%t_core|/data/core/%t_core.%s.%p.%P_%u_%g_%E|' ${D}/init
	fi

	sed -i '/#DEV_ENABLE_EXTFS#/d' ${D}/init
	sed -i '/#DEV_START_SSHD#/d' ${D}/init
}

FILES:${PN} += " /init /dev ${sysconfdir}/init_ascii"

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"

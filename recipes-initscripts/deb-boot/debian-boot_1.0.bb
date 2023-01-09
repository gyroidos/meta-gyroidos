SUMMARY = "init script to start deb container"
LICENSE = "MIT"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
SRC_URI = "file://deb-boot-script.sh"

PR = "r2"

S = "${WORKDIR}"

do_install() {
	install -d ${D}${base_sbindir}/
	install -m 0755 ${WORKDIR}/deb-boot-script.sh ${D}${base_sbindir}/init
}

FILES:${PN} += "${base_sbindir}/init"

# Due to kernel dependency
PACKAGE_ARCH = "${MACHINE_ARCH}"

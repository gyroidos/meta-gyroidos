inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI = "https://ftp.de.debian.org/debian/pool/main/l/linux/linux-source-4.19_${PV}_all.deb"

SRC_URI[md5sum] = "2e3542c9738a9fb871f30c534f25321e"
SRC_URI[sha256sum] = "40b47a82621a11751c43692c13ed301283366b2b23a14c331058175ac6dd87ea"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-debian"

SOURCE_DIR = "linux-source-4.19"
SOURCE_TARBALL = "usr/src/${SOURCE_DIR}.tar.xz"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

addtask kernel_unpacksrc before do_kernel_checkout after do_unpack

do_kernel_unpacksrc() {
	tar -xf ${WORKDIR}/${SOURCE_TARBALL} -C ${WORKDIR}
}

S = "${WORKDIR}/${SOURCE_DIR}"

KBUILD_DEFCONFIG_genericx86-64 = "x86_64_defconfig" 
KCONFIG_MODE="--alldefconfig"


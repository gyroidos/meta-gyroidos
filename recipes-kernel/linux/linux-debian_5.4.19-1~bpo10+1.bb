require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI = "https://ftp.de.debian.org/debian/pool/main/l/linux/linux-source-5.4_${PV}_all.deb"

SRC_URI[md5sum] = "c6d004cb00f7f7e54398b3a307e20936"
SRC_URI[sha256sum] = "7affb5d47deb69150fb63d7837dbb635009e1ca3ba558c7f8fea6ea4dde7fa85"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-debian"

SOURCE_DIR = "linux-source-5.4"
SOURCE_TARBALL = "usr/src/${SOURCE_DIR}.tar.xz"
EXTERNALSRC = "1"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

addtask kernel_unpacksrc after do_unpack

do_kernel_checkout[depends] += "linux-debian:do_kernel_unpacksrc"
do_patch[depends] += "linux-debian:do_kernel_unpacksrc"
do_kernel_metadata[depends] += "linux-debian:do_kernel_unpacksrc"
do_configure[depends] = "linux-debian:do_kernel_unpacksrc"


do_kernel_unpacksrc() {
	tar -xf ${WORKDIR}/${SOURCE_TARBALL} -C ${WORKDIR}
}

S = "${WORKDIR}/${SOURCE_DIR}"

KBUILD_DEFCONFIG_genericx86-64 = "x86_64_defconfig" 
KCONFIG_MODE="--alldefconfig"


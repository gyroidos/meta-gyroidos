inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

SRC_URI = "https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-${PV}.tar.xz"

SRC_URI[md5sum] = "f1ae5618270676470e6b2706c61d909b"
SRC_URI[sha256sum] = "669a74f4aeef07645061081d9c05d23216245702b4095afb3d957f79098f0daf"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-vanilla"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

S = "${WORKDIR}/linux-${PV}"

KBUILD_DEFCONFIG_genericx86-64 = "x86_64_defconfig" 
KCONFIG_MODE="--alldefconfig"


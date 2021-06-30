inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI = "https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-${PV}.tar.xz"

SRC_URI[md5sum] = "38ad744241074e0c02f3152364988785"
SRC_URI[sha256sum] = "569122a39c6b325befb9ac1c07da0c53e6363b3baacd82081d131b06c1dc1415"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-vanilla"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

S = "${WORKDIR}/linux-${PV}"

KBUILD_DEFCONFIG_genericx86-64 = "x86_64_defconfig" 
KCONFIG_MODE="--alldefconfig"


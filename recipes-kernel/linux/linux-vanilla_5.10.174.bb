inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI = "https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-${PV}.tar.xz"

SRC_URI[md5sum] = "49849fe2fe687ce3b65a9f551a9b5cac"
SRC_URI[sha256sum] = "2f8a0cd000d5839f0ff44ba0f3b4f9a3253ea5f78ae2a1c7484ad3fb1e8a4a24"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-vanilla"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

S = "${WORKDIR}/linux-${PV}"

KBUILD_DEFCONFIG_genericx86-64 = "x86_64_defconfig" 
KCONFIG_MODE="--alldefconfig"


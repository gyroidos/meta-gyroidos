inherit kernel
require recipes-kernel/linux/linux-yocto.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

SRC_URI = "https://cdn.kernel.org/pub/linux/kernel/v5.x/linux-${PV}.tar.xz"

SRC_URI[md5sum] = "5fadbcdf7c2fd17c43e316562e9833db"
SRC_URI[sha256sum] = "477ce8f7624263e4346c0fc25ffc334af06bcac4d6bebdd5a7fe4681557fdb39"

LINUX_VERSION ?= "${PV}"
LINUX_VERSION_EXTENSION ?= "-vanilla"

SRCREV="${AUTOREV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

S = "${WORKDIR}/linux-${PV}"

KBUILD_DEFCONFIG:genericx86-64 = "x86_64_defconfig" 
KCONFIG_MODE="--alldefconfig"


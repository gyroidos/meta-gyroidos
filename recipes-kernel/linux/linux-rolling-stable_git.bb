inherit kernel
require recipes-kernel/linux/linux-yocto.inc

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git;protocol=git;name=machine;branch=linux-rolling-stable"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION_EXTENSION = "-stable"

KERNEL_VERSION_SANITY_SKIP="1"

SRCREV_machine ?= "${AUTOREV}"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-vanilla:"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

KBUILD_DEFCONFIG:genericx86-64 = "x86_64_defconfig"
KCONFIG_MODE="--alldefconfig"

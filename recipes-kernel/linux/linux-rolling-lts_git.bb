inherit kernel
require recipes-kernel/linux/linux-yocto.inc

HOMEPAGE = "https://www.kernel.org"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git;protocol=https;name=machine;branch=linux-rolling-lts"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION ?= "1.0"
LINUX_VERSION_EXTENSION = "-lts"

KERNEL_VERSION_SANITY_SKIP="1"

SRCREV_machine ?= "${AUTOREV}"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-rolling:"
FILESEXTRAPATHS:prepend := "${THISDIR}/linux-rolling-lts:"

PV = "${LINUX_VERSION}+${SRCPV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

KBUILD_DEFCONFIG:genericx86-64 = "x86_64_defconfig"
KCONFIG_MODE="--alldefconfig"

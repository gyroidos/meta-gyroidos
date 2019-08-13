SUMMARY = "U-boot boot scripts for Xilinx devices"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "u-boot-mkimage-native"

inherit deploy nopackages

INHIBIT_DEFAULT_DEPS = "1"

KERNEL_BOOTCMD ?= "bootm"
KERNEL_CONSOLE = "tty0"
KERNEL_LOGLEVEL ?= "8"

BOOTMODE ?= "trustx"

SRC_URI = " \
            file://boot.cmd.trustx \
            "

PACKAGE_ARCH = "${MACHINE_ARCH}"

UBOOTSCR_BASE_NAME ?= "${PN}-${PKGE}-${PKGV}-${PKGR}-${DATETIME}"
UBOOTSCR_BASE_NAME[vardepsexclude] = "DATETIME"

do_compile() {
    sed -e 's/@@KERNEL_IMAGETYPE@@/${KERNEL_IMAGETYPE}/' \
        -e 's/@@KERNEL_BOOTCMD@@/${KERNEL_BOOTCMD}/' \
        -e 's/@@KERNEL_CONSOLE@@/${KERNEL_CONSOLE}/' \
        -e 's/@@KERNEL_LOGLEVEL@@/${KERNEL_LOGLEVEL}/' \
        "${WORKDIR}/boot.cmd.${BOOTMODE}" > "${WORKDIR}/boot.cmd"
    mkimage -A arm -T script -C none -n "Boot script" -d "${WORKDIR}/boot.cmd" boot.scr
}


do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 boot.scr ${DEPLOYDIR}/${UBOOTSCR_BASE_NAME}.scr
    ln -sf ${UBOOTSCR_BASE_NAME}.scr ${DEPLOYDIR}/boot.scr
}

addtask do_deploy after do_compile before do_build

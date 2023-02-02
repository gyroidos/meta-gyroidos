DESCRIPTION = "This is a wrapper for the user, group and hosts NSS API."
SECTION = "tools"
LICENSE = "GPL-3.0-only"
PR = "r0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=d32239bcb673463ab874e80d47fae504"

TAG := "${PV}"
SRC_URI = "git://git.samba.org/uid_wrapper.git;protocol=https;tag=uid_wrapper-${TAG};branch=master"
PV = "${TAG}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

DESCRIPTION = "This is a wrapper for the user, group and hosts NSS API."
SECTION = "tools"
LICENSE = "GPLv3"
PR = "r0"

LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://git.samba.org/uid_wrapper.git;protocol=https;tag=uid_wrapper-${PV}"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

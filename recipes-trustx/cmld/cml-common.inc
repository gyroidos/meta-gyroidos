LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "kirkstone"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

SRC_URI = "git://github.com/gyroidos/cml.git;branch=${BRANCH};protocol=https"

S = "${WORKDIR}/git"

INSANE_SKIP:${PN} = "ldflags"

do_configure () {
    :
}

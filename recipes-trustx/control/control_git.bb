LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "trustx-master"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

# upstream repository comment out for development and use local fork below
SRC_URI = "git://github.com/trustm3/device_fraunhofer_common_cml.git;branch=${BRANCH}"

# uncomment this an replay user/path to your local fork for development
#SRC_URI = "git:///home/<user>/device_fraunhofer_common_cml/;protocol=file;branch=wip"

S = "${WORKDIR}/git/control/"

INSANE_SKIP_${PN} = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text"


do_configure () {
    :
}

do_compile () {
    oe_runmake all
}

do_install () {
    install -d ${D}${sbindir}/
    install -m 0755 ${S}/control ${D}${sbindir}/
}

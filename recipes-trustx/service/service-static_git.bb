LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "dunfell"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

SRC_URI = "git://github.com/gyroidos/cml.git;branch=${BRANCH}"

S = "${WORKDIR}/git/"

INSANE_SKIP_${PN} = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text"

FILES_${PN} += "${base_sbindir}"
INHIBIT_PACKAGE_STRIP = "1"

do_configure () {
        :
}

do_compile () {
        oe_runmake -C service service-static
        oe_runmake -C service exec_cap_systime
}

do_install () {
        :
	install -d ${D}${base_sbindir}/
	install -m 0755 ${S}service/cml-service-container ${D}${base_sbindir}/
	install -m 0755 ${S}service/exec_cap_systime ${D}${base_sbindir}/
}

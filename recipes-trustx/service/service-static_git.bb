require recipes-trustx/cmld/cml-common.inc




DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text"

FILES:${PN} += "${base_sbindir}"
INHIBIT_PACKAGE_STRIP = "1"

do_compile () {
        oe_runmake -C service service-static
        oe_runmake -C service exec_cap_systime
}

do_install () {
        :
	install -d ${D}/${base_sbindir}/
	install -m 0755 ${S}/service/cml-service-container ${D}${base_sbindir}/
	install -m 0755 ${S}/service/exec_cap_systime ${D}${base_sbindir}/
}

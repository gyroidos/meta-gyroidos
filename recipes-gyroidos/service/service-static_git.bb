require recipes-gyroidos/cmld/cml-common.inc

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text"

FILES:${PN} += "${base_sbindir}"
INHIBIT_PACKAGE_STRIP = "1"

do_compile () {
        oe_runmake -C service service-static
}

do_install () {
        :
	install -d ${D}/${base_sbindir}/
	install -m 0755 ${B}/service/cml-service-container-static ${D}${base_sbindir}/
}

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "trustx-master"
SRCREV = "${AUTOREV}"
SRC_URI = "git://github.com/trustm3/device_fraunhofer_common_cml.git;branch=${BRANCH}"

S = "${WORKDIR}/git/"

INSANE_SKIP_${PN} = "ldflags"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append = " file://start_service.sh \
                   file://mini_init \
"


DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text"

FILES_${PN} += "${base_sbindir}"
INHIBIT_PACKAGE_STRIP = "1"


INITSCRIPT_PARAMS = "start 90 5 ."
INITSCRIPT_NAME = "start_service.sh"

inherit update-rc.d

CONFFILES_${PN} += "${sysconfdir}/init.d/start_service.sh"

do_configure () {
        :
}


do_compile () {
        oe_runmake -C service all
}

do_install () {
        :
	install -d ${D}${base_sbindir}/
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}service/cml-service-container ${D}${base_sbindir}/

	install -m 0755 ${WORKDIR}/mini_init ${D}${base_sbindir}/

	install -m 0755 ${WORKDIR}/start_service.sh ${D}${sysconfdir}/init.d/
	#update-rc.d -r ${D} start_service.sh start 90 5 .
	mkdir -p ${DEPLOY_DIR_IMAGE}/proto
	cp ${S}daemon/*.proto ${DEPLOY_DIR_IMAGE}/proto
}

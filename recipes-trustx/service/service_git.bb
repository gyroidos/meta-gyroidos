require recipes-trustx/cmld/cml-common.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = "\
	file://ssig_pki_generator.conf \
	file://openssl-dockerlocal-rootca.cnf \
	file://openssl-dockerlocal-subca.cnf \
	file://openssl-dockerlocal-ssig.cnf \
"

PACKAGES =+ "converter"

INSANE_SKIP:converter = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text libtar zlib openssl"

FILES:${PN} += "${base_sbindir}"
INHIBIT_PACKAGE_STRIP = "1"

SCRIPT_DIR = "${TOPDIR}/../trustme/build"
PROVISIONING_DIR = "${SCRIPT_DIR}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"


do_compile () {
        oe_runmake -C service all
        oe_runmake -C converter all
}

do_install () {
        :
	install -d ${D}/${base_sbindir}/
	install -m 0755 ${B}/service/cml-service-container ${D}/${base_sbindir}/
	install -d ${D}/${bindir}/
	install -m 0755 ${B}/converter/converter ${D}${bindir}/

	install -d ${D}/pki_generator
	install -d ${D}/pki_generator/config_creator
	install -m 0755 ${ENROLLMENT_DIR}/provisioning_lib.sh ${D}/pki_generator/
	install -m 0755 ${ENROLLMENT_DIR}/certificates/ssig_pki_generator.sh ${D}/pki_generator/
	install -m 0600 ${WORKDIR}/ssig_pki_generator.conf ${D}/pki_generator/
	install -m 0600 ${WORKDIR}/openssl-dockerlocal-rootca.cnf ${D}/pki_generator
	install -m 0600 ${WORKDIR}/openssl-dockerlocal-subca.cnf ${D}/pki_generator
	install -m 0600 ${WORKDIR}/openssl-dockerlocal-ssig.cnf ${D}/pki_generator

	mkdir -p ${DEPLOY_DIR_IMAGE}/proto
	cp ${S}/daemon/*.proto ${DEPLOY_DIR_IMAGE}/proto
}

RDEPENDS:converter += "bash openssl libtar zlib curl squashfs-tools libgcc"

FILES:converter = "\
	${bindir}/converter \
	pki_generator/* \
"

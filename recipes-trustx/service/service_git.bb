LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "dunfell"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

SRC_URI = "git://github.com/trustm3/device_fraunhofer_common_cml.git;branch=${BRANCH}"
SRC_URI_append = "\
	file://ssig_pki_generator.conf \
	file://openssl-dockerlocal-rootca.cnf \
	file://openssl-dockerlocal-subca.cnf \
	file://openssl-dockerlocal-ssig.cnf \
"

S = "${WORKDIR}/git/"

PACKAGES =+ "converter"

INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_converter = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text libtar zlib openssl"

FILES_${PN} += "${base_sbindir}"
INHIBIT_PACKAGE_STRIP = "1"

SCRIPT_DIR = "${TOPDIR}/../trustme/build"
PROVISIONING_DIR = "${SCRIPT_DIR}/device_provisioning"
ENROLLMENT_DIR = "${PROVISIONING_DIR}/oss_enrollment"

do_configure () {
        :
}


do_compile () {
        oe_runmake -C service all
        oe_runmake -C converter all
}

do_install () {
        :
	install -d ${D}${base_sbindir}/
	install -m 0755 ${S}service/cml-service-container ${D}${base_sbindir}/
	install -d ${D}${bindir}/
	install -m 0755 ${S}converter/converter ${D}${bindir}/

	install -d ${D}/pki_generator
	install -d ${D}/pki_generator/config_creator
	install -m 0755 ${ENROLLMENT_DIR}/provisioning_lib.sh ${D}/pki_generator/
	install -m 0755 ${ENROLLMENT_DIR}/certificates/ssig_pki_generator.sh ${D}/pki_generator/
	install -m 0600 ${WORKDIR}/ssig_pki_generator.conf ${D}/pki_generator/
	install -m 0600 ${WORKDIR}/openssl-dockerlocal-rootca.cnf ${D}/pki_generator
	install -m 0600 ${WORKDIR}/openssl-dockerlocal-subca.cnf ${D}/pki_generator
	install -m 0600 ${WORKDIR}/openssl-dockerlocal-ssig.cnf ${D}/pki_generator

	mkdir -p ${DEPLOY_DIR_IMAGE}/proto
	cp ${S}daemon/*.proto ${DEPLOY_DIR_IMAGE}/proto
}

RDEPENDS_converter += "bash openssl libtar zlib curl squashfs-tools libgcc"

FILES_converter = "\
	${bindir}/converter \
	pki_generator/* \
"

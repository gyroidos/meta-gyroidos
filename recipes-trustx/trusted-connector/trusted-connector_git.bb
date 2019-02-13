HOMEPAGE = "https://industrial-data-space.github.io/trusted-connector-documentation/"
SUMMARY = "Trusted Connector - A secure IoT gateway platform"
DESCRIPTION = "The Trusted Connector is an open IoT edge gateway platform and \
a reference implementation of the Industrial Data Space Connector. It is \
currently being standardized as DIN Spec 27070. \
Use the Trusted Connector to connect sensors with cloud services and other \
Connectors, using a vast range of protocol adapters. Security mechanisms like \
secure boot, remote platform attestation and data usage control allow you \
stay in control over your data and support you in GDPR compliance and \
auditing. The Trusted Connector is open source and built on open standards to \
avoid vendor lock-in. \
"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=86d3f3a95c324c9479bd8986968f4327"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

BRANCH = "develop"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

SRC_URI = "git://github.com/industrial-data-space/trusted-connector.git;branch=${BRANCH}"
SRC_URI_append = " file://start_connector.sh "

S = "${WORKDIR}/git"

RDEPENDS_${PN} += "openjre-8"

do_compile() {
	cd ${S}/ids-webconsole/src/main/resources/www
	yarn install
	cd ${S}
	sh gradlew clean install
}

do_install() {
  install -d ${D}/root/
  tar xvf ${S}/karaf-assembly/build/karaf-assembly-*.tar.gz --strip-components=1 -C ${D}/root
  install -d ${D}${sysconfdir}/init.d
  install -m 0755 ${WORKDIR}/start_connector.sh ${D}${sysconfdir}/init.d/
}


CONFFILES_${PN} += "${sysconfdir}/init.d/start_connector.sh"
FILES_${PN} += "/root/* "

inherit update-rc.d
INITSCRIPT_PARAMS = "start 90 5 ."
INITSCRIPT_NAME = "start_connector.sh"

INHIBIT_PACKAGE_STRIP = "1"

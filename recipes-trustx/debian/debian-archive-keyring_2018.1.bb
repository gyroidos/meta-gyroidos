SUMMARY = "Debian archive keyring"
DESCRIPTION = "Use the debian package for keyring"
LICENSE = "GPLv2"
RC_URI[md5sum] = "51cb7f8ee1686228cd570c9b748d19ec"
SRC_URI[sha256sum] = "3dc37cb40fb4fe69559096624295e3c6730658786f4e64349413c58d4f1d2927"

SRC_URI = "http://ftp.de.debian.org/debian/pool/main/d/${PN}/${PN}_${PV}_all.deb"

LIC_FILES_CHKSUM = "file://usr/share/doc/debian-archive-keyring/copyright;md5=3a39527c50bc61e28a31bd9c3de8e17f"

DEPENDS += "xz-native"

S = "${WORKDIR}"

inherit bin_package pkgconfig

do_install() {
	cp -r ${WORKDIR}/etc ${D} 
	cp -r ${WORKDIR}/usr ${D} 
}

FILES_${PN} = "/*"


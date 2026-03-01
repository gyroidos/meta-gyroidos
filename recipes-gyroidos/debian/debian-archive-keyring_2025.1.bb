SUMMARY = "Debian archive keyring"
DESCRIPTION = "Use the debian package for keyring"
LICENSE = "GPL-2.0-only"
SRC_URI[sha256sum] = "9ea7778e443144ca490668737a8ab22dd3e748bb99e805e22ec055abeb3c7fac"

SRC_URI = "http://ftp.debian.org/debian/pool/main/d/${PN}/${PN}_${PV}_all.deb"

LIC_FILES_CHKSUM = "file://usr/share/doc/debian-archive-keyring/copyright;md5=21eb0bd04f14237e900abd948226d14e"

DEPENDS += "xz-native"

S = "${WORKDIR}"

inherit bin_package pkgconfig

do_install() {
	cp -r ${WORKDIR}/etc ${D} 
	cp -r ${WORKDIR}/usr ${D} 
}

FILES:${PN} = "/*"


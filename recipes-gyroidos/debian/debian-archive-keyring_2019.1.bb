SUMMARY = "Debian archive keyring"
DESCRIPTION = "Use the debian package for keyring"
LICENSE = "GPL-2.0-only"
SRC_URI[md5sum] = "ca03ec9a7a146210df4923193520f79c"
SRC_URI[sha256sum] = "9cefd8917f3d97a999c136aa87f04a3024408b5bc1de470de7d6dfa5e4bd4361"

SRC_URI = "http://ftp.debian.org/debian/pool/main/d/${PN}/${PN}_${PV}_all.deb"

LIC_FILES_CHKSUM = "file://usr/share/doc/debian-archive-keyring/copyright;md5=3a39527c50bc61e28a31bd9c3de8e17f"

DEPENDS += "xz-native"

S = "${WORKDIR}"

inherit bin_package pkgconfig

do_install() {
	cp -r ${WORKDIR}/etc ${D} 
	cp -r ${WORKDIR}/usr ${D} 
}

FILES:${PN} = "/*"


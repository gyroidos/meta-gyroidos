SUMMARY = "LXCFS is a userspace filesystem created to avoid kernel limitations"
LICENSE = "LGPL-2.1-or-later"

inherit autotools pkgconfig

SRC_URI = " \
    https://linuxcontainers.org/downloads/lxcfs/lxcfs-${PV}.tar.gz \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=29ae50a788f33f663405488bc61eecb1"
SRC_URI[md5sum] = "a5ca8b71338099111171ecbacf2787ed"
SRC_URI[sha256sum] = "8bc4dbd3b0bd14379766993b4d5fa34c5331c0c2cc253c4d30797249f6a0096d"

DEPENDS += "fuse"
RDEPENDS:${PN} += "fuse"

CACHED_CONFIGUREVARS += "ac_cv_path_HELP2MAN='false // No help2man //'"
EXTRA_OECONF += "--with-distro=unknown"

do_install:append() {
        rm -r ${D}/usr/share/lxc
        install -d ${D}/var/lib/${PN}
}

FILES:${PN} += "/var/lib/${PN}"

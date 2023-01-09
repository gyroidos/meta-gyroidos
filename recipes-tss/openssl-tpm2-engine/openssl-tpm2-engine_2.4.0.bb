SUMMARY = "TPM2 engine and key creation tools for openssl"
DESCRIPTION = "This package contains 2 sets of code, a command-line utility used to \
generate a TSS key blob and write it to disk and an OpenSSL engine which \
interfaces with the TSS API.  Because this application uses the TPM \
cryptographically, it must be build with the IBM TSS."

HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/openssl_tpm2_engine.git/"
LICENSE = "LGPL-2.1-only"

DEPENDS = "openssl ibmtss2"

RDEPENDS:${PN} += "openssl libgcc"

EXTRA_OECONF = "--with-openssl=${RECIPE_SYSROOT}/usr"

#EXTRA_OEMAKE = "ENGINEDIR=/usr/lib/engines"

TAR_N = "openssl_tpm2_engine"

LIC_FILES_CHKSUM = "file://LICENSE;md5=a055871bc591288e6970672b3ff8736d"
SRC_URI = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/${TAR_N}.git/snapshot/${TAR_N}-${PV}.tar.gz \
	file://Cross-compile-compatible-enginesdir-variable_${PV}.patch \
	file://0001-Allow-to-compile-with-Werror-maybe-uninitialized_${PV}.patch \
"

S = "${WORKDIR}/${TAR_N}-${PV}"

SRC_URI[md5sum] = "b097a711b4081308a5e0114255f2c80c"
SRC_URI[sha256sum] = "7de7d214e869be99485f717b31fbff98c579064a8e8bbb2bacfce4e395407931"

inherit autotools pkgconfig

do_configure:prepend() {
	touch ${S}/NEWS
	touch ${S}/AUTHORS
	touch ${S}/ChangeLog
	cp ${S}/LICENSE ${S}/COPYING
}

do_install:append() {
	install -d ${D}${libdir}/engines-1.1
	mv ${D}${libdir}/engines/libtpm2.so ${D}${libdir}/engines-1.1/tpm2.so
	rm ${D}${libdir}/engines/tpm2.so
}

FILES:${PN} += "${libdir}/*"
FILES:${PN} += "${bindir}/*"

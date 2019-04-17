SUMMARY = "TPM2 engine and key creation tools for openssl"
DESCRIPTION = "This package contains 2 sets of code, a command-line utility used to \
generate a TSS key blob and write it to disk and an OpenSSL engine which \
interfaces with the TSS API.  Because this application uses the TPM \
cryptographically, it must be build with the IBM TSS."

HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/openssl_tpm2_engine.git/"
LICENSE = "LGPL-2.1"

DEPENDS = "openssl ibmtss2"

RDEPENDS_${PN} += "openssl libgcc"

EXTRA_OECONF = "--with-openssl=${RECIPE_SYSROOT}/usr"

#EXTRA_OEMAKE = "ENGINEDIR=/usr/lib/engines"

TAR_N = "openssl_tpm2_engine"

LIC_FILES_CHKSUM = "file://LICENSE;md5=a055871bc591288e6970672b3ff8736d"
SRC_URI = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/${TAR_N}.git/snapshot/${TAR_N}-${PV}.tar.gz \
	file://Cross-compile-compatible-enginesdir-variable.patch \
	file://dont-depend-on-help2man.patch"

S = "${WORKDIR}/${TAR_N}-${PV}"

SRC_URI[md5sum] = "09fbc28add9ceadc4752a5192bd368cb"
SRC_URI[sha256sum] = "b898b9901f9b50dabdbb08795b9a4b2736d8f71cb23ba13891bd0894f5614108"

inherit autotools pkgconfig

do_configure_prepend() {
	touch ${S}/NEWS
	touch ${S}/AUTHORS
	touch ${S}/ChangeLog
	cp ${S}/LICENSE ${S}/COPYING
}

FILES_${PN} += "${libdir}/*"
FILES_${PN} += "${bindir}/*"

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

TAR_N = "openssl_tpm2_engine"

LIC_FILES_CHKSUM = "file://LICENSE;md5=a055871bc591288e6970672b3ff8736d"
SRC_URI = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/${TAR_N}.git/snapshot/${TAR_N}-${PV}.tar.gz \
	file://Cross-compile-compatible-enginesdir-variable_${PV}.patch \
	file://Makefile.am-Use-src_topdir-instead-of-relative-inclu_${PV}.patch \
	file://src-provider-keymgmt-initialize-order-in-tpm2_keymgm_${PV}.patch \
	file://0001-fix-uninitialized-variables-reported-by-gcc_${PV}.patch \
"

S = "${WORKDIR}/${TAR_N}-${PV}"

EXTRA_OEMAKE = "-I ${S}/src/include"

SRC_URI[md5sum] = "587c08c6c75a7318f0f1d2faaa210385"
SRC_URI[sha256sum] = "42fcecaf40945ae9603b93bc1e63a48b62ac1b25f9b4375c3b67dac9d5bf1ea7"

inherit autotools pkgconfig

do_configure:prepend() {
	touch ${S}/NEWS
	touch ${S}/AUTHORS
	touch ${S}/ChangeLog
	cp ${S}/LICENSE ${S}/COPYING
}

do_install:append() {
	install -d ${D}${libdir}/engines-3.0
	mv ${D}${libdir}/engines/libtpm2.so ${D}${libdir}/engines-3.0/tpm2.so
	rm ${D}${libdir}/engines/tpm2.so
	rm -r ${D}${libdir}/ossl-modules
}

FILES:${PN} += "${libdir}/*"
FILES:${PN} += "${bindir}/*"

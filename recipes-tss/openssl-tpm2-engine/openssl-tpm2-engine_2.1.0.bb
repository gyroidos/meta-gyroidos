SUMMARY = "TPM2 engine and key creation tools for openssl"
DESCRIPTION = "This package contains 2 sets of code, a command-line utility used to \
generate a TSS key blob and write it to disk and an OpenSSL engine which \
interfaces with the TSS API.  Because this application uses the TPM \
cryptographically, it must be build with the IBM TSS."

HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/openssl_tpm2_engine.git/"
LICENSE = "LGPL-2.1-only"

DEPENDS = "openssl ibmtss2"

RDEPENDS_${PN} += "openssl libgcc"

EXTRA_OECONF = "--with-openssl=${RECIPE_SYSROOT}/usr"

TAR_N = "openssl_tpm2_engine"

#TODO update chcksm
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7810fab7487fb0aad327b76f1be7cd7"
SRC_URI = "https://git.kernel.org/pub/scm/linux/kernel/git/jejb/${TAR_N}.git/snapshot/${TAR_N}-${PV}.tar.gz"

S = "${WORKDIR}/${TAR_N}-${PV}"

#2.1.0
#SRC_URI[md5sum] = "09fbc28add9ceadc4752a5192bd368cb"
#SRC_URI[sha256sum] = "b898b9901f9b50dabdbb08795b9a4b2736d8f71cb23ba13891bd0894f5614108"
#1.1.0
SRC_URI[md5sum] = "dde855ad5293bb8b30b2c2c5aab7e64f"
SRC_URI[sha256sum] = "ea30e22ff2a7c108ab0900fbd514bad3f21c08f19a41b30ffa16c7b8a41ff01d"

inherit autotools pkgconfig

do_configure_prepend() {
	touch ${S}/NEWS
	touch ${S}/AUTHORS
	touch ${S}/ChangeLog
	cp ${S}/LICENSE ${S}/COPYING
}

#do_configure(){
#	oe_runconf 
#}

#do_compile(){
#	oe_runmake
#}
# 
#do_install(){
#	oe_runmake install
#}

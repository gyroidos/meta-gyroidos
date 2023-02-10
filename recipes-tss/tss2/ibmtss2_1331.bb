SUMMARY = "ibmtss2"
DESCRIPTION = "This is a user space TSS for TPM 2.0. It implements the functionality equivalent to (but not API compatible with) the TCG TSS working group's ESAPI, SAPI, and TCTI API's (and perhaps more) but with a hopefully simpler interface."
HOMEPAGE = "https://sourceforge.net/projects/ibmtpm20tss"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE;md5=1e023f61454ac828b4aa1bc4293f7d5f"

SRC_URI[md5sum] = "95068099d4a3aebd22632a0e8878b1d7"
SRC_URI[sha256sum] = "70ca492040f795c1471ccea57cf4afcb852860ebc2b135ef9606287fe27a231d"

SRC_URI = "${SOURCEFORGE_MIRROR}/ibmtpm20tss/ibmtss${PV}.tar.gz;downloadfilename=ibmtss-${PV}.tar.gz;subdir=${PN}"

DEPENDS += "openssl"

S = "${WORKDIR}/${PN}/utils"

INSANE_SKIP:${PN} = "ldflags"
INSANE_SKIP:${PN}-dev = "ldflags"

FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"

LIBRARY_FLAGS = "\
	-I. \
	-fPIC \
	-DTPM_TPM20 \
	-DTPM_INTERFACE_TYPE_DEFAULT=\"dev\" \
"

# overwrite compiler and compiler flags in makefile
EXTRA_OEMAKE = "\
	'--makefile=makefiletpm20' \
	'CC = ${CC}' \
	'CCLFLAGS = ${LIBRARY_FLAGS}' \
"

do_populate_lic:prepend() {
    bb.build.exec_func('do_copy_lic', d)
}

do_copy_lic() {
    cp ${WORKDIR}/${PN}/LICENSE ${S}/LICENSE
}

do_compile() {
	oe_runmake libibmtss.so libibmtssutils.so
	oe_runmake all
}


PREFIX = "tpm2"
do_install() {
	:
	install -d ${D}${libdir}/
	install -m 0755 ${S}/libibmtss.so ${D}${libdir}/
	install -m 0755 ${S}/libibmtssutils.so ${D}${libdir}/

	install -d ${D}${bindir}/
	install -m 0755 ${S}/getrandom ${D}${bindir}/${PREFIX}_getrandom
	#install -m 0755 ${S}/powerup ${D}${bindir}/${PREFIX}_powerup
	#install -m 0755 ${S}/startup ${D}${bindir}/${PREFIX}_startup
	#install -m 0755 ${S}/quote ${D}${bindir}/${PREFIX}_quote
	#install -m 0755 ${S}/unseal ${D}${bindir}/${PREFIX}_unseal
	#install -m 0755 ${S}/load ${D}${bindir}/${PREFIX}_seal
	install -m 0755 ${S}/clear ${D}${bindir}/${PREFIX}_clear
	#install -m 0755 ${S}/create ${D}${bindir}/${PREFIX}_create
	#install -m 0755 ${S}/createprimary ${D}${bindir}/${PREFIX}_createprimary
	#install -m 0755 ${S}/pcrextend ${D}${bindir}/${PREFIX}_pcrextend
	install -m 0755 ${S}/pcrread ${D}${bindir}/${PREFIX}_pcrread
	#install -m 0755 ${S}/sign ${D}${bindir}/${PREFIX}_sign
	#install -m 0755 ${S}/verifysignature ${D}${bindir}/${PREFIX}_verifiysignature

	install -d ${D}${includedir}/
	install -d ${D}${includedir}/ibmtss/
	install -m 0644 ${S}/ibmtss/*.h ${D}${includedir}/ibmtss/

	# create symlinks for tss library
	cd ${D}${libdir}
	ln -s libibmtssutils.so libibmtssutils.so.1
	ln -s libibmtssutils.so libibmtssutils.so.1.1
	ln -s libibmtss.so libibmtss.so.1
	ln -s libibmtss.so libibmtss.so.1.1
}

FILES:${PN} += "${libdir}/*.so"

FILES:${PN}-dev += "${includedir}"

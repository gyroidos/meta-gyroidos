LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "trustx-master"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

# upstream repository comment out for development and use local fork below
SRC_URI = "git://github.com/trustm3/device_fraunhofer_common_cml.git;branch=${BRANCH}"

# uncomment this an replay user/path to your local fork for development
#SRC_URI = "git:///home/<user>/device_fraunhofer_common_cml/;protocol=file;branch=wip"

S = "${WORKDIR}/git/"

PACKAGES =+ "scd tpm2d"

INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_scd = "ldflags"
INSANE_SKIP_tpm2d = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c libselinux protobuf-c-text libcap e2fsprogs openssl ibmtss2"

EXTRA_OEMAKE = "TRUSTME_HARDWARE=${TRUSTME_HARDWARE}"

do_configure () {
    :
}


do_compile () {
    oe_runmake -C daemon
    oe_runmake -C control
    oe_runmake -C run
    oe_runmake -C scd
    oe_runmake -C tpm2d
    oe_runmake -C tpm2_control
}

do_install () {
    install -d ${D}${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}daemon/cmld ${D}${sbindir}/
    install -m 0755 ${S}control/control ${D}${sbindir}/
    install -m 0755 ${S}run/run ${D}${sbindir}/
    install -m 0755 ${S}scd/scd ${D}${sbindir}/
    install -m 0755 ${S}tpm2d/tpm2d ${D}${sbindir}/
    install -m 0755 ${S}tpm2_control/tpm2_control ${D}${sbindir}/
    mkdir -p ${DEPLOY_DIR_IMAGE}/proto
    cp ${S}daemon/*.proto ${DEPLOY_DIR_IMAGE}/proto
}

RDEPENDS_scd += "cmld openssl"
RDEPENDS_tpm2d += "cmld ibmtss2"

FILES_scd = "${sbindir}/scd"
FILES_tpm2d = "${sbindir}/tpm2*"


LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "dunfell"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

# upstream repository comment out for development and use local fork below
SRC_URI = "git://github.com/trustm3/device_fraunhofer_common_cml.git;branch=${BRANCH};protocol=https"

# uncomment this an replay user/path to your local fork for development
#SRC_URI = "git:///home/<user>/device_fraunhofer_common_cml/;protocol=file;branch=wip"

S = "${WORKDIR}/git"

PACKAGES =+ "control scd tpm2d rattestation"

INSANE_SKIP:${PN} = "ldflags"
INSANE_SKIP:scd = "ldflags"
INSANE_SKIP:tpm2d = "ldflags"
INSANE_SKIP:control = "ldflags"
INSANE_SKIP:rattestation = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text e2fsprogs openssl ibmtss2 sc-hsm-embedded"

EXTRA_OEMAKE = "TRUSTME_HARDWARE=${TRUSTME_HARDWARE}"
EXTRA_OEMAKE += "TRUSTME_SCHSM=${TRUSTME_SCHSM}"
EXTRA_OEMAKE += "DEVELOPMENT_BUILD=${DEVELOPMENT_BUILD}"
EXTRA_OEMAKE += "CC_MODE=${CC_MODE}"

do_configure () {
    :
}

do_compile () {
    oe_runmake -C daemon
    oe_runmake -C control
    oe_runmake -C scd
    oe_runmake -C tpm2d
    oe_runmake -C tpm2_control
    oe_runmake -C rattestation
    oe_runmake -C common libcommon_full
}

do_install () {
    install -d ${D}/${sbindir}/
    install -d ${D}/${sysconfdir}/init.d
    install -m 0755 ${S}/daemon/cmld ${D}/${sbindir}/
    install -m 0755 ${S}/control/control ${D}/${sbindir}/
    install -m 0755 ${S}/scd/scd ${D}/${sbindir}/
    install -m 0755 ${S}/tpm2d/tpm2d ${D}/${sbindir}/
    install -m 0755 ${S}/tpm2_control/tpm2_control ${D}/${sbindir}/
    install -m 0755 ${S}/rattestation/rattestation ${D}/${sbindir}/
    install -d ${D}/${libdir}
    install -m 0755 ${S}/common/libcommon_full.a ${D}/${libdir}/
    install -d 0755 ${D}/${includedir}/common
    install -m 0644 ${S}/common/*.h ${D}/${includedir}/common
}

RDEPENDS:scd += "cmld openssl"
RDEPENDS:tpm2d += "cmld ibmtss2"
RDEPENDS:control += "protobuf-c protobuf-c-text"
RDEPENDS:rattestation += "openssl protobuf-c protobuf-c-text"

FILES:scd = "${sbindir}/scd"
FILES:control = "${sbindir}/control"
FILES:tpm2d = "${sbindir}/tpm2*"
FILES:rattestation = "${sbindir}/rattestation"


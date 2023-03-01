require recipes-trustx/cmld/cml-common.inc

PACKAGES =+ "control scd tpm2d rattestation"

INSANE_SKIP:scd = "ldflags"
INSANE_SKIP:tpm2d = "ldflags"
INSANE_SKIP:control = "ldflags"
INSANE_SKIP:rattestation = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text e2fsprogs openssl ibmtss2 sc-hsm-embedded"

EXTRA_OEMAKE = "TRUSTME_HARDWARE=${TRUSTME_HARDWARE}"
EXTRA_OEMAKE += "TRUSTME_SCHSM=${TRUSTME_SCHSM}"
EXTRA_OEMAKE += "DEVELOPMENT_BUILD=${DEVELOPMENT_BUILD}"
EXTRA_OEMAKE += "CC_MODE=${CC_MODE}"

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
    install -d ${D}/${includedir}/common
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


LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "dunfell"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

# upstream repository comment out for development and use local fork below
SRC_URI = "git://github.com/trustm3/device_fraunhofer_common_cml.git;branch=${BRANCH}"

# uncomment this an replay user/path to your local fork for development
#SRC_URI = "git:///home/<user>/device_fraunhofer_common_cml/;protocol=file;branch=wip"

S = "${WORKDIR}/git/"

PACKAGES =+ "control scd tpm2d rattestation"

INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_scd = "ldflags"
INSANE_SKIP_tpm2d = "ldflags"
INSANE_SKIP_control = "ldflags"
INSANE_SKIP_rattestation = "ldflags"

DEPENDS = "protobuf-c-native protobuf-c protobuf-c-text e2fsprogs openssl ibmtss2 sc-hsm-embedded rsync-native"

EXTRA_OEMAKE = "TRUSTME_HARDWARE=${TRUSTME_HARDWARE}"
EXTRA_OEMAKE += "TRUSTME_SCHSM=${TRUSTME_SCHSM}"
EXTRA_OEMAKE += "DEVELOPMENT_BUILD=${DEVELOPMENT_BUILD}"
EXTRA_OEMAKE += "CC_MODE=${CC_MODE}"

# Determine if a local checkout of the cml repo is available.
# If so, build using externalsrc.
# If not, build from git.
python () {
    cml_dir = d.getVar('TOPDIR', True) + "/../trustme/cml"
    if os.path.isdir(cml_dir):
        d.setVar('EXTERNALSRC', cml_dir)
        d.setVar('EXTERNALSRC_BUILD', cml_dir)
}
inherit externalsrc



do_configure () {
    :
}

do_compile () {
    if [ -n "${EXTERNALSRC}" ]; then
        rsync -lr --exclude="oe-logs" --exclude="oe-workdir" "${S}/" "${B}"
    fi

    oe_runmake -C daemon
    oe_runmake -C control
    oe_runmake -C scd
    oe_runmake -C tpm2d
    oe_runmake -C tpm2_control
    oe_runmake -C rattestation
    oe_runmake -C common libcommon_full
}

do_install () {
    install -d ${D}${sbindir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${B}/daemon/cmld ${D}${sbindir}/
    install -m 0755 ${B}/control/control ${D}${sbindir}/
    install -m 0755 ${B}/scd/scd ${D}${sbindir}/
    install -m 0755 ${B}/tpm2d/tpm2d ${D}${sbindir}/
    install -m 0755 ${B}/tpm2_control/tpm2_control ${D}${sbindir}/
    install -m 0755 ${B}/rattestation/rattestation ${D}${sbindir}/
    install -d ${D}${libdir}
    install -m 0755 ${B}/common/libcommon_full.a ${D}${libdir}/
    install -d 0755 ${D}${includedir}/common
    install -m 0644 ${B}/common/*.h ${D}${includedir}/common

    install -d ${D}/${includedir}/proto
    install -m 0644 ${B}/daemon/*.proto ${D}${includedir}/proto
    if [ "y" = "${CC_MODE}" ]; then
        # if building cc_mode override files with respective cc_mode version
        install -m 0644 ${S}/daemon/cc_mode/*.proto ${D}${includedir}/proto
    fi
}

RDEPENDS_scd += "cmld openssl"
RDEPENDS_tpm2d += "cmld ibmtss2"
RDEPENDS_control += "protobuf-c protobuf-c-text"
RDEPENDS_rattestation += "openssl protobuf-c protobuf-c-text"

FILES_scd = "${sbindir}/scd"
FILES_control = "${sbindir}/control"
FILES_tpm2d = "${sbindir}/tpm2*"
FILES_rattestation = "${sbindir}/rattestation"


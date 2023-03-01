LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

BRANCH = "dunfell"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

SRC_URI = "git://github.com/gyroidos/cml.git;branch=${BRANCH};protocol=https"

S = "${WORKDIR}/git"

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

INSANE_SKIP:${PN} = "ldflags"

do_configure () {
    :
}

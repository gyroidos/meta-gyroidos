LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

CML_BRANCH ?= "main"
SRCREV = "${AUTOREV}"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

CML_HOST ?= "github.com/gyroidos"

SRC_URI = "git://${CML_HOST}/cml.git;branch=${CML_BRANCH};protocol=https"

S = "${WORKDIR}/git"

# enable buildhistory for this recipe to allow SRCREV extraction
inherit buildhistory
BUILDHISTORY_COMMIT = "0"

DEPENDS:append = " rsync-native"

# Determine if a local checkout of the cml repo is available.
# If so, build using externalsrc.
# If not, build from git.
python () {
    cml_dir = d.getVar('TOPDIR', True) + "/../gyroidos/cml"
    if os.path.isdir(cml_dir):
        d.setVar('EXTERNALSRC', cml_dir)
}
inherit externalsrc

INSANE_SKIP:${PN} = "ldflags"

do_configure () {
    :
}

# The poor man's out-of-source build
# We require cmld and service* to be built using dfferent variants of libc
# for container and host builds (e.g. glibc and musl). Build in-source would
# poison the source directory with artifacts compiled with another libc, thus
# we need to keep the source directory clean and copy the sources to the build
# directory first.
do_compile:prepend () {
    if [ -n "${EXTERNALSRC}" ]; then
        rsync -lr --exclude="oe-logs" --exclude="oe-workdir" "${S}/" "${B}"
    fi
}

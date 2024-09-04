include linux-gyroidos.inc

LINUX_VERSION_EXTENSION = "-gyroidos"

# enable buildhistory for this recipe to allow SRCREV extraction
inherit buildhistory
BUILDHISTORY_COMMIT = "0"

SRC_URI += "\
        file://0001-ipvs-allow-netlink-configuration-from-non-initial-us.patch \
        file://0001-kbuild-remove-recent-dependency-on-truncate-program.patch \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

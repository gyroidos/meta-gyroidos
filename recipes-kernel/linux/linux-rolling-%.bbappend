include linux-gyroidos.inc

LINUX_VERSION_EXTENSION = "-gyroidos"

# enable buildhistory for this recipe to allow SRCREV extraction
inherit buildhistory
BUILDHISTORY_COMMIT = "0"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
KERNEL_FEATURES:remove = "cfg/efi.scc"

SRC_URI += "file://0001-shiftfs-uid-gid-shifting-bind-mount.patch \
            file://0002-shiftfs-rework-and-extend.patch \
            file://0003-shiftfs-support-some-btrfs-ioctls.patch \
            file://0001-shiftfs-allow-mounting-of-other-shiftfs-on-shiftfs.patch \
            "

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-vanilla:"
FILESEXTRAPATHS_prepend := "${THISDIR}/linux-debian:"


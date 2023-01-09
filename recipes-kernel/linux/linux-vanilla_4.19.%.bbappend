SRC_URI += "file://4.19/0001-shiftfs-uid-gid-shifting-bind-mount.patch \
            file://4.19/0002-shiftfs-rework-and-extend.patch \
            file://4.19/0003-shiftfs-support-some-btrfs-ioctls.patch \
            file://4.19/0004-UBUNTU-SAUCE-shiftfs-use-translated-ids-when-chaning.patch \
            file://4.19/0005-UBUNTU-SAUCE-shiftfs-fix-passing-of-attrs-to-underal.patch \
            file://4.19/0006-UBUNTU-SAUCE-shiftfs-prevent-use-after-free-when-ver.patch \
            file://4.19/0007-UBUNTU-SAUCE-shiftfs-use-separate-llseek-method-for-.patch \
            file://4.19/0008-UBUNTU-SAUCE-shiftfs-lock-down-certain-superblock-fl.patch \
            file://4.19/0009-UBUNTU-SAUCE-shiftfs-allow-changing-ro-rw-for-subvol.patch \
            file://4.19/0010-UBUNTU-SAUCE-shiftfs-enable-overlayfs-on-shiftfs.patch \
            file://4.19/0011-UBUNTU-SAUCE-shiftfs-add-O_DIRECT-support.patch \
            file://4.19/0012-UBUNTU-SAUCE-shiftfs-pass-correct-point-down.patch \
            file://4.19/0013-UBUNTU-SAUCE-Revert-UBUNTU-SAUCE-shiftfs-enable-over.patch \
            file://4.19/0014-UBUNTU-SAUCE-shiftfs-mark-slab-objects-SLAB_RECLAIM_.patch \
            file://4.19/0015-UBUNTU-SAUCE-shiftfs-fix-buggy-unlink-logic.patch \
            file://4.19/0016-UBUNTU-SAUCE-shiftfs-Fix-refcount-underflow-in-btrfs.patch \
            file://4.19/0017-UBUNTU-SAUCE-shiftfs-prevent-type-confusion.patch \
            file://4.19/0018-UBUNTU-SAUCE-shiftfs-Correct-id-translation-for-lowe.patch \
            file://4.19/0019-UBUNTU-SAUCE-shiftfs-Restore-vm_file-value-when-lowe.patch \
            file://4.19/0020-UBUNTU-SAUCE-shiftfs-setup-correct-s_maxbytes-limit.patch \
            file://4.19/0021-UBUNTU-SAUCE-shiftfs-drop-CAP_SYS_RESOURCE-from-effe.patch \
            file://4.19/0022-UBUNTU-SAUCE-shiftfs-prevent-lower-dentries-from-goi.patch \
            file://4.19/0023-shiftfs-backport-to-v4.19.patch \
            file://0001-shiftfs-allow-mounting-of-other-shiftfs-on-shiftfs.patch \
            "

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


SRC_URI += "\
	file://0001-UBUNTU-SAUCE-shiftfs-uid-gid-shifting-bind-mount.patch \
	file://0002-UBUNTU-SAUCE-shiftfs-rework-and-extend.patch \
	file://0003-UBUNTU-SAUCE-shiftfs-support-some-btrfs-ioctls.patch \
	file://0004-UBUNTU-SAUCE-shiftfs-use-translated-ids-when-chaning.patch \
	file://0005-UBUNTU-SAUCE-shiftfs-fix-passing-of-attrs-to-underal.patch \
	file://0006-UBUNTU-SAUCE-shiftfs-prevent-use-after-free-when-ver.patch \
	file://0007-UBUNTU-SAUCE-shiftfs-use-separate-llseek-method-for-.patch \
	file://0008-UBUNTU-SAUCE-shiftfs-lock-down-certain-superblock-fl.patch \
	file://0009-UBUNTU-SAUCE-shiftfs-allow-changing-ro-rw-for-subvol.patch \
	file://0010-UBUNTU-SAUCE-shiftfs-add-O_DIRECT-support.patch \
	file://0011-UBUNTU-SAUCE-shiftfs-pass-correct-point-down.patch \
	file://0012-UBUNTU-SAUCE-shiftfs-fix-buggy-unlink-logic.patch \
	file://0013-UBUNTU-SAUCE-shiftfs-mark-slab-objects-SLAB_RECLAIM_.patch \
	file://0014-UBUNTU-SAUCE-shiftfs-rework-how-shiftfs-opens-files.patch \
	file://0015-UBUNTU-SAUCE-shiftfs-Restore-vm_file-value-when-lowe.patch \
	file://0016-UBUNTU-SAUCE-shiftfs-setup-correct-s_maxbytes-limit.patch \
	file://0017-UBUNTU-SAUCE-shiftfs-drop-CAP_SYS_RESOURCE-from-effe.patch \
	file://0018-UBUNTU-SAUCE-shiftfs-Fix-refcount-underflow-in-btrfs.patch \
	file://0019-UBUNTU-SAUCE-shiftfs-prevent-type-confusion.patch \
	file://0020-UBUNTU-SAUCE-shiftfs-Correct-id-translation-for-lowe.patch \
	file://0021-UBUNTU-SAUCE-shiftfs-prevent-lower-dentries-from-goi.patch \
	file://0001-shiftfs-allow-mounting-of-other-shiftfs-on-shiftfs.patch \
	file://0001-l2tp-Allow-management-of-tunnels-and-session-in-user.patch \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

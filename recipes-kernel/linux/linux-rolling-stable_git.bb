inherit kernel
require recipes-kernel/linux/linux-yocto.inc

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git;protocol=git;name=machine;branch=linux-rolling-stable"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

LINUX_VERSION_EXTENSION = "-stable"

KERNEL_VERSION_SANITY_SKIP="1"

SRCREV_machine ?= "${AUTOREV}"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-vanilla:"
SRC_URI += "\
	file://shiftfs-v6.2/0001-UBUNTU-SAUCE-shiftfs-uid-gid-shifting-bind-mount.patch \
	file://shiftfs-v6.2/0002-UBUNTU-SAUCE-shiftfs-rework-and-extend.patch \
	file://shiftfs-v6.2/0003-UBUNTU-SAUCE-shiftfs-support-some-btrfs-ioctls.patch \
	file://shiftfs-v6.2/0004-UBUNTU-SAUCE-shiftfs-use-translated-ids-when-chaning.patch \
	file://shiftfs-v6.2/0005-UBUNTU-SAUCE-shiftfs-fix-passing-of-attrs-to-underal.patch \
	file://shiftfs-v6.2/0006-UBUNTU-SAUCE-shiftfs-prevent-use-after-free-when-ver.patch \
	file://shiftfs-v6.2/0007-UBUNTU-SAUCE-shiftfs-use-separate-llseek-method-for-.patch \
	file://shiftfs-v6.2/0008-UBUNTU-SAUCE-shiftfs-lock-down-certain-superblock-fl.patch \
	file://shiftfs-v6.2/0009-UBUNTU-SAUCE-shiftfs-allow-changing-ro-rw-for-subvol.patch \
	file://shiftfs-v6.2/0010-UBUNTU-SAUCE-shiftfs-add-O_DIRECT-support.patch \
	file://shiftfs-v6.2/0011-UBUNTU-SAUCE-shiftfs-pass-correct-point-down.patch \
	file://shiftfs-v6.2/0012-UBUNTU-SAUCE-shiftfs-fix-buggy-unlink-logic.patch \
	file://shiftfs-v6.2/0013-UBUNTU-SAUCE-shiftfs-mark-slab-objects-SLAB_RECLAIM_.patch \
	file://shiftfs-v6.2/0014-UBUNTU-SAUCE-shiftfs-rework-how-shiftfs-opens-files.patch \
	file://shiftfs-v6.2/0015-UBUNTU-SAUCE-shiftfs-Restore-vm_file-value-when-lowe.patch \
	file://shiftfs-v6.2/0016-UBUNTU-SAUCE-shiftfs-setup-correct-s_maxbytes-limit.patch \
	file://shiftfs-v6.2/0017-UBUNTU-SAUCE-shiftfs-drop-CAP_SYS_RESOURCE-from-effe.patch \
	file://shiftfs-v6.2/0018-UBUNTU-SAUCE-shiftfs-Fix-refcount-underflow-in-btrfs.patch \
	file://shiftfs-v6.2/0019-UBUNTU-SAUCE-shiftfs-prevent-type-confusion.patch \
	file://shiftfs-v6.2/0020-UBUNTU-SAUCE-shiftfs-Correct-id-translation-for-lowe.patch \
	file://shiftfs-v6.2/0021-UBUNTU-SAUCE-shiftfs-prevent-lower-dentries-from-goi.patch \
	file://shiftfs-v6.2/0022-UBUNTU-SAUCE-shiftfs-record-correct-creator-credenti.patch \
	file://shiftfs-v6.2/0023-UBUNTU-SAUCE-shiftfs-let-userns-root-destroy-subvolu.patch \
	file://shiftfs-v6.2/0024-UBUNTU-SAUCE-shiftfs-Fix-build-errors-from-missing-f.patch \
	file://shiftfs-v6.2/0025-UBUNTU-SAUCE-shiftfs-prevent-ESTALE-for-LOOKUP_JUMP-.patch \
	file://shiftfs-v6.2/0026-UBUNTU-SAUCE-shiftfs-fix-build-error-with-5.11.patch \
	file://shiftfs-v6.2/0027-UBUNTU-SAUCE-shiftfs-free-allocated-memory-in-shiftf.patch \
	file://shiftfs-v6.2/0028-UBUNTU-SAUCE-shiftfs-handle-copy_to_user-return-valu.patch \
	file://shiftfs-v6.2/0029-UBUNTU-SAUCE-shiftfs-fix-sendfile-invocations.patch \
	file://shiftfs-v6.2/0030-UBUNTU-SAUCE-shiftfs-support-kernel-5.15.patch \
	file://shiftfs-v6.2/0031-UBUNTU-SAUCE-shiftfs-always-rely-on-init_user_ns.patch \
	file://shiftfs-v6.2/0032-UBUNTU-SAUCE-shiftfs-fix-missing-include-required-in.patch \
	file://shiftfs-v6.2/0033-UBUNTU-SAUCE-shiftfs-support-kernel-6.1.patch \
	file://shiftfs-v6.2/0034-UBUNTU-SAUCE-shiftfs-support-linux-6.2.patch \
	file://shiftfs-v6.2/0035-UBUNTU-SAUCE-shiftfs-fix-EOVERFLOW-inside-the-contai.patch \
	file://0001-shiftfs-allow-mounting-of-other-shiftfs-on-shiftfs.patch \
"

PVBASE := "${PV}"
PV = "${PVBASE}+${SRCPV}"

DEPENDS += "elfutils-native"

COMPATIBLE_MACHINE = "${MACHINE}"

KBUILD_DEFCONFIG_genericx86-64 = "x86_64_defconfig"
KCONFIG_MODE="--alldefconfig"

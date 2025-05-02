FILESEXTRAPATHS:prepend := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'gyroidos-cml-initramfs' ], "${THISDIR}/${PN}:", "",d)}'

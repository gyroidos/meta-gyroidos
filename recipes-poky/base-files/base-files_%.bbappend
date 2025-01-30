FILESEXTRAPATHS:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'gyroidos-cml-initramfs' ], ":${THISDIR}/${PN}", "",d)}'

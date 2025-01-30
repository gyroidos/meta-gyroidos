FILESEXTRAPATHS:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'gyroidos-cml-initramfs' ], ":${THISDIR}/${PN}", "",d)}'

SRC_URI_:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'gyroidos-cml-initramfs' ], "file://installer.cfg", "",d)}'
SRC_URI_:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'gyroidos-cml-initramfs' ], "file://static.cfg", "",d)}'

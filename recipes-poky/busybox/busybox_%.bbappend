FILESEXTRAPATHS:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'trustx-cml-initramfs' ], ":${THISDIR}/${PN}", "",d)}'

SRC_URI_:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'trustx-cml-initramfs' ], "file://installer.cfg", "",d)}'
SRC_URI_:append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'trustx-cml-initramfs' ], "file://static.cfg", "",d)}'

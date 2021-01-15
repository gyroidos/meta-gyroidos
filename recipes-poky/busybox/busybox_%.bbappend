FILESEXTRAPATHS_append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'trustx-cml-initramfs' ], ":${THISDIR}/${PN}", "",d)}'

SRC_URI_append = " file://installer.cfg "
SRC_URI_append = " file://static.cfg "

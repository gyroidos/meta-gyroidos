FILESEXTRAPATHS_append := '${@bb.utils.contains("INITRAMFS_IMAGE", [ 'trustx-cml-initramfs' ], ":${THISDIR}/${PN}", "",d)}'

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI_TMP := "${SRC_URI}"
SRC_URI += "${@bb.utils.contains(SRC_URI_TMP, "file://0001-pv-display-handle-error-of-tcgetpgrp-in-pv_in_foregr.patch", "file://0001-pv-display-handle-error-of-tcgetpgrp-in-pv_in_foregr.patch", "", d)}"

SRC_URI_append = "\
	file://0001-l2tp-Allow-management-of-tunnels-and-session-in-user.patch \
	file://lockdown.cfg \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-vanilla:"
FILESEXTRAPATHS_prepend := "${THISDIR}/linux-debian:"


SRC_URI += " \
	file://0001-net-add-uevent-socket-member.patch \
	file://0002-netns-send-uevent-messages.patch \
"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"


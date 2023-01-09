SRC_URI += "\
	file://5.10/0001-dm-introduce-audit-event-module-for-device-mapper.patch \
	file://5.10/0002-dm-integrity-log-audit-events-for-dm-integrity-targe.patch \
	file://5.10/0003-dm-crypt-log-aead-integrity-violations-to-audit-subs.patch \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

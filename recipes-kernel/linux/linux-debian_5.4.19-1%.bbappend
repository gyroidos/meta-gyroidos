SRC_URI:append = "\
	file://5.4/0001-dm-report-suspended-device-during-destroy.patch \
	file://0001-l2tp-Allow-management-of-tunnels-and-session-in-user.patch \
	file://5.4/0001-audit-Add-__rcu-annotation-to-RCU-pointer.patch \
	file://5.4/0002-audit-collect-audit-task-parameters.patch \
	file://5.4/0003-audit-add-container-id.patch \
	file://5.4/0004-audit-read-container-ID-of-a-process.patch \
	file://5.4/0005-audit-convert-to-contid-list-to-check-for-orch-engin.patch \
	file://5.4/0006-audit-log-drop-of-contid-on-exit-of-last-task.patch \
	file://5.4/0007-audit-log-container-info-of-syscalls.patch \
	file://5.4/0008-audit-add-contid-support-for-signalling-the-audit-da.patch \
	file://5.4/0009-audit-add-support-for-non-syscall-auxiliary-records.patch \
	file://5.4/0010-audit-add-containerid-support-for-user-records.patch \
	file://5.4/0011-audit-add-containerid-filtering.patch \
	file://5.4/0012-audit-add-support-for-containerid-to-network-namespa.patch \
	file://5.4/0013-audit-contid-check-descendancy-and-nesting.patch \
	file://5.4/0014-audit-track-container-nesting.patch \
	file://5.4/0015-audit-check-contid-depth-and-add-limit-config-param.patch \
	file://5.4/0016-audit-check-contid-count-per-netns-and-add-config-pa.patch \
	file://5.4/0017-audit-add-capcontid-to-set-contid-outside-init_user_.patch \
	file://lockdown.cfg \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-vanilla:"
FILESEXTRAPATHS:prepend := "${THISDIR}/linux-debian:"


LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${TOPDIR}/../gyroidos/build/COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit deploy

SSTATE_SKIP_CREATION = "1"

do_compile() {
	ssh-keygen -f "${B}/id_ed25519" -t ed25519 -N ""
	mkdir -p "${B}/etc/ssh"
	ssh-keygen -A -f "${B}"
}
addtask do_compile after do_configure

do_deploy() {
	mkdir -p "${DEPLOYDIR}/ssh-keys"
	cp "${B}/etc/ssh/ssh_host_ed25519_key" "${DEPLOYDIR}/ssh-keys"
	cp "${B}/etc/ssh/ssh_host_ed25519_key.pub" "${DEPLOYDIR}/ssh-keys"
	cp "${B}/id_ed25519" "${DEPLOYDIR}/ssh-keys"
	cp "${B}/id_ed25519.pub" "${DEPLOYDIR}/ssh-keys"
}
addtask do_deploy after do_compile before do_install

do_install() {
	mkdir -p "${D}/etc/ssh"
	cp "${B}/etc/ssh/ssh_host_ed25519_key" "${D}/etc/ssh"
	cp "${B}/etc/ssh/ssh_host_ed25519_key.pub" "${D}/etc/ssh"
	cp "${B}/id_ed25519.pub" "${D}/etc/ssh/authorized_keys"
}
addtask do_install after do_deploy before do_package

FILES:${PN} = "/etc/ssh/*"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://60-persistent-storage.rules"

do_install:append() {
        install -m 0644 ${WORKDIR}/60-persistent-storage.rules ${D}${sysconfdir}/udev/rules.d/60-persistent-storage.rules
}

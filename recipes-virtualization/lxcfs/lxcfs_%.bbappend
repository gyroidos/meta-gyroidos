
SRC_URI:remove = "file://0001-bindings-fix-build-with-newer-linux-libc-headers.patch"
SRC_URI:remove = "file://0001-meson.build-force-pid-open-send_signal-detection.patch"

REQUIRED_DISTRO_FEATURES:remove = "systemd"
DEPENDS:remove = "systemd"

EXTRA_OEMESON:remove = "-Dinit-script=${VIRTUAL-RUNTIME_init_manager}"
EXTRA_OEMESON:append = "-Dinit-script=sysvinit"

do_install:append() {
        rm -r ${D}/usr/share/lxc
        install -d ${D}/var/lib/${PN}
}

FILES:${PN} += "/var/lib/${PN}"

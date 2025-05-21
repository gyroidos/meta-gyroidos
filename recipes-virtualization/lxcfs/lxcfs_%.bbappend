
REQUIRED_DISTRO_FEATURES:remove = "systemd"
DEPENDS:remove = "systemd"

EXTRA_OEMESON:remove = "-Dinit-script=${VIRTUAL-RUNTIME_init_manager}"
EXTRA_OEMESON:append = "-Dinit-script=sysvinit"

do_install:append() {
        rm -r ${D}/usr/share/lxc
        install -d ${D}/var/lib/${PN}
}

FILES:${PN} += "/var/lib/${PN}"

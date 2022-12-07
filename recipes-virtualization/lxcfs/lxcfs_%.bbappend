
EXTRA_OECONF:remove = "--with-init-script=${VIRTUAL-RUNTIME_init_manager}"

do_install:append() {
        rm -r ${D}/usr/share/lxc
        install -d ${D}/var/lib/${PN}
}

FILES:${PN} += "/var/lib/${PN}"

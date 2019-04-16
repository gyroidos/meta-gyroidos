do_compile() {
    oe_runmake sgdisk
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 sgdisk ${D}${sbindir}
}

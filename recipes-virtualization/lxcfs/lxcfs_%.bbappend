# This bbappend is designed to handle lxcfs versions from yocto versions kirkstone and scarthgap.
# Fortunately, applying patches in SRC_URI is the only operation that must only be done for one version,
# other settings can be done unconditionally and will be ignored by the incompatible version.

# configuration common for all yocto versions
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# configuration for kirkstone PV==4.0.7
EXTRA_OECONF:remove = "--with-init-script=${VIRTUAL-RUNTIME_init_manager}"

# configuration for scarthgap PV==5.0.4
SRC_URI += '${@bb.utils.contains("PV", "5.0.4", "file://0001-meson.build-Do-not-link-using-ld.gold.patch", "", d)}'

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

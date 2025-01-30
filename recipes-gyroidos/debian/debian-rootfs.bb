SUMMARY = "Debian rootfs"
DESCRIPTION = "This package installs a debian skeleton rootfs using debootstrap"
HOMEPAGE = ""
LICENSE = "GPL-2.0-only"


HOSTTOOLS += "debootstrap gpgv"

# Debian uses different names for certain arches.
def get_deb_arch(d):
    import bb

    deb_arch = d.getVar('TRANSLATED_TARGET_ARCH', True)
    if deb_arch == "aarch64":
        deb_arch = "arm64"
    if deb_arch == "x86-64":
        deb_arch = "amd64"
    elif deb_arch == "powerpc":
        deb_arch = "ppc"
    elif deb_arch == "powerpc64":
        deb_arch = "ppc64el"
    elif (deb_arch == "i486" or deb_arch == "i586" or deb_arch == "i686"):
        deb_arch = "i386"

    return deb_arch

DEB_ARCH = "${@get_deb_arch(d)}"

do_install() {
  /usr/sbin/debootstrap --arch ${DEB_ARCH} stable ${D}
}

FILES:${PN} = "/*"


# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   COPYING
SUMMARY = "Light-weight PKCS#11 library for using the SmartCard-HSM"
DESCRIPTION = "This module has been initially developed to support the integration of a SmartCard-HSM in embedded systems with a little footprint. Rather than using a PC/SC daemon to manage attached card readers and token, the smaller Card Terminal API (CT-API) can be used."
HOMEPAGE = "https://github.com/CardContact/sc-hsm-embedded"
# SECTION = ""
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=55b854a477953696452f698a3af5de1c"

SRC_URI = "git://git@github.com/ceppleaisec/sc-hsm-embedded.git;protocol=ssh;branch=cardservice_dev"

# Modify these as desired
PV = "hsm+git${SRCPV}"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

# force *.so into main package (see https://www.yoctoproject.org/pipermail/yocto/2017-August/037581.html)
# to resolve "do_package_qa: QA Issue: -dev package contains non-symlink .so" error
# FIXME: find proper solution
FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*.so"

# NOTE: unable to map the following pkg-config dependencies: libcurl) libpcsclite libusb-1.0
#       (this is based on recipes that have previously been built and packaged)
DEPENDS = "openssl librepo libusb1"

# NOTE: if this software is not capable of being built in a separate build directory
# from the source, you should replace autotools with autotools-brokensep in the
# inherit line
inherit pkgconfig autotools

# Specify any options you want to pass to the configure script using EXTRA_OECONF:
EXTRA_OECONF = "--enable-ctapi --enable-debug"

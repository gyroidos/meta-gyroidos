DESCRIPTION = "IMA/EVM control utility"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://LICENSES.txt;md5=0c60c9dd30223ddb83b533418bfd3f30"

DEPENDS += "openssl attr keyutils"

DEPENDS:class-native += "openssl-native keyutils-native"

SRC_URI = "https://github.com/linux-integrity/ima-evm-utils/releases/download/v${PV}/ima-evm-utils-${PV}.tar.gz;downloadfilename=ima-evm-utils-${PV}.tar.gz"

SRC_URI[md5sum] = "0bd63f1df6a571b1c959d1aac1d05c61"
SRC_URI[sha256sum] = "3d09e77a09e985b285891f13d458653f44913879147679dce30bdbf00380d40e"

## Documentation depends on asciidoc, which we do not have, so
## do not build documentation.
#SRC_URI += "file://disable-doc-creation.patch"

## Workaround for upstream incompatibility with older Linux distros.
## Relevant for us when compiling ima-evm-utils-native.
#SRC_URI += "file://evmctl.c-do-not-depend-on-xattr.h-with-IMA-defines.patch"
#
## Required for xargs with more than one path as argument (better for performance).
#SRC_URI += "file://command-line-apply-operation-to-all-paths.patch"

#S = "${WORKDIR}/git"

#inherit pkgconfig autotools features_check
inherit pkgconfig autotools

#REQUIRED_DISTRO_FEATURES = "ima"

EXTRA_OECONF:append:class-target = " --with-kernel-headers=${STAGING_KERNEL_BUILDDIR}"

# blkid is called by evmctl when creating evm checksums.
# This is less useful when signing files on the build host,
# so disable it when compiling on the host.
RDEPENDS:${PN}:append:class-target = " util-linux-blkid libcrypto attr libattr keyutils"

BBCLASSEXTEND = "native nativesdk"

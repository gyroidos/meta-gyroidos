DESCRIPTION = "IMA/EVM control utility"
LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "openssl attr keyutils"

DEPENDS:class-native += "openssl-native keyutils-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/linux-ima/ima-evm-utils/ima-evm-utils-${PV}.tar.gz;downloadfilename=ima-evm-utils-${PV}.tar.gz"

SRC_URI[md5sum] = "55cc0e2c77a725f722833c3b4a36038c"
SRC_URI[sha256sum] = "c2b206e7f9fbe62a938b7ae59e31906fefae4d5351fe01db739bd8346b75d4a7"

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

SUMMARY = "Protocol Buffers - structured data serialisation mechanism"
DESCRIPTION = "This is protobuf-c, a C implementation of the Google Protocol Buffers data \
serialization format. It includes libprotobuf-c, a pure C library that \
implements protobuf encoding and decoding, and protoc-c, a code generator that \
converts Protocol Buffer .proto files to C descriptor code, based on the \
original protoc. protobuf-c formerly included an RPC implementation; that code \
has been split out into the protobuf-c-rpc project."
HOMEPAGE = "https://github.com/protobuf-c/protobuf-c"
SECTION = "console/tools"
LICENSE = "BSD-2-Clause"

DEPENDS = "protobuf-native protobuf"

PACKAGE_BEFORE_PN = "${PN}-compiler"
RDEPENDS:${PN}-compiler = "protobuf-compiler"
RDEPENDS:${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE;md5=cb901168715f4782a2b06c3ddaefa558"
SRC_URI = "https://github.com/protobuf-c/protobuf-c/releases/download/v${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "dabc05a5f11c21b96d8d6db4153f5343"
SRC_URI[sha256sum] = "22956606ef50c60de1fabc13a78fbc50830a0447d780467d3c519f84ad527e78"

inherit autotools pkgconfig

EXTRA_OECONF:append = "--enable-static=yes"

FILES:${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"

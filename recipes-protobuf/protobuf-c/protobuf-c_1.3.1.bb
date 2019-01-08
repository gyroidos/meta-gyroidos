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
RDEPENDS_${PN}-compiler = "protobuf-compiler"
RDEPENDS_${PN}-dev += "${PN}-compiler"

LIC_FILES_CHKSUM = "file://LICENSE;md5=cb901168715f4782a2b06c3ddaefa558"
SRC_URI = "https://github.com/protobuf-c/protobuf-c/archive/v${PV}.tar.gz"

SRC_URI[md5sum] = "ab3aa79312ed7b1fca401c8682e3aa7a"
SRC_URI[sha256sum] = "5eeec797d7ff1d4b1e507925a1780fad5dd8dd11163203d8832e5a9f20a79b08"

inherit autotools pkgconfig

EXTRA_OECONF_append = "--enable-static=yes"

FILES_${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"

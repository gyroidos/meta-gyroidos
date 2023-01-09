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

SRC_URI[md5sum] = "eee5a28fbf2dd0298c108a18cecd3a03"
SRC_URI[sha256sum] = "51472d3a191d6d7b425e32b612e477c06f73fe23e07f6a6a839b11808e9d2267"


inherit autotools pkgconfig

EXTRA_OECONF:append = "--enable-static=yes"

FILES:${PN}-compiler = "${bindir}"

BBCLASSEXTEND = "native nativesdk"

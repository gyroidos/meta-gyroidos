SUMMARY = "protobuf-c-text"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in \
an efficient yet extensible format. Google uses Protocol Buffers for \
almost all of its internal RPC protocols and file formats."
HOMEPAGE = "https://github.com/protobuf-c/protobuf-c-text"
SECTION = "console/tools"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=c6a81c7d46ef4188c8bd511266895d5d"

BRANCH = "master"

SRCREV = "${AUTOREV}"
#SRC_URI = "git://github.com/protobuf-c/protobuf-c-text.git;protocol=http"
SRC_URI = "git://github.com/trustm3/external_protobuf-c-text.git;protocol=http;branch=${BRANCH}"

inherit pkgconfig autotools-brokensep

DEPENDS += "protobuf-c autoconf automake libcheck re2c-native"

PYTHON_SRC_DIR="python"
TEST_SRC_DIR="examples"
LANG_SUPPORT="cpp python"

S = "${WORKDIR}/git"

TARGET_CFLAGS += "-pedantic -Wall -Wextra -Werror -O2 -DHAVE_PROTOBUF_C_MESSAGE_CHECK"

EXTRA_OECONF_append = "--enable-static=yes"

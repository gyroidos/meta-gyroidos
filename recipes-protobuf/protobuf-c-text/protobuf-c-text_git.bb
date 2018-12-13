SUMMARY = "protobuf-c-text"
DESCRIPTION = "Protocol Buffers are a way of encoding structured data in \
an efficient yet extensible format. Google uses Protocol Buffers for \
almost all of its internal RPC protocols and file formats."
HOMEPAGE = "https://github.com/protobuf-c/protobuf-c-text"
SECTION = "console/tools"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=c6a81c7d46ef4188c8bd511266895d5d"

BRANCH = "trustme-7.1.2_r33-github"

SRCREV = "${AUTOREV}"
#SRC_URI = "git://github.com/protobuf-c/protobuf-c-text.git;protocol=http"
SRC_URI = "git://github.com/trustm3/external_protobuf-c-text.git;protocol=http;branch=${BRANCH}"

inherit pkgconfig autotools-brokensep

DEPENDS += "protobuf-c autoconf automake libcheck re2c-native"

PYTHON_SRC_DIR="python"
TEST_SRC_DIR="examples"
LANG_SUPPORT="cpp python"

S = "${WORKDIR}/git"

TARGET_CFLAGS += "-DHAVE_PROTOBUF_C_MESSAGE_CHECK"

do_configure(){
#       oe_runconf --libdir=${libdir} --sbindir=${sbindir} --bindir=${bindir} --mandir=${mandir} --includedir=${includedir} DESTDIR=${D}
#       oe_runconf --with-sysroot=${STAGING_DIR_TARGET}
        oe_runconf
	autoreconf --force --install
#--prefix=/usr
}

do_compile() {
        oe_runmake
}

do_install() {
#       oe_runmake install
        oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} \
                        MANDIR=${mandir} \
                        INCLUDEDIR=${includedir} \
                        LIBDIR=${libdir} \
                        BINDIR=${bindir}
# dirty fix: move header file to /usr/local/include/protobuf-c-text/
	mkdir ${D}${includedir}/protobuf-c-text/
	mv ${D}${includedir}/protobuf-c/protobuf-c-text.h ${D}${includedir}/protobuf-c-text/

# dirty fix:
        chrpath -d ${D}${libdir}/libprotobuf-c-text.so.2.0.0
}


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# backport of master to support kernel >= 6.18
SRC_URI:append = "\
    file://0001-tools-Kconfiglib-add-support-for-transitional-attrib.patch \
    file://0001-Kconfiglib-support-conditional-depends-on-X-if-Y-dep.patch \
"

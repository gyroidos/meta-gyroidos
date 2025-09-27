FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# configuration for kirkstone PV==0.40.0
SRC_URI:append = '${@bb.utils.contains("PV", "0.40.0", "\
	file://${PV}/0001-pixman-access-Mark-__dummy__-variables-with-MAYBE_UN.patch \
	file://${PV}/0002-Fix-some-build-warning.patch \
	file://${PV}/0003-pixman-combine-float.c-fix-inlining-failed-error.patch \
	file://${PV}/0004-Fix-Wincompatible-function-pointer-types-warning.patch \
	file://${PV}/0005-test-pixel-Fixed-warning-in-verfiy-maybe-uninitilaiz.patch \
	file://${PV}/0006-pixman-combine32-Fixed-warning-in-combine_mask-maybe.patch \
", "", d)}'

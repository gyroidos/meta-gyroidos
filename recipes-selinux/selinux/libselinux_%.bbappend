DEPENDS_append_libc-musl = " fts "

CFLAGS_append_libc-musl += "-lfts"

FILESEXTRAPATHS_prepend := "${THISDIR}/fixedpatch:"

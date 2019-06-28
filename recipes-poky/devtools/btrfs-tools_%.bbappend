# This is to remove python bindings from btrfs-tools and avoid a python installation in CML
DEPENDS = "util-linux attr e2fsprogs lzo acl"
EXTRA_OECONF += " --disable-python"

do_install_remove = "oe_runmake 'DESTDIR=${D}' 'PYTHON_SITEPACKAGES_DIR=${PYTHON_SITEPACKAGES_DIR}' install_python"

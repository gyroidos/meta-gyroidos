# An example recipe to build service container

inherit trustme-containerimage
require recipes-core/images/core-image-base.bb


# add your software
IMAGE_INSTALL += "gdb"

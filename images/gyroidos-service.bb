# An example recipe to build service container

inherit gyroidos-containerimage
require recipes-core/images/core-image-base.bb


# add your software
IMAGE_INSTALL += "gdb"

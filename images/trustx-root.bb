require recipes-core/images/core-image-base.bb

IMAGE_INSTALL:append = " libselinux libcgroup cgroup-lite lvm2 cmld scd control run strace iptables"

IMAGE_ROOTFS_EXTRA_SPACE:append += "+ 3000000"


require recipes-core/images/core-image-minimal.bb

include images/trustx-signing.inc

# Workaround for ICECC bug causing missing include dir errors
# uncomment to disable warnings
# TARGET_CFLAGS += " -w "
# BUILD_CFLAGS += " -w "

IMAGE_FSTYPES = "squashfs ext4"

IMAGE_FEATURES:append = " ssh-server-openssh"
IMAGE_INSTALL:append = " iproute2"
IMAGE_INSTALL:append = " iptables"
IMAGE_INSTALL:append = " util-linux"
IMAGE_INSTALL:append = " bridge-utils"
IMAGE_INSTALL:append = " usbutils"
IMAGE_INSTALL:append = " util-linux"
IMAGE_INSTALL:append = " binutils"
IMAGE_INSTALL:append = " shadow"

CONFIGS_OUT = "${B}/trustx-configs"

IMAGE_POSTPROCESS_COMMAND:append = " do_sign_guestos; "

do_sign_guestos:append () {
    mkdir -p ${CONFIGS_OUT}
    mkdir -p ${CONFIGS_OUT}/container

    cp ${CONTAINER_CONFIG} ${CONFIGS_OUT}/container
    sed -i '/guest_os:*/c\guest_os: \"${PN}os\"' ${CONFIGS_OUT}/container/${CONTAINER_CONFIG_FILE}
    sed -i '/guestos_version:*/c\guestos_version: ${GYROIDOS_VERSION}' ${CONFIGS_OUT}/container/${CONTAINER_CONFIG_FILE}
}

ROOTFS_POSTPROCESS_COMMAND:append = " update_inittab; "
ROOTFS_POSTPROCESS_COMMAND:append = " update_hostname; "
ROOTFS_POSTPROCESS_COMMAND:append = " update_network_interfaces; "

update_inittab () {
    sed -i "/ttyS[[:digit:]]\+/d" ${IMAGE_ROOTFS}/etc/inittab
    #echo "1::respawn:${base_sbindir}/mingetty --autologin root tty1" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "1:12345:respawn:${base_sbindir}/getty 38400 tty1" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "2:12345:respawn:${base_sbindir}/getty 38400 tty2" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "3:12345:respawn:${base_sbindir}/getty 38400 tty3" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "4:12345:respawn:${base_sbindir}/getty 38400 tty4" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "5:12345:respawn:${base_sbindir}/getty 38400 tty5" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "6:12345:respawn:${base_sbindir}/getty 38400 tty6" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "7:12345:respawn:${base_sbindir}/getty 38400 tty7" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "8:12345:respawn:${base_sbindir}/getty 38400 tty8" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "9:12345:respawn:${base_sbindir}/getty 38400 tty9" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "10:12345:respawn:${base_sbindir}/getty 38400 tty10" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "11:12345:respawn:${base_sbindir}/getty 38400 tty11" >> ${IMAGE_ROOTFS}/etc/inittab
    echo "12:12345:respawn:${base_sbindir}/getty 38400 tty12" >> ${IMAGE_ROOTFS}/etc/inittab

}

update_hostname () {
    echo "${PN}" > ${IMAGE_ROOTFS}/etc/hostname
}

update_network_interfaces () {
    cat >> ${IMAGE_ROOTFS}/etc/network/interfaces <<EOF

auto cmleth0
iface cmleth0 inet dhcp
EOF
}

# Manually remove the <guestos>os-<version> directory from
# ${DEPLOY_DIR_IMAGE} as this is not cleaned up by do_clean().
# This enables us to completely remove a guestos from a
# succeding trustx-cml build without orphan directories.
# Inetentionally use rmdir so this function will fail if the
# directory content was not cleaned properly.
python do_clean:append () {
    import glob
    import os

    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
    pn = d.getVar('PN')

    dirs = glob.glob(deploy_dir_image + '/trustx-guests/' + pn + 'os-*')

    for dir in dirs:
        os.rmdir(dir)
}


require recipes-core/images/core-image-minimal.bb

include images/trustx-signing.inc

# Workaround for ICECC bug causing missing include dir errors
# uncomment to disable warnings
# TARGET_CFLAGS += " -w "
# BUILD_CFLAGS += " -w "

IMAGE_FSTYPES = "squashfs ext4"

IMAGE_FEATURES_append = " allow-empty-password"
IMAGE_FEATURES_append = " empty-root-password"
IMAGE_FEATURES_append = " ssh-server-dropbear"
IMAGE_INSTALL_append = " control"
IMAGE_INSTALL_append = " openvswitch"
IMAGE_INSTALL_append = " bridge-utils"
IMAGE_INSTALL_append = " iproute2"
IMAGE_INSTALL_append = " iptables"
IMAGE_INSTALL_append = " util-linux"
IMAGE_INSTALL_append = " bridge-utils"
IMAGE_INSTALL_append = " usbutils"
IMAGE_INSTALL_append = " tcpdump"
IMAGE_INSTALL_append = " util-linux"
IMAGE_INSTALL_append = " binutils"

CONFIGS_OUT = "${DEPLOY_DIR_IMAGE}/trustx-configs"

do_sign_guestos_append () {
    mkdir -p ${CONFIGS_OUT}
    mkdir -p ${CONFIGS_OUT}/container

    cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/00000000-0000-0000-0000-000000000000.conf ${CONFIGS_OUT}/container
    sed -i '/guest_os:*/c\guest_os: \"${PN}os\"' ${CONFIGS_OUT}/container/00000000-0000-0000-0000-000000000000.conf

    cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/device.conf ${CONFIGS_OUT}
    sed -i '/c0os:*/c\c0os: \"${PN}os\"' ${CONFIGS_OUT}/device.conf
}

ROOTFS_POSTPROCESS_COMMAND_append = " update_inittab; "
ROOTFS_POSTPROCESS_COMMAND_append = " update_hostname; "
ROOTFS_POSTPROCESS_COMMAND_append = " update_network_interfaces; "

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
    echo "trustx-core" > ${IMAGE_ROOTFS}/etc/hostname
}

update_network_interfaces () {
    cat >> ${IMAGE_ROOTFS}/etc/network/interfaces <<EOF

auto cmleth0
iface cmleth0 inet dhcp
EOF
}

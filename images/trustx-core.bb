require recipes-core/images/core-image-minimal.bb

include images/trustx-signing.inc

IMAGE_FSTYPES = "squashfs ext4"

IMAGE_FEATURES_append = " allow-empty-password"
IMAGE_FEATURES_append = " empty-root-password"
IMAGE_FEATURES_append = " ssh-server-dropbear"
IMAGE_INSTALL_append = " control"

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

}

update_hostname () {
    echo "trustx-core" > ${IMAGE_ROOTFS}/etc/hostname
}

update_network_interfaces () {
    cat >> ${IMAGE_ROOTFS}/etc/network/interfaces <<EOF

auto eth0
iface eth0 inet dhcp
EOF
}

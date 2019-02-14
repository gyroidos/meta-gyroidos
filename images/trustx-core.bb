require recipes-core/images/core-image-minimal.bb

include images/trustx-signing.inc

IMAGE_FSTYPES = "squashfs ext4"

EXTRA_IMAGE_FEATURES += "allow-empty-password"
EXTRA_IMAGE_FEATURES += "empty-root-password"
EXTRA_IMAGE_FEATURES += "ssh-server-dropbear"

CONFIGS_OUT = "${DEPLOY_DIR_IMAGE}/trustx-configs"

do_sign_guestos_append () {
    mkdir -p ${CONFIGS_OUT}
    mkdir -p ${CONFIGS_OUT}/container

    cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/00000000-0000-0000-0000-000000000000.conf ${CONFIGS_OUT}/container
    sed -i '/guest_os:*/c\guest_os: \"${PN}os\"' ${CONFIGS_OUT}/container/00000000-0000-0000-0000-000000000000.conf

    cp ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/device.conf ${CONFIGS_OUT}
    sed -i '/c0os:*/c\c0os: \"${PN}os\"' ${CONFIGS_OUT}/device.conf
}

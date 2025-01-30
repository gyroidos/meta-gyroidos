####### Contains necessary properties of a gyroidos container image

include images/gyroidos-signing.inc

IMAGE_FSTYPES:append = " squashfs"
PACKAGE_INSTALL:append = " service-static"

### NOTE: for this container image, you can provide an operating system config template
### as well as a container template, for this purpose use the variables
### GUESTOS_CONF_TEMPLATE
### and
### CONTAINER_CONF_TEMPLATE
### respectively. If you do not redefine these variable, a default guestos config
### and no container config will be created

GUESTOS_CONF_TEMPLATE ??= "${CFG_OVERLAY_DIR}/guestos-template.conf"
CONTAINER_CONF_TEMPLATE ??= ""
CONFIGS_OUT = "${DEPLOY_DIR_IMAGE}/gyroidos-configs"

do_sign_guestos:prepend () {
    if [ ! -f ${CFG_OVERLAY_DIR}/${GYROIDOS_HARDWARE}/${PN}os.conf ]; then
	cp ${GUESTOS_CONF_TEMPLATE}  ${CFG_OVERLAY_DIR}/${GYROIDOS_HARDWARE}/${PN}os.conf
	sed -i '/name:*/c\name: \"${PN}\"' ${CFG_OVERLAY_DIR}/${GYROIDOS_HARDWARE}/${PN}os.conf
    fi
    if [ ! -z ${CONTAINER_CONF_TEMPLATE} ]; then
	cp ${CONTAINER_CONF_TEMPLATE} ${CONFIGS_OUT}/container
    fi
}

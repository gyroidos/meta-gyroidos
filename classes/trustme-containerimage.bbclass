####### Contains necessary properties of a trustme container image

include images/trustx-signing.inc

IMAGE_FSTYPES_append = " squashfs"
PACKAGE_INSTALL_append = " service-static"

### if os config file does not exist yet, create it from the default template
GUESTOS_CONF_TEMPLATE = "guestos-template.conf"

do_sign_guestos_prepend () {
    if [ ! -f ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/${PN}os.conf ]; then
	cp ${CFG_OVERLAY_DIR}/${GUESTOS_CONF_TEMPLATE}  ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/${PN}os.conf
	sed -i '/name:*/c\name: \"${PN}\"' ${CFG_OVERLAY_DIR}/${TRUSTME_HARDWARE}/${PN}os.conf
    fi
}
###

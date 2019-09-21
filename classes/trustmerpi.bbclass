inherit trustmegeneric

#
# Create an partitioned trustme image that can be copied to an SD card
#

TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

TRUSTME_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_bootpart"
TRUSTME_IMAGE_TMP="${DEPLOY_DIR_IMAGE}/tmp_trustmeimage"
TRUSTME_IMAGE_OUT="${DEPLOY_DIR_IMAGE}/trustme_image"

TRUSTME_IMAGE="${TRUSTME_IMAGE_OUT}/trustmeimage.img"

TRUSTME_DEFAULTCONFIG="trustx-core.conf"

# Set kernel and boot loader
IMAGE_BOOTLOADER ?= "bcm2835-bootfiles"

# for multiconfig container we need rpi variables to be set empty
RPI_USE_U_BOOT ?= ""
KERNEL_DEVICETREE ?= ""

# Kernel image name
SDIMG_KERNELIMAGE_raspberrypi  ?= "kernel.img"
SDIMG_KERNELIMAGE_raspberrypi2 ?= "kernel7.img"
SDIMG_KERNELIMAGE_raspberrypi3-64 ?= "kernel8.img"

do_image_trustmerpi[depends] = " \
    ${IMAGE_BOOTLOADER}:do_deploy \
    ${@bb.utils.contains('RPI_USE_U_BOOT', '1', 'u-boot:do_deploy', '',d)} \
    ${@bb.utils.contains('RPI_USE_U_BOOT', '1', 'rpi-u-boot-scr:do_deploy', '',d)} \
"

do_image_trustmerpi[depends] += " ${TRUSTME_GENERIC_DEPENDS} "

do_image_trustmerpi[recrdeps] = "do_build"

def splitoverlays(d, out, ver=None):
    dts = d.getVar("KERNEL_DEVICETREE")
    # Device Tree Overlays are assumed to be suffixed by '-overlay.dtb' (4.1.x) or by '.dtbo' (4.4.9+) string and will be put in a dedicated folder
    if out:
        overlays = oe.utils.str_filter_out('\S+\-overlay\.dtb$', dts, d)
        overlays = oe.utils.str_filter_out('\S+\.dtbo$', overlays, d)
    else:
        overlays = oe.utils.str_filter('\S+\-overlay\.dtb$', dts, d) + \
                   " " + oe.utils.str_filter('\S+\.dtbo$', dts, d)

    return overlays

do_rpi_bootpart () {

	if [ -z "${DEPLOY_DIR_IMAGE}" ];then
		bbfatal "Cannot get bitbake variable \"DEPLOY_DIR_IMAGE\""
		exit 1
	fi

	if [ -z "${TRUSTME_BOOTPART_DIR}" ];then
		bbfatal "Cannot get bitbake variable \"TRUSTME_BOOTPART_DIR\""
		exit 1
	fi
	# Check if we are building with device tree support
	DTS="${KERNEL_DEVICETREE}"

	bbnote "Copying boot partition files to ${TRUSTME_BOOTPART_DIR}"

	machine=$(echo "${MACHINE}" | tr "_" "-")
	bbdebug 1 "Boot machine: $machine"

	rm -fr "${TRUSTME_BOOTPART_DIR}"
	install -d "${TRUSTME_BOOTPART_DIR}/tmp"

	cp --dereference "${DEPLOY_DIR_IMAGE}/bcm2835-bootfiles/"* "${TRUSTME_BOOTPART_DIR}"
	if [ -n "${DTS}" ]; then
		# Copy board device trees to root folder
		for dtbf in ${@splitoverlays(d, True)}; do
			dtb=`basename $dtbf`
			cp --dereference "${DEPLOY_DIR_IMAGE}/$dtb" "${TRUSTME_BOOTPART_DIR}/$dtb"
		done
		# Copy device tree overlays to dedicated folder
		mkdir -p "${TRUSTME_BOOTPART_DIR}/overlays"
		for dtbf in ${@splitoverlays(d, False)}; do
			dtb=`basename $dtbf`
			cp --dereference "${DEPLOY_DIR_IMAGE}/$dtb" "${TRUSTME_BOOTPART_DIR}/overlays/$dtb"
		done
	fi
	if [ "${RPI_USE_U_BOOT}" = "1" ]; then
		cp --dereference "${DEPLOY_DIR_IMAGE}/u-boot.bin" "${TRUSTME_BOOTPART_DIR}/${SDIMG_KERNELIMAGE}"
		cp --dereference "${DEPLOY_DIR_IMAGE}/boot.scr" "${TRUSTME_BOOTPART_DIR}/boot.scr"
		if [ ! -z "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
			cp --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}.bin" "${TRUSTME_BOOTPART_DIR}/${KERNEL_IMAGETYPE}"
		else
			cp --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/${KERNEL_IMAGETYPE}" "${TRUSTME_BOOTPART_DIR}/${KERNEL_IMAGETYPE}"
		fi
	else
		if [ ! -z "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
			cp --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}.bin" "${TRUSTME_BOOTPART_DIR}/${SDIMG_KERNELIMAGE}"
		else
			cp --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/${KERNEL_IMAGETYPE}" "${TRUSTME_BOOTPART_DIR}/${SDIMG_KERNELIMAGE}"
		fi
	fi

	bbnote  "Created rpi boot files at ${TRUSTME_BOOTPART_DIR}/"
	rm -fr "${TRUSTME_BOOTPART_DIR}/tmp/"
}


IMAGE_CMD_trustmerpi () {
	bbnote  "Using standard trustme partition"
	do_rpi_bootpart
	do_build_trustmeimage
}


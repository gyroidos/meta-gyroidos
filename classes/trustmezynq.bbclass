inherit trustmegeneric

#
# Create an partitioned trustme image that can be copied to an SD card
#

TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

TRUSTME_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_bootpart"
TRUSTME_IMAGE_TMP="${DEPLOY_DIR_IMAGE}/tmp_trustmeimage"

TRUSTME_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_bootpart"
TRUSTME_IMAGE_OUT="${DEPLOY_DIR_IMAGE}/trustme_image"

TRUSTME_IMAGE="${TRUSTME_IMAGE_OUT}/trustmeimage.img"

TRUSTME_DEFAULTCONFIG="trustx-core.conf"


do_image_trustmezynq[depends] = " \
    u-boot-mkimage-native:do_populate_sysroot \
    u-boot-zynq-scr:do_deploy \
    virtual/boot-bin:do_deploy \
"


do_image_trustmezynq[depends] += " ${TRUSTME_GENERIC_DEPENDS} "

do_zynq_bootpart () {

	if [ -z "${DEPLOY_DIR_IMAGE}" ];then
		bbfatal "Cannot get bitbake variable \"DEPLOY_DIR_IMAGE\""
		exit 1
	fi

	if [ -z "${TRUSTME_BOOTPART_DIR}" ];then
		bbfatal "Cannot get bitbake variable \"TRUSTME_BOOTPART_DIR\""
		exit 1
	fi

	bbnote "Copying boot partition files to ${TRUSTME_BOOTPART_DIR}"
	
	machine=$(echo "${MACHINE}" | tr "_" "-")
	bbdebug 1 "Boot machine: $machine"

	rm -fr "${TRUSTME_BOOTPART_DIR}"
	install -d "${TRUSTME_BOOTPART_DIR}/tmp"
	
	cp --dereference "${DEPLOY_DIR_IMAGE}/boot.bin" "${TRUSTME_BOOTPART_DIR}/BOOT.BIN"
	cp --dereference "${DEPLOY_DIR_IMAGE}/boot.scr" "${TRUSTME_BOOTPART_DIR}/BOOT.SCR"

	cp "${TOPDIR}/../trustme/build/yocto/arm64/zcu104-zynqmp/trustme-fit.its" "${TRUSTME_BOOTPART_DIR}/tmp/trustme-fit.its"
	cp --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/Image-zcu104-zynqmp.bin" "${TRUSTME_BOOTPART_DIR}/tmp/Image-zcu104-zynqmp.bin"
	cp --dereference "${DEPLOY_DIR_IMAGE}/zynqmp-zcu104-revC.dtb" "${TRUSTME_BOOTPART_DIR}/tmp/zynqmp-zcu104-revC.dtb"
	cp --dereference "${DEPLOY_DIR_IMAGE}/trustx-cml-initramfs-zcu104-zynqmp.cpio.gz" "${TRUSTME_BOOTPART_DIR}/tmp/trustx-cml-initramfs-zcu104-zynqmp.cpio.gz"

	mkimage -f "${TRUSTME_BOOTPART_DIR}/tmp/trustme-fit.its" "${TRUSTME_BOOTPART_DIR}/Image"
	bbnote  "Created zynq boot files at ${TRUSTME_BOOTPART_DIR}/"
	rm -fr "${TRUSTME_BOOTPART_DIR}/tmp/"
}


IMAGE_CMD_trustmezynq () {
	bbnote  "Using standard trustme partition"
	do_zynq_bootpart
	do_build_trustmeimage
}

#addtask do_zynq_bootpart before IMAGE_CMD_trustmeimagezcu104
#addtask do_build_trustmeimage before IMAGE_CMD_trustmeimagezcu104


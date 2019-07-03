inherit image_types
inherit kernel-artifact-names

#
# Create an partitioned trustme image that can be dd'ed to the boot medium
#


TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

TRUSTME_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_bootpart"
TRUSTME_IMAGE_TMP="${DEPLOY_DIR_IMAGE}/tmp_trustmeimage"

TRUSTME_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_bootpart"
TRUSTME_IMAGE_OUT="${DEPLOY_DIR_IMAGE}/trustme_image"
TRUSTME_CONTAINER_ARCH="qemux86-64"

TRUSTME_IMAGE="${TRUSTME_IMAGE_OUT}/trustmeimage.img"

TRUSTME_DEFAULTCONFIG?="trustx-core.conf"




do_trustme_bootpart[depends] += " \
    virtual/kernel:do_deploy \
    sbsigntool-native:do_populate_sysroot \
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    btrfs-tools-native:do_populate_sysroot \
    gptfdisk-native:do_populate_sysroot \
    trustx-cml-initramfs:do_image_complete \
    virtual/kernel:do_shared_workdir \
"




do_trustme_bootpart () {
	rm -fr ${TRUSTME_BOOTPART_DIR}

	if [ -z "${DEPLOY_DIR_IMAGE}" ];then
		bbfatal "Cannot get bitbake variable \"DEPLOY_DIR_IMAGE\""
		exit 1
	fi

	if [ -z "${TRUSTME_BOOTPART_DIR}" ];then
		bbfatal "Cannot get bitbake variable \"TRUSTME_BOOTPART_DIR\""
		exit 1
	fi

	if [ -z "${MACHINE}" ];then
		bbfatal "Cannot get bitbake variable \"MACHINE\""
		exit 1
	fi

	bbnote "Signing kernel binary"
	kernelbin="${DEPLOY_DIR_IMAGE}/cml-kernel/bzImage-initramfs-${MACHINE}.bin"
	if [ -L "${kernelbin}" ]; then
	    link=`readlink "${kernelbin}"`
	    ln -sf ${link}.signed ${kernelbin}.signed
	fi

	sbsign --key "${SECURE_BOOT_SIGNING_KEY}" --cert "${SECURE_BOOT_SIGNING_CERT}" --output "${kernelbin}.signed" "${kernelbin}"

	bbnote "Copying boot partition files to ${TRUSTME_BOOTPART_DIR}"

	machine=$(echo "${MACHINE}" | tr "_" "-")
	bbdebug 1 "Boot machine: $machine"

	install -d "${TRUSTME_BOOTPART_DIR}/EFI/BOOT/"
	cp --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/bzImage-initramfs-${machine}.bin.signed" "${TRUSTME_BOOTPART_DIR}/EFI/BOOT/BOOTX64.EFI"
}





addtask do_trustme_bootpart before do_image_trustmeimage

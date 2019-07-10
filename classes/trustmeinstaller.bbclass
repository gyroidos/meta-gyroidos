inherit image_types
inherit kernel-artifact-names

#
# Create an partitioned trustme image that can be dd'ed to the boot medium
#


TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

INSTALLER_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/installer_bootpart"
INSTALLER_IMAGE_TMP="${DEPLOY_DIR_IMAGE}/tmp_installerimage"

INSTALLER_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/installer_bootpart"
INSTALLER_IMAGE_OUT="${DEPLOY_DIR_IMAGE}/instaler_image"
TRUSTME_CONTAINER_ARCH="qemux86-64"

INSTALLER_IMAGE="${INSTALLER_IMAGE_OUT}/trustmeimage.img"


do_installer_bootpart[depends] = " \
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    btrfs-tools-native:do_populate_sysroot \
    gptfdisk-native:do_populate_sysroot \
    sbsigntool-native:do_populate_sysroot \
	virtual/kernel:do_deploy \
"


do_installer_bootpart () {

	if [ -z "${DEPLOY_DIR_IMAGE}" ];then
		bbfatal "Cannot get bitbake variable \"DEPLOY_DIR_IMAGE\""
		exit 1
	fi

	if [ -z "${INSTALLER_BOOTPART_DIR}" ];then
		bbfatal "Cannot get bitbake variable \"INSTALLER_BOOTPART_DIR\""
		exit 1
	fi

	if [ -z "${MACHINE}" ];then
		bbfatal "Cannot get bitbake variable \"MACHINE\""
		exit 1
	fi

	rm -fr ${INSTALLER_BOOTPART_DIR}

	machine_replaced=$(echo "${MACHINE}" | tr "_" "-")

	bbnote "Signing kernel binary"
	kernelbin="${DEPLOY_DIR_IMAGE}/installer-kernel/bzImage-initramfs-${machine_replaced}.bin"
	if [ -L "${kernelbin}" ]; then
		link=`readlink "${kernelbin}"`
		rm -f ${link}.signed ${kernelbin}.signed
		ln -sf ${link}.signed ${kernelbin}.signed
	fi

	sbsign --key "${SECURE_BOOT_SIGNING_KEY}" --cert "${SECURE_BOOT_SIGNING_CERT}" --output "${kernelbin}.signed" "${kernelbin}"

	bbnote "Copying boot partition files to ${INSTALLER_BOOTPART_DIR}"

	bbdebug 1 "Boot machine: $machine"

	install -d "${INSTALLER_BOOTPART_DIR}/EFI/BOOT/"
	cp --dereference "${DEPLOY_DIR_IMAGE}/installer-kernel/bzImage-initramfs-${machine_replaced}.bin.signed" "${INSTALLER_BOOTPART_DIR}/EFI/BOOT/BOOTX64.EFI"
}

INSTALLER_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_installerbootpart"
INSTALLER_IMAGE_TMP="${DEPLOY_DIR_IMAGE}/tmp_trustmeinstaller"
INSTALLER_TARGET_ALIGN="4096"
INSTALLER_TARGET_SECTOR_SIZE="4096"

INSTALLER_BOOTPART_DIR="${DEPLOY_DIR_IMAGE}/trustme_installerbootpart"
INSTALLER_IMAGE_OUT="${DEPLOY_DIR_IMAGE}/trustme_image"
TRUSTME_CONTAINER_ARCH="qemux86-64"

INSTALLER_IMAGE="${INSTALLER_IMAGE_OUT}/trustmeinstaller.img"
INSTALLER_BOOTPART_EXTRA_FACTOR="1.2"
INSTALLER_BOOTPART_FS="fat16"


addtask do_installer_bootpart before do_image_trustmeinstaller

do_image_trustmeinstaller[depends] = " \
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    btrfs-tools-native:do_populate_sysroot \
    gptfdisk-native:do_populate_sysroot \
    virtual/kernel:do_deploy \
"

#IMAGE_CMD_trustmeinstaller[deptask] += " do_installer_bootpart "


IMAGE_CMD_trustmeinstaller () {
#
#	if [ -z "${INSTALLER_BOOTPART_DIR}" ];then
#		bbfatal_log "Cannot get bitbake variable \"INSTALLER_BOOTPART_DIR\""
#		exit 1
#	fi
#
#	if [ -z "${TOPDIR}" ];then
#		bbfatal_log "Cannot get bitbake variable \"TOPDIR\""
#		exit 1
#	fi
#
#	if [ -z "${DEPLOY_DIR_IMAGE}" ];then
#		bbfatal_log "Cannot get bitbake variable \"DEPLOY_DIR_IMAGE\""
#		exit 1
#	fi
#
#	if [ -z "${MACHINE_ARCH}" ];then
#		bbfatal_log "Cannot get bitbake variable \"MACHINE_ARCH\""
#		exit 1
#	fi
#
#	if [ -z "${WORKDIR}" ];then
#		bbfatal_log "Cannot get bitbake variable \"WORKDIR\""
#		exit 1
#	fi
#
#	if [ -z "${S}" ];then
#		bbfatal_log "Cannot get bitbake variable \"TRUSTME_HARDWARE\""
#		exit 1
#	fi
#
#	if [ -z "${PREFERRED_PROVIDER_virtual/kernel}" ];then
#		bbfatal_log "Cannot get bitbake variable \"PREFERRED_PROVIDER_virtual/kernel\""
#		exit 1
#	fi
#
#	if [ -z "${MACHINE}" ];then
#		bbfatal_log "Cannot get bitbake variable \"MACHINE\""
#		exit 1
#	fi
#
#	if [ -z "${DISTRO}" ];then
#		bbfatal_log "Cannot get bitbake variable \"DISTRO\""
#		exit 1
#	fi
#
#	if [ -z "${INSTALLER_IMAGE_OUT}" ];then
#		bbfatal_log "Cannot get bitbake variable \"INSTALLER_IMAGE_OUT\""
#		exit 1
#	fi
#
#	rm -f "${INSTALLER_IMAGE}"
#
#	machine_replaced=$(echo "${MACHINE}" | tr "-" "_")
#
#	bbnote "Starting to create trustme image"
#	# create temporary directories
#	install -d "${INSTALLER_IMAGE_OUT}"
#	install -d "${INSTALLER_BOOTPART_DIR}"
#	tmp_modules="${INSTALLER_IMAGE_TMP}/tmp_modules"
#	tmp_datapart="${INSTALLER_IMAGE_TMP}/tmp_data"
#	rootfs_datadir="${tmp_datapart}/userdata/"
#	tmpdir="${TOPDIR}/tmp_container"
#	trustme_fsdir="${INSTALLER_IMAGE_TMP}/filesystems"
#	trustme_bootfs="$trustme_fsdir/trustme_bootfs"
#	trustme_datafs="$trustme_fsdir/trustme_datafs"
#
#	# TODO warn user?
#	rm -fr "${INSTALLER_IMAGE_TMP}"
#	install -d "${INSTALLER_IMAGE_TMP}"
#	rm -fr "${rootfs_datadir}"
#	install -d "${rootfs_datadir}"
#	rm -fr "${trustme_fsdir}"
#	install -d "${trustme_fsdir}"
#	rm -fr "${tmp_modules}/"
#	install -d "${tmp_modules}/"
#
#	# define file locations
#	#deploy_dir_container = "${tmpdir}/deploy/images/qemu-x86-64"
#	deploy_dir_container="${tmpdir}/deploy/images/$(echo "${TRUSTME_CONTAINER_ARCH}" | tr "_" "-")"
#
#	src="${TOPDIR}/../trustme/build/"
#	config_creator_dir="${src}/config_creator"
#	proto_file_dir="${WORKDIR}/cml/daemon"
#	provisioning_dir="${src}/device_provisioning"
#	enrollment_dir="${provisioning_dir}/oss_enrollment"
#	test_cert_dir="${TOPDIR}/test_certificates"
#
#
#	if ! [ -d "${test_cert_dir}" ];then
#		bbfatal_log "Test PKI not generated at ${test_cert_dir}\nIs trustx-cml built?"
#		exit 1
#	fi
#
#	# copy files to tmp data directory
#	bbnote "Preparing files for data partition"
#
#	install -d "${rootfs_datadir}/trustme_boot/EFI/BOOT"
#
#	cp -r "${DEPLOY_DIR_IMAGE}/tmp_trustmeimage/tmp_data" "${rootfs_datadir}/trustme_data"
#	cp -r --dereference "${DEPLOY_DIR_IMAGE}/cml-kernel/bzImage-initramfs-${machine_replaced}.bin.signed" "${rootfs_datadir}/trustme_boot/EFI/BOOT/BOOTX64.EFI"
#	cp "${TOPDIR}/../trustme/build/yocto/install_trustme.sh" "${rootfs_datadir}/"
#
#	# copy modules to data partition directory
#	cp -fL "${DEPLOY_DIR_IMAGE}/installer-kernel/modules-${MODULE_TARBALL_LINK_NAME}.tgz" "${tmp_modules}/modules.tgz"
#	ls -l "${tmp_modules}"
#	tar -C "${tmp_modules}/" -xf "${tmp_modules}/modules.tgz"
#	kernelabiversion="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"
#	#kernelabiversion="${KERNELVERSION}"
#	rm -f "${tmp_modules}/modules.tgz"
#	bbnote "Updating modules dependencies for kernel $kernelabiversion"
#	sh -c "cd \"${tmp_modules}\" && depmod --basedir \"${tmp_modules}\" ${kernelabiversion}"
#	cp -fr "${tmp_modules}/lib/modules" "${tmp_datapart}"
#
#	# Create boot partition and mark it as bootable
#	bootpart_size_targetblocks="$(du --block-size=${INSTALLER_TARGET_ALIGN} -s ${INSTALLER_BOOTPART_DIR} | awk '{print $1}')"
#	bootpart_size_targetblocks="$(python -c "print(str($bootpart_size_targetblocks + ($bootpart_size_targetblocks * ${INSTALLER_BOOTPART_EXTRA_FACTOR}))[:-2])")"
#	bootpart_size_bytes="$(expr $bootpart_size_targetblocks '*' ${INSTALLER_TARGET_ALIGN})"
#	# append space to bootpart if INSTALLER_TARGET_ALIGN < 1024 (mkdisfs block size)
#	if ! [ "$(expr $bootpart_size_bytes '%' 1024)"="0" ]; then
#		echo "Appending boot partition size to match mkdosfs blocksize of 1024"
#		bootpart_size_bytes="$(expr $bootpart_size_bytes + '(' $bootpart_size_bytes '%' 1024 ')')"
#		bootpart_size_targetblocks="$(${bootpart_size_bytes} '/' ${INSTALLER_TARGET_ALIGN})"
#	fi
#	bbnote "Boot files size $bootpart_size_bytes) bytes"
#
#
#	bootpart_size_1k="$(expr $bootpart_size_bytes '/' 1024)"
#
#	datapart_size_targetblocks="$(du --block-size=${INSTALLER_TARGET_ALIGN} -s ${tmp_datapart} | awk '{print $1}')"
#	datapart_size_targetblocks="$(python -c "print(str($datapart_size_targetblocks + ($datapart_size_targetblocks * ${INSTALLER_BOOTPART_EXTRA_FACTOR}))[:-2])")"
#	datapart_size_bytes="$(expr $datapart_size_targetblocks '*' ${INSTALLER_TARGET_ALIGN})"
#	datafolder_size="$(expr $datapart_size_targetblocks '*' ${INSTALLER_TARGET_ALIGN})"
#	bbnote "Data files size: $datafolder_size bytes"
#
#	##### create filesystems #####
#
#	# creating boot filesystem
#	bbnote "Creating boot filesystem ${trustme_bootfs}"
#	rm -f "$trustme_bootfs"
#	mkdosfs -n efi -C "$trustme_bootfs" "$bootpart_size_1k"
#
#	MTOOLS_SKIP_CHECK=1 mcopy -i "$trustme_bootfs" -s ${INSTALLER_BOOTPART_DIR}/* ::/
#	chmod 644 "$trustme_bootfs"
#
#
#	# creating data filesystem
#	bbnote "Creating data filesystem ${trustme_datafs}"
#	bbdebug 1 "dd'ing data fs: ${datapart_size_targetblocks} 4K blocks, $(expr ${datapart_size_targetblocks} '*' ${INSTALLER_TARGET_ALIGN}) bytes"
#	dd if=/dev/zero of="$trustme_datafs" conv=notrunc,fsync iflag=sync oflag=sync bs=${INSTALLER_TARGET_ALIGN} count=$datapart_size_targetblocks
#	/bin/sync
#	bbdebug 1 "Creating ext4 fs of size ${datapart_size_targetblocks} blocks, ${datapart_size_bytes} bytes on file $trustme_datafs"
#	#mkfs.btrfs --byte-count "${datapart_size_bytes}" --label trustme --rootdir "$tmp_datapart" "$trustme_datafs"
#	mkfs.ext4 -b ${INSTALLER_TARGET_ALIGN} -d "$tmp_datapart" -L trustme "$trustme_datafs" "${datapart_size_targetblocks}"
#	chmod 644 "$trustme_datafs"
#	#btrfsck "$trustme_datafs"
#
#	/bin/sync
#
#	##### Create empty image and partition table #####
#	bootimg_size_targetblocks="$(du -Ls --block-size=${INSTALLER_TARGET_ALIGN} "$trustme_bootfs" | awk '{print $1}')"
#	bootimg_size_bytes="$(expr $bootimg_size_targetblocks '*' ${INSTALLER_TARGET_ALIGN})"
#	dataimg_size_targetblocks="${datapart_size_targetblocks}"
#	dataimg_size_bytes="$(expr $dataimg_size_targetblocks '*' ${INSTALLER_TARGET_ALIGN})"
#
#	bbdebug 1 "Filesystem sizes:\nbootimg_size_targetblocks $bootimg_size_targetblocks\nbootimg_size_bytes $bootimg_size_bytes\ndataimg_size_targetblocks $dataimg_size_targetblocks\ndataimg_size_bytes $dataimg_size_bytes"
#	##### calc start/end of partitions #####
#	# cals start/end of boot partition
#	start_bootpart="$(expr 34 '*' ${INSTALLER_TARGET_SECTOR_SIZE})"
#	start_bootpart="$(expr $start_bootpart + '(' $start_bootpart '%' ${INSTALLER_TARGET_ALIGN} ')' )"
#	start_bootpart_targetblocks="$(expr $start_bootpart '/' ${INSTALLER_TARGET_ALIGN})"
#	end_bootpart="$(expr $start_bootpart + $bootpart_size_bytes)"
#
#	end_bootpart_target_blocks="$(expr $start_bootpart + $bootpart_size_bytes)"
#
#	# calc start/end of data partition
#	if [ "$(expr $end_bootpart '%' ${INSTALLER_TARGET_ALIGN})"="0" ]; then
#		start_datapart="$(expr $end_bootpart + '(' $end_bootpart '%' ${INSTALLER_TARGET_ALIGN} ')' + ${INSTALLER_TARGET_ALIGN} )"
#		#bbnote "$(echo "Start datapartition: ${start_datapart}appended ${INSTALLER_TARGET_ALIGN} bytes\")"
#	else
#		start_datapart="$(expr $end_bootpart + '(' $end_bootpart '%' ${INSTALLER_TARGET_ALIGN} ')')"
#		#bbnote "Start datapartition: ${start_datapart}appended $(expr $end_bootpart '%' ${INSTALLER_TARGET_ALIGN}) bytes"
#	fi
#
#	start_datapart_targetblocks="$(expr $start_datapart '/' ${INSTALLER_TARGET_ALIGN})"
#
#	end_datapart="$(expr $start_datapart + $dataimg_size_bytes)"
#	#end_datapart="$(expr $end_datapart + '(' $end_datapart '%' ${INSTALLER_TARGET_ALIGN} ')' + 10000 '*' ${INSTALLER_TARGET_ALIGN})"
#
#	img_size_targetblocks="$(expr '(' $end_datapart + '(' $end_datapart '%' ${INSTALLER_TARGET_ALIGN} ')' + 34 '*' ${INSTALLER_TARGET_ALIGN} + 10000 '*' ${INSTALLER_TARGET_ALIGN} ')' '/' ${INSTALLER_TARGET_ALIGN})"
#	img_size="$(expr $img_size_targetblocks '*' ${INSTALLER_TARGET_ALIGN})"
#
#
#	##### Create partitions #####
#	bbdebug 1 "Creating empty image file"
#
#	rm -f ${INSTALLER_IMAGE}
#	dd if=/dev/zero of=${INSTALLER_IMAGE} bs=${INSTALLER_TARGET_ALIGN} count=$img_size_targetblocks conv=notrunc,fsync iflag=sync oflag=sync status=progress
#	/bin/sync
#	sleep 2
#	tmp_img_size="$(du --block-size=1 ${INSTALLER_IMAGE})"
#	if ! [ "$tmp_img_size"="$img_size" ];then
#		bbfatal_log "Image size should be $img_size but is $tmp_img_size. Aborting..."
#	else
#		bbnote "Sucessfully verified size of ${INSTALLER_IMAGE}"
#	fi
#
#	bbnote "Creating partition table:"
#	parted -s "${INSTALLER_IMAGE}" unit B --align none mklabel gpt
#	sgdisk --move-second-header "${INSTALLER_IMAGE}"
#
#	# Create boot partition
#	parted -s ${INSTALLER_IMAGE} unit B --align none mkpart boot ${INSTALLER_BOOTPART_FS} "${start_bootpart}B" "${end_bootpart}B"
#	parted -s ${INSTALLER_IMAGE} set 1 legacy_boot on
#	parted -s ${INSTALLER_IMAGE} set 1 msftdata on
#	parted -s ${INSTALLER_IMAGE} set 1 boot off
#	parted -s ${INSTALLER_IMAGE} set 1 esp off
#	partprobe
#	bbnote "Created boot partition"
#
#	parted -s ${INSTALLER_IMAGE} unit B --align none print
#
#	# Create data partition
#	parted -s ${INSTALLER_IMAGE} unit B --align none mkpart trustmeinstaller "${INSTALLER_BOOTPART_FS}" "${start_datapart}B" "${end_datapart}B"
#	parted -s ${INSTALLER_IMAGE} set 2 legacy_boot off
#	parted -s ${INSTALLER_IMAGE} set 2 msftdata off
#	parted -s ${INSTALLER_IMAGE} set 2 boot off
#	parted -s ${INSTALLER_IMAGE} set 2 esp off
#
#	bbnote "Created data partition"
#
#	bbnote "Copying filesystems to partitions"
#	bbdebug 1 "Sizes:\nimg_size: ${img_size}\nimg_size_targetblocks: ${img_size_targetblocks}\nstart_datapart=${start_datapart}\n end_datapart: ${end_datapart}\nstart_bootpart=${start_bootpart}\n end_bootpart: ${end_bootpart}\nbootimg_size_bytes: ${bootimg_size_bytes}\ndataimg_size_bytes: ${dataimg_size_bytes}"
#
#	bbnote "Copying boot filesystem to partition"
#	dd if=${trustme_bootfs} of=${INSTALLER_IMAGE} bs=${INSTALLER_TARGET_ALIGN} count=${bootimg_size_targetblocks} seek=${start_bootpart_targetblocks} conv=notrunc,fsync iflag=sync oflag=sync status=progress
#	/bin/sync
#	partlayout="$(parted ${INSTALLER_IMAGE} unit B --align none print 2>1)"
#	bbdebug 1 "${partlayout}"
#
#	#sgdisk --print ${INSTALLER_IMAGE}
#
#	bbnote "Copying data filesystem to partition"
#	bbdebug 1 "img_size(planned): ${img_size}, img_size (real): $(du --block-size=1 ${INSTALLER_IMAGE})\nimg_size_targetblocks: ${img_size_targetblocks}, start_datapart=${start_datapart}, end_datapart: ${end_datapart}"
#
#	# TODO host tool
#	/bin/sync
#	dd if=${trustme_datafs} of=${INSTALLER_IMAGE} bs=${INSTALLER_TARGET_ALIGN} count=${dataimg_size_targetblocks} seek=${start_datapart_targetblocks} conv=notrunc,fsync iflag=sync oflag=sync status=progress
#
#	partlayout="$(parted ${INSTALLER_IMAGE} unit B --align none print 2>1)"
#	bbnote "Final partition layout:\n${partlayout}"
#
#	checkfs="$(cmp ${trustme_datafs} ${INSTALLER_IMAGE} --bytes=${dataimg_size_bytes} --ignore-initial=0:$(expr ${start_datapart_targetblocks} '*' ${INSTALLER_TARGET_ALIGN}))"
#
#	if [ "${checkfs}"="" ];then
#		bbnote "Sucessfully verified integrity of data filesystem"
#	else
#		bbfatal_log "Failed to verify integrity of data filesystem. Aborting..."
#	fi
#
#	checkfs="$(cmp ${trustme_bootfs} ${INSTALLER_IMAGE} --bytes=${bootimg_size_bytes} --ignore-initial=0:$(expr ${start_bootpart_targetblocks} '*' ${INSTALLER_TARGET_ALIGN}))"
#
#	if [ "${checkfs}"="" ];then
#		bbnote "Sucessfully verified integrity of boot filesystem"
#	else
#		bbfatal_log "Failed to verify integrity of boot filesystem. Aborting..."
#	fi
#
#	bbnote "Successfully created trustme image at ${INSTALLER_IMAGE}"
}

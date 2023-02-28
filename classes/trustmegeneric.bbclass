inherit image_types
inherit kernel-artifact-names
inherit deploy

#
# Create an partitioned trustme image that can be dd'ed to the boot medium
#


TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

TRUSTME_TARGET_ALIGN="4096"
TRUSTME_TARGET_SECTOR_SIZE="4096"
TRUSTME_SECTOR_SIZE="4096"
TRUSTME_PARTTABLE_TYPE?="gpt"

TRUSTME_BOOTPART_DIR="${B}/trustme_bootpart"
TRUSTME_IMAGE_TMP="${B}/tmp_trustmeimage"
TRUSTME_IMAGE_OUT="${B}/trustme_image"
TRUSTME_IMAGE="${TRUSTME_IMAGE_OUT}/trustmeimage.img"

TRUSTME_DATAPART_EXTRA_SPACE?="100"
TRUSTME_BOOTPART_EXTRA_FACTOR="1.2"
TRUSTME_BOOTPART_FS="fat16"
TRUSTME_BOOTPART_ALIGN="4096"
TRUSTME_DATAPART_FS="ext4"
TRUSTME_ROOTFS_ALIGN="4096"

TRUSTME_DEFAULTCONFIG?="trustx-core.conf"

TRUSTME_GENERIC_DEPENDS = " \
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    gptfdisk-native:do_populate_sysroot \
    trustx-cml-initramfs:do_image_complete \
    trustx-cml-firmware:do_image_complete \
    virtual/kernel:do_deploy \
"

DEPENDS += "e2fsprogs-native bc-native"


do_build_trustmeimage () {

	if [ -z "${TRUSTME_BOOTPART_DIR}" ];then
		bbfatal_log "Cannot get bitbake variable \"TRUSTME_BOOTPART_DIR\""
		exit 1
	fi

	if [ -z "${TOPDIR}" ];then
		bbfatal_log "Cannot get bitbake variable \"TOPDIR\""
		exit 1
	fi

	if [ -z "${DEPLOY_DIR_IMAGE}" ];then
		bbfatal_log "Cannot get bitbake variable \"DEPLOY_DIR_IMAGE\""
		exit 1
	fi

	if [ -z "${DEPLOY_DIR_IPK}" ];then
		bbfatal_log "Cannot get bitbake variable \"DEPLOY_DIR_IPK\""
		exit 1
	fi


	if [ -z "${MACHINE_ARCH}" ];then
		bbfatal_log "Cannot get bitbake variable \"MACHINE_ARCH\""
		exit 1
	fi

	if [ -z "${WORKDIR}" ];then
		bbfatal_log "Cannot get bitbake variable \"WORKDIR\""
		exit 1
	fi

	if [ -z "${S}" ];then
		bbfatal_log "Cannot get bitbake variable \"TRUSTME_HARDWARE\""
		exit 1
	fi

	if [ -z "${PREFERRED_PROVIDER_virtual/kernel}" ];then
		bbfatal_log "Cannot get bitbake variable \"PREFERRED_PROVIDER_virtual/kernel\""
		exit 1
	fi

	if [ -z "${MACHINE}" ];then
		bbfatal_log "Cannot get bitbake variable \"MACHINE\""
		exit 1
	fi

	if [ -z "${DISTRO}" ];then
		bbfatal_log "Cannot get bitbake variable \"DISTRO\""
		exit 1
	fi

	if [ -z "${TRUSTME_IMAGE_OUT}" ];then
		bbfatal_log "Cannot get bitbake variable \"TRUSTME_IMAGE_OUT\""
		exit 1
	fi

	if [ -z "${TRUSTME_IMAGE_TMP}" ];then
		bbfatal_log "Cannot get bitbake variable \"TRUSTME_IMAGE_TMP\""
		exit 1
	fi

	if [ -z "${TRUSTME_CONTAINER_ARCH}" ];then
		bbfatal_log "Cannot get bitbake variable \"TRUSTME_CONTAINER_ARCH\""
		exit 1
	fi



	rm -fr ${TRUSTME_IMAGE_TMP}
	rm -f "${TRUSTME_IMAGE}"

	machine=$(echo "${MACHINE}" | tr "-" "_")

	bbnote "Starting to create trustme image"
	# create temporary directories
	install -d "${TRUSTME_IMAGE_OUT}"
	install -d "${TRUSTME_BOOTPART_DIR}"
	tmp_modules="${TRUSTME_IMAGE_TMP}/tmp_modules"
	tmp_firmware="${TRUSTME_IMAGE_TMP}/tmp_firmware"
	tmp_datapart="${TRUSTME_IMAGE_TMP}/tmp_data"
	rootfs_datadir="${tmp_datapart}/userdata/"
	tmpdir="${TOPDIR}/tmp_container"
	trustme_fsdir="${TRUSTME_IMAGE_TMP}/filesystems"
	trustme_bootfs="$trustme_fsdir/trustme_bootfs"
	trustme_datafs="$trustme_fsdir/trustme_datafs"

	install -d "${TRUSTME_IMAGE_TMP}"
	rm -fr "${rootfs_datadir}"
	install -d "${rootfs_datadir}"
	rm -fr "${trustme_fsdir}"
	install -d "${trustme_fsdir}"
	rm -fr "${tmp_modules}/"
	install -d "${tmp_modules}/"
	rm -fr "${tmp_firmware}/"
	install -d "${tmp_firmware}/"

	rm -fr "${tmp_firmware}/"
	install -d "${tmp_firmware}/"

	install -d "${rootfs_datadir}/cml/tokens"
	install -d "${rootfs_datadir}/cml/containers_templates"

	# define file locations
	#deploy_dir_container = "${tmpdir}/deploy/images/qemu-x86-64"
	containerarch="${TRUSTME_CONTAINER_ARCH}"
	deploy_dir_container="${tmpdir}/deploy/images/$(echo $containerarch | tr "_" "-")"

	src="${TOPDIR}/../trustme/build/"
	config_creator_dir="${src}/config_creator"
	proto_file_dir="${WORKDIR}/cml/daemon"
	provisioning_dir="${src}/device_provisioning"
	enrollment_dir="${provisioning_dir}/oss_enrollment"
	test_cert_dir="${TOPDIR}/test_certificates"
	cfg_overlay_dir="${src}/config_overlay"

	if ! [ -d "${test_cert_dir}" ];then
		bbfatal_log "Test PKI not generated at ${test_cert_dir}\nIs trustx-cml-userdata built?"
		exit 1
	fi

	# copy files to temp data directory
	bbnote "Preparing files for data partition"

	cp -f "${test_cert_dir}/ssig_rootca.cert" "${rootfs_datadir}/cml/tokens/"
	mkdir -p "${rootfs_datadir}/cml/operatingsystems/"
	mkdir -p "${rootfs_datadir}/cml/containers/"

	if [ -d "${TOPDIR}/../custom_containers" ];then # custom container provided in ${TOPDIR}/../custom_container
		bbnote "Installing custom container and configs to image: ${TOPDIR}/../custom_containers"
		cp -far "${TOPDIR}/../custom_containers/00000000-0000-0000-0000-000000000000.conf" "${rootfs_datadir}/cml/containers_templates/"
		find "${TOPDIR}/../custom_containers/" -name '*os*' -exec cp -afr {} "${rootfs_datadir}/cml/operatingsystems" \;
		cp -f "${TOPDIR}/../custom_containers/device.conf" "${rootfs_datadir}/cml/"
	elif [ -d "${deploy_dir_container}/trustx-guests" ];then # container built in default location
		bbnote "Installing containers from default location ${deploy_dir_container}/trustx-guests"
		cp -far "${deploy_dir_container}/trustx-configs/container/." "${rootfs_datadir}/cml/containers_templates/"
		cp -afr "${deploy_dir_container}/trustx-guests/." "${rootfs_datadir}/cml/operatingsystems"
		cp -f "${deploy_dir_container}/trustx-configs/device.conf" "${rootfs_datadir}/cml/"
	else # no container provided
		bbwarn "It seems that no containers were built in directory ${deploy_dir_container}. You will have to provide at least c0 manually!"
		cp ${cfg_overlay_dir}/${TRUSTME_HARDWARE}/device.conf "${rootfs_datadir}/cml/"
	fi

	# sign container configs
	find "${rootfs_datadir}/cml/containers_templates" -name '*.conf' -exec bash \
		${enrollment_dir}/config_creator/sign_config.sh {} \
		${TEST_CERT_DIR}/ssig_cml.key ${TEST_CERT_DIR}/ssig_cml.cert \;

	# copy modules to data partition directory
	kernelabiversion="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"
	if [ -d "${DEPLOY_DIR_IMAGE}/cml-kernel/modules/lib/modules/${kernelabiversion}" ]; then
		rm -rf "${tmp_modules}/lib/modules"
		mkdir -p "${tmp_modules}/lib"
		unsquashfs -d "${tmp_modules}/lib/modules" "${DEPLOY_DIR_IMAGE}/cml-kernel/modules-${MODULE_TARBALL_LINK_NAME}.squashfs"
		cp -rf ${DEPLOY_DIR_IMAGE}/cml-kernel/modules/lib/modules/${kernelabiversion}/* "${tmp_modules}/lib/modules/${kernelabiversion}/"
		bbnote "Updating modules dependencies for kernel $kernelabiversion"
		sh -c "cd \"${tmp_modules}\" && depmod --basedir \"${tmp_modules}\" ${kernelabiversion}"
		mksquashfs "${tmp_modules}/lib/modules" "${DEPLOY_DIR_IMAGE}/cml-kernel/modules-${MODULE_TARBALL_LINK_NAME}.squashfs" -noappend
	fi
	cp -fL "${DEPLOY_DIR_IMAGE}/cml-kernel/modules-${MODULE_TARBALL_LINK_NAME}.squashfs" "${tmp_datapart}/modules.img"

	# copy firmware to data partition directory
	bbnote "Copying linux-firmware"
	cp -fL "${DEPLOY_DIR_IMAGE}/trustx-cml-firmware-${MACHINE}.squashfs" "${tmp_datapart}/firmware.img"

	# copy kernel update files to data partition directory
	bbnote "Copying kernel update files"
	if ! [ -z "${UPDATE_FILES}" ];then
		for update_file in ${UPDATE_FILES}; do
			if [ -L $update_file ]; then
				real_update_file=$(readlink -f $update_file)
			else
				real_update_file=$update_file
			fi
			cp -fr "$real_update_file" "${rootfs_datadir}/cml/operatingsystems"
		done
	fi

	# copy trustme files to image deploy dir
	cp -afr "${tmp_datapart}/." "${TRUSTME_IMAGE_OUT}/trustme_datapartition"

	# Create boot partition and mark it as bootable
	bootpart_size_targetblocks="$(du --block-size=${TRUSTME_TARGET_ALIGN} -s ${TRUSTME_BOOTPART_DIR} | awk '{print $1}')"
	bootpart_size_targetblocks="$(echo ${bootpart_size_targetblocks} '*' ${TRUSTME_BOOTPART_EXTRA_FACTOR} '/' 1 | bc)"
	bootpart_size_bytes="$(expr $bootpart_size_targetblocks '*' ${TRUSTME_TARGET_ALIGN})"
	# append space to bootpart if TRUSTME_TARGET_ALIGN < 1024 (mkdisfs block size)
	if ! [ "$(expr $bootpart_size_bytes '%' 1024)"="0" ]; then
		echo "Appending boot partition size to match mkdosfs blocksize of 1024"
		bootpart_size_bytes="$(expr $bootpart_size_bytes + '(' $bootpart_size_bytes '%' 1024 ')')"
		bootpart_size_targetblocks="$(${bootpart_size_bytes} '/' ${TRUSTME_TARGET_ALIGN})"
	fi
	bbnote "Boot files size $bootpart_size_bytes) bytes"


	bootpart_size_1k="$(expr $bootpart_size_bytes '/' 1024)"

	datapart_size_targetblocks="$(du --block-size=${TRUSTME_TARGET_ALIGN} -s ${tmp_datapart} | awk '{print $1}')"
	datapart_size_targetblocks="$(echo ${datapart_size_targetblocks} '+' '(' ${TRUSTME_DATAPART_EXTRA_SPACE} '*' 1024 '/' 4 ')' '+' 1 | bc)"
	datapart_size_bytes="$(expr $datapart_size_targetblocks '*' ${TRUSTME_TARGET_ALIGN})"
	datafolder_size="$(expr $datapart_size_targetblocks '*' ${TRUSTME_TARGET_ALIGN})"
	bbnote "Data files size: $datafolder_size bytes"

	##### create filesystems #####

	# creating boot filesystem
	bbnote "Creating boot filesystem ${trustme_bootfs}"
	rm -f "$trustme_bootfs"
	mkdosfs -n boot -C "$trustme_bootfs" "$bootpart_size_1k"

	MTOOLS_SKIP_CHECK=1 mcopy -i "$trustme_bootfs" -s ${TRUSTME_BOOTPART_DIR}/* ::/
	chmod 644 "$trustme_bootfs"


	# creating data filesystem
	bbnote "Creating data filesystem ${trustme_datafs}"
	bbdebug 1 "dd'ing data fs: ${datapart_size_targetblocks} 4K blocks, $(expr ${datapart_size_targetblocks} '*' ${TRUSTME_TARGET_ALIGN}) bytes"
	dd if=/dev/zero of="$trustme_datafs" conv=notrunc,fsync iflag=sync oflag=sync bs=${TRUSTME_TARGET_ALIGN} count=$datapart_size_targetblocks
	/bin/sync
	bbdebug 1 "Creating ext4 fs of size ${datapart_size_targetblocks} blocks, ${datapart_size_bytes} bytes on file $trustme_datafs"
	mkfs.ext4 -b ${TRUSTME_TARGET_ALIGN} -d "$tmp_datapart" -L trustme "$trustme_datafs" "${datapart_size_targetblocks}"
	chmod 644 "$trustme_datafs"

	/bin/sync

	##### Create empty image and partition table #####
	bootimg_size_targetblocks="$(du -Ls --block-size=${TRUSTME_TARGET_ALIGN} "$trustme_bootfs" | awk '{print $1}')"
	bootimg_size_bytes="$(expr $bootimg_size_targetblocks '*' ${TRUSTME_TARGET_ALIGN})"
	dataimg_size_targetblocks="${datapart_size_targetblocks}"
	dataimg_size_bytes="$(expr $dataimg_size_targetblocks '*' ${TRUSTME_TARGET_ALIGN})"

	bbdebug 1 "Filesystem sizes:\nbootimg_size_targetblocks $bootimg_size_targetblocks\nbootimg_size_bytes $bootimg_size_bytes\ndataimg_size_targetblocks $dataimg_size_targetblocks\ndataimg_size_bytes $dataimg_size_bytes"
	##### calc start/end of partitions #####
	# cals start/end of boot partition
	start_bootpart="$(expr 34 '*' ${TRUSTME_TARGET_SECTOR_SIZE})"
	start_bootpart="$(expr $start_bootpart + '(' $start_bootpart '%' ${TRUSTME_TARGET_ALIGN} ')' )"
	start_bootpart_targetblocks="$(expr $start_bootpart '/' ${TRUSTME_TARGET_ALIGN})"
	end_bootpart="$(expr $start_bootpart + $bootpart_size_bytes)"

	end_bootpart_target_blocks="$(expr $start_bootpart + $bootpart_size_bytes)"

	# calc start/end of data partition
	if [ "$(expr $end_bootpart '%' ${TRUSTME_TARGET_ALIGN})"="0" ]; then
		start_datapart="$(expr $end_bootpart + '(' $end_bootpart '%' ${TRUSTME_TARGET_ALIGN} ')' + ${TRUSTME_TARGET_ALIGN} )"
		#bbnote "$(echo "Start datapartition: ${start_datapart}appended ${TRUSTME_TARGET_ALIGN} bytes\")"
	else
		start_datapart="$(expr $end_bootpart + '(' $end_bootpart '%' ${TRUSTME_TARGET_ALIGN} ')')"
		#bbnote "Start datapartition: ${start_datapart}appended $(expr $end_bootpart '%' ${TRUSTME_TARGET_ALIGN}) bytes"
	fi

	start_datapart_targetblocks="$(expr $start_datapart '/' ${TRUSTME_TARGET_ALIGN})"

	end_datapart="$(expr $start_datapart + $dataimg_size_bytes)"
	#end_datapart="$(expr $end_datapart + '(' $end_datapart '%' ${TRUSTME_TARGET_ALIGN} ')' + 10000 '*' ${TRUSTME_TARGET_ALIGN})"

	img_size_targetblocks="$(expr '(' $end_datapart + '(' $end_datapart '%' ${TRUSTME_TARGET_ALIGN} ')' + 34 '*' ${TRUSTME_TARGET_ALIGN} + 10000 '*' ${TRUSTME_TARGET_ALIGN} ')' '/' ${TRUSTME_TARGET_ALIGN})"
	img_size="$(expr $img_size_targetblocks '*' ${TRUSTME_TARGET_ALIGN})"


	##### Create partitions #####
	bbdebug 1 "Creating empty image file"

	rm -f ${TRUSTME_IMAGE}
	dd if=/dev/zero of=${TRUSTME_IMAGE} bs=${TRUSTME_TARGET_ALIGN} count=$img_size_targetblocks conv=notrunc,fsync iflag=sync oflag=sync status=progress
	/bin/sync
	sleep 2
	tmp_img_size="$(du --block-size=1 ${TRUSTME_IMAGE})"
	if ! [ "$tmp_img_size"="$img_size" ];then
		bbfatal_log "Image size should be $img_size but is $tmp_img_size. Aborting..."
	else
		bbnote "Sucessfully verified size of ${TRUSTME_IMAGE}"
	fi

	bbnote "Creating partition table:"
	bbnote  "Building image using ${TRUSTME_PARTTABLE_TYPE} partition table"
	parted -s "${TRUSTME_IMAGE}" unit B --align none mklabel ${TRUSTME_PARTTABLE_TYPE}
	bbnote  "created label"

	if [ "${TRUSTME_PARTTABLE_TYPE}" = "gpt" ];then
		bbnote  "Moving second header on ${TRUSTME_PARTTABLE_TYPE} image"
		sgdisk --move-second-header "${TRUSTME_IMAGE}"
	fi

	# Create boot partition
	parted -s ${TRUSTME_IMAGE} unit B --align none mkpart primary ${TRUSTME_BOOTPART_FS} "${start_bootpart}B" "${end_bootpart}B"
	sync
	partprobe
	bbnote "Created boot partition"

	parted -s ${TRUSTME_IMAGE} unit B --align none print

	# Create data partition
	parted -s ${TRUSTME_IMAGE} unit B --align none mkpart primary "${TRUSTME_BOOTPART_FS}" "${start_datapart}B" "${end_datapart}B"
	sync
	partprobe
	bbnote "Created data partition"

	if [ "${TRUSTME_PARTTABLE_TYPE}" = "gpt" ];then
		parted -s ${TRUSTME_IMAGE} name 1 boot
		parted -s ${TRUSTME_IMAGE} set 1 legacy_boot on
		parted -s ${TRUSTME_IMAGE} set 1 msftdata  on
		parted -s ${TRUSTME_IMAGE} set 1 boot off
		parted -s ${TRUSTME_IMAGE} set 1 esp off
		sync
		partprobe


		parted -s ${TRUSTME_IMAGE} name 2 trustme
		parted -s ${TRUSTME_IMAGE} set 2 legacy_boot off
		parted -s ${TRUSTME_IMAGE} set 2 msftdata  off
		parted -s ${TRUSTME_IMAGE} set 2 boot off
		parted -s ${TRUSTME_IMAGE} set 2 esp off
		sync
		partprobe

		bbnote "Done setting Set partiion names and flags"
	fi


	bbnote "Copying filesystems to partitions"
	bbdebug 1 "Sizes:\nimg_size: ${img_size}\nimg_size_targetblocks: ${img_size_targetblocks}\nstart_datapart=${start_datapart}\n end_datapart: ${end_datapart}\nstart_bootpart=${start_bootpart}\n end_bootpart: ${end_bootpart}\nbootimg_size_bytes: ${bootimg_size_bytes}\ndataimg_size_bytes: ${dataimg_size_bytes}"

	bbnote "Copying boot filesystem to partition"
	dd if=${trustme_bootfs} of=${TRUSTME_IMAGE} bs=${TRUSTME_TARGET_ALIGN} count=${bootimg_size_targetblocks} seek=${start_bootpart_targetblocks} conv=notrunc,fsync iflag=sync oflag=sync status=progress
	/bin/sync
	partlayout="$(parted ${TRUSTME_IMAGE} unit B --align none print 2>&1)"
	bbdebug 1 "${partlayout}"

	#sgdisk --print ${TRUSTME_IMAGE}

	bbnote "Copying data filesystem to partition"
	bbdebug 1 "img_size(planned): ${img_size}, img_size (real): $(du --block-size=1 ${TRUSTME_IMAGE})\nimg_size_targetblocks: ${img_size_targetblocks}, start_datapart=${start_datapart}, end_datapart: ${end_datapart}"

	# TODO host tool
	/bin/sync
	dd if=${trustme_datafs} of=${TRUSTME_IMAGE} bs=${TRUSTME_TARGET_ALIGN} count=${dataimg_size_targetblocks} seek=${start_datapart_targetblocks} conv=notrunc,fsync iflag=sync oflag=sync status=progress

	partlayout="$(parted ${TRUSTME_IMAGE} unit B --align none print 2>&1)"
	bbnote "Final partition layout:\n${partlayout}"

	checkfs="$(cmp ${trustme_datafs} ${TRUSTME_IMAGE} --bytes=${dataimg_size_bytes} --ignore-initial=0:$(expr ${start_datapart_targetblocks} '*' ${TRUSTME_TARGET_ALIGN}))"

	if [ "${checkfs}"="" ];then
		bbnote "Sucessfully verified integrity of data filesystem"
	else
		bbfatal_log "Failed to verify integrity of data filesystem. Aborting..."
	fi

	checkfs="$(cmp ${trustme_bootfs} ${TRUSTME_IMAGE} --bytes=${bootimg_size_bytes} --ignore-initial=0:$(expr ${start_bootpart_targetblocks} '*' ${TRUSTME_TARGET_ALIGN}))"

	if [ "${checkfs}"="" ];then
		bbnote "Sucessfully verified integrity of boot filesystem"
	else
		bbfatal_log "Failed to verify integrity of boot filesystem. Aborting..."
	fi

	bbnote "Successfully created trustme image at ${TRUSTME_IMAGE}"
}

do_deploy () {
	install -d "${DEPLOYDIR}"
	cp -r "${TRUSTME_IMAGE_OUT}" "${DEPLOYDIR}"
}
addtask do_deploy after do_image_complete

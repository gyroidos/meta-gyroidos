inherit image_types
inherit kernel-artifact-names
inherit p11-signing

#
# Create an partitioned gyroidos image that can be dd'ed to the boot medium
#


TEST_CERT_DIR = "${TOPDIR}/test_certificates"
SECURE_BOOT_SIGNING_KEY = "${TEST_CERT_DIR}/ssig_subca.key"
SECURE_BOOT_SIGNING_CERT = "${TEST_CERT_DIR}/ssig_subca.cert"

GYROIDOS_IMAGE_OUT="${B}/gyroidos_image"
GYROIDOS_IMAGE="${GYROIDOS_IMAGE_OUT}/gyroidosimage.img"

GYROIDOS_GENERIC_DEPENDS = " \
    gyroidos-cml-initramfs:do_image_complete \
    gyroidos-cml-modules:do_image_complete \
    gyroidos-cml-firmware:do_image_complete \
    virtual/kernel:do_deploy \
"

DEPENDS += "e2fsprogs-native bc-native"

install_ssig_rootca () {
	if [ -z "${GUESTOS_SIG_ROOT_CERT}" ];then
		bbfatal "GUESTOS_SIG_ROOT_CERT is not set. Set GUESTOS_SIG_ROOT_CERT in local.conf."
	fi

	if is_pkcs11_uri ${GUESTOS_SIG_ROOT_CERT}; then
		extract_cert "${GUESTOS_SIG_ROOT_CERT}" "${rootfs_datadir}/cml/tokens/ssig_rootca.cert"
	else
		if ! [ -f "${GUESTOS_SIG_ROOT_CERT}" ];then
			bbfatal_log "Root certificate not generated at ${GUESTOS_SIG_ROOT_CERT}."
			exit 1
		fi
		cp -f "${GUESTOS_SIG_ROOT_CERT}" "${rootfs_datadir}/cml/tokens/ssig_rootca.cert"
	fi
}

do_rootfs () {
	if [ -z "${GYROIDOS_IMAGE_OUT}" ];then
		bbfatal_log "Cannot get bitbake variable \"GYROIDOS_IMAGE_OUT\""
		exit 1
	fi

	if [ -z "${GYROIDOS_GUESTOS_ARCH}" ];then
		bbfatal_log "Cannot get bitbake variable \"GYROIDOS_GUESTOS_ARCH\""
		exit 1
	fi

	rm -f "${GYROIDOS_IMAGE}"

	machine=$(echo "${MACHINE}" | tr "-" "_")

	bbnote "Starting to create gyroidos image"
	# create temporary directories
	install -d "${GYROIDOS_IMAGE_OUT}"
	rootfs="${IMAGE_ROOTFS}"
	rootfs_datadir="${rootfs}/userdata/"
	tmpdir="${TOPDIR}/tmp_container"

	rm -fr "${rootfs}/"
	install -d "${rootfs}/"
	rm -fr "${rootfs_datadir}"
	install -d "${rootfs_datadir}"

	install -d "${rootfs_datadir}/cml/tokens"
	install -d "${rootfs_datadir}/cml/containers_templates"

	# define file locations
	#deploy_dir_container = "${tmpdir}/deploy/images/qemu-x86-64"
	guestosarch="${GYROIDOS_GUESTOS_ARCH}"
	deploy_dir_container="${tmpdir}/deploy/images/$(echo $guestosarch | tr "_" "-")"

	src="${TOPDIR}/../gyroidos/build/"
	config_creator_dir="${src}/config_creator"
	proto_file_dir="${WORKDIR}/cml/daemon"
	provisioning_dir="${src}/device_provisioning"
	enrollment_dir="${provisioning_dir}/oss_enrollment"
	cfg_overlay_dir="${src}/config_overlay"
	device_cfg="${WORKDIR}/device.conf"

	# copy files to temp data directory
	bbnote "Preparing files for data partition"

	install_ssig_rootca
	mkdir -p "${rootfs_datadir}/cml/operatingsystems/"
	mkdir -p "${rootfs_datadir}/cml/containers/"

	if [ -d "${TOPDIR}/../custom_containers" ];then # custom container provided in ${TOPDIR}/../custom_container
		bbnote "Installing custom container and configs to image: ${TOPDIR}/../custom_containers"
		cp -far "${TOPDIR}/../custom_containers/00000000-0000-0000-0000-000000000000.conf" "${rootfs_datadir}/cml/containers_templates/"
		find "${TOPDIR}/../custom_containers/" -name '*os*' -exec cp -afr {} "${rootfs_datadir}/cml/operatingsystems" \;
		cp -f "${TOPDIR}/../custom_containers/device.conf" "${rootfs_datadir}/cml/"
	elif [ -d "${deploy_dir_container}/gyroidos-guests" ];then # container built in default location
		bbnote "Installing containers from default location ${deploy_dir_container}/gyroidos-guests"
		cp -far "${deploy_dir_container}/gyroidos-configs/container/." "${rootfs_datadir}/cml/containers_templates/"
		cp -afr "${deploy_dir_container}/gyroidos-guests/." "${rootfs_datadir}/cml/operatingsystems"
		cp -f "${device_cfg}" "${rootfs_datadir}/cml/"
	else # no container provided
		bbwarn "It seems that no containers were built in directory ${deploy_dir_container}. You will have to provide at least c0 manually!"
		cp -f "${device_cfg}" "${rootfs_datadir}/cml/"
	fi

	# sign container configs
	find "${rootfs_datadir}/cml/containers_templates" -name '*.conf' -exec bash \
		${enrollment_dir}/config_creator/sign_config.sh {} \
		${TEST_CERT_DIR}/ssig_cml.key ${TEST_CERT_DIR}/ssig_cml.cert \;

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
}

ROOTFS_PREPROCESS_COMMAND = ""

do_rootfs[depends] += " ${GYROIDOS_GENERIC_DEPENDS} "

IMAGE_POSTPROCESS_COMMAND:append = " deploy_gyroidosimage; "

deploy_gyroidosimage () {
	ln -sf "../${IMAGE_LINK_NAME}.wic" "${GYROIDOS_IMAGE_OUT}/gyroidosimage.img"
	ln -sf "../${IMAGE_LINK_NAME}.wic.bmap" "${GYROIDOS_IMAGE_OUT}/gyroidosimage.img.bmap"

	# deploy rootfs contents for installer build
	cp -r "${IMAGE_ROOTFS}" "${GYROIDOS_IMAGE_OUT}/gyroidos_datapartition"

	cp -r "${GYROIDOS_IMAGE_OUT}" "${IMGDEPLOYDIR}"
}

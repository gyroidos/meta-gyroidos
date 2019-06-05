inherit image
inherit trustmeimage

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="trustmeimage"

do_uefi_sign() {
    for i in `find ${DEPLOY_DIR}/ -name '${SIGNING_BINARIES}'`; do
        if [ -L $i ]; then
            link=`readlink ${i}`
            ln -sf ${link}.signed ${i}.signed
        fi
        sbsign --key ${SECURE_BOOT_SIGNING_KEY} --cert ${SECURE_BOOT_SIGNING_CERT} --output ${i}.signed ${i}
    done
}

do_image_trustmeimage[nostamp] = "1"
do_trustme_bootpart[nostamp] = "1"
do_uefi_sign[depends] = "virtual/kernel:do_deploy"

do_trustme_bootpart[depends] += "trustx-cml-initramfs:do_image_complete"
do_trustme_bootpart[depends] += "virtual/kernel:do_deploy"

addtask do_uefi_sign before do_image_trustmeimage

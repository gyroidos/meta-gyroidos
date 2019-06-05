inherit image
inherit trustmeimage

KERNELVERSION="$(cat "${STAGING_KERNEL_BUILDDIR}/kernel-abiversion")"

DEPENDS += "coreutils-native"

IMAGE_FSTYPES="trustmeimage"

do_image_trustmeimage[nostamp] = "1"
do_trustme_bootpart[nostamp] = "1"

do_trustme_bootpart[depends] += "trustx-cml-initramfs:do_image_complete"
do_trustme_bootpart[depends] += "virtual/kernel:do_uefi_sign"

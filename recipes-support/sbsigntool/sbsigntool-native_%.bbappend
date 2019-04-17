# overwrite clashing dependency to openssl10-native
PREMIRROR += "git://kernel.googlesource.com/pub/scm/linux/kernel/git/jejb/sbsigntools.git;protocol=https;name=sbsigntools"
DEPENDS = "binutils-native gnu-efi-native help2man-native openssl-native util-linux-native"
#SRCREV_sbsigntools = "216dbd3331a7e14ff79cc4dd68c29896f1152ae4"
SRCREV_sbsigntools = "cbbafe244baee231f1726d86af634b6680f9629e"
#PV = "0.9-git${SRCPV}"

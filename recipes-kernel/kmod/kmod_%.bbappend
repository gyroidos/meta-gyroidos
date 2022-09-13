# We generate our pki including kernel signing keys in package pki-native
# Since we cannot generically bbappend all kernels to set the dependecy,
# we use th kmod package here. The kernel.bbclass depends on the kmod-native
# package. Hence, if we append this, all kernels using kernel.bbclass
# transitively will depend on pki-native though kmod-native and our
# kernel signing keys will be generated before kernel build.

DEPENDS += "pki-native"

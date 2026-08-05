# Ensure a clean build directory so configure results are never stale
do_configure[cleandirs] += "${B}"

# Fix grub configure failure: "cannot link at address 0x2000"
# musl toolchain defaults to PIE, preventing grub from linking boot
# stages at absolute addresses. Pass -no-pie through gcc (not ld directly)
# so gcc translates it to the correct linker flag.
CFLAGS:append = " -fno-PIE"
LDFLAGS:append = " -no-pie"

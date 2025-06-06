TMPDIR = "${TOPDIR}/tmp_container"

DISTRO = "core-ref"

PACKAGE_CLASSES = "package_deb"

EXTRA_IMAGE_FEATURES ?= "debug-tweaks"

USER_CLASSES ?= "buildstats"

PATCHRESOLVE = "noop"

PACKAGECONFIG:append:pn-qemu-native = " sdl"
PACKAGECONFIG:append:pn-nativesdk-qemu = " sdl"

CONF_VERSION = "2"
MACHINE = "qemuarm"

PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"
PREFERRED_PROVIDER_virtual/kernel:core-ref = "linux-dummy"
PREFERRED_PROVIDER_virtual/kernel:linuxstdbase = "linux-dummy"

KERNEL_DEVICETREE = ""

IMAGE_FSTYPES = "cpio.gz ext4"

# Possible provider: cacao-initial-native and jamvm-initial-native
PREFERRED_PROVIDER_virtual/java-initial-native = "cacao-initial-native"

# Possible provider: cacao-native and jamvm-native
PREFERRED_PROVIDER_virtual/java-native = "cacao-native"

# Optional since there is only one provider for now
PREFERRED_PROVIDER_virtual/javac-native = "ecj-bootstrap-native"

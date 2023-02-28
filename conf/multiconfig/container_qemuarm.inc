TMPDIR = "${TOPDIR}/tmp_container"

DISTRO ?= "poky"

PACKAGE_CLASSES ?= "package_rpm"

EXTRA_IMAGE_FEATURES ?= "debug-tweaks"

USER_CLASSES ?= "buildstats"

PATCHRESOLVE = "noop"

PACKAGECONFIG:append:pn-qemu-native = " sdl"
PACKAGECONFIG:append:pn-nativesdk-qemu = " sdl"

CONF_VERSION = "2"
MACHINE = "qemuarm"

PREFERRED_PROVIDER_virtual/kernel = "linux-dummy"
PREFERRED_PROVIDER_virtual/kernel_poky-tiny = "linux-dummy"
PREFERRED_PROVIDER_virtual/kernel_linuxstdbase = "linux-dummy"

DISTRO = "poky"
#DISTRO_FEATURES:append = " virtualization"

IMAGE_FSTYPES = "cpio.gz ext4"

PACKAGE_CLASSES = "package_deb"

# Possible provider: cacao-initial-native and jamvm-initial-native
PREFERRED_PROVIDER_virtual/java-initial-native = "cacao-initial-native"

# Possible provider: cacao-native and jamvm-native
PREFERRED_PROVIDER_virtual/java-native = "cacao-native"

# Optional since there is only one provider for now
PREFERRED_PROVIDER_virtual/javac-native = "ecj-bootstrap-native"

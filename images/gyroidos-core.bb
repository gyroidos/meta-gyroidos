OS_CONFIG = "${THISDIR}/${PN}/${GYROIDOS_HARDWARE}/${PN}os.conf"
CONTAINER_CONFIG_FILE = "00000000-0000-0000-0000-000000000000.conf"
CONTAINER_CONFIG = "${THISDIR}/${PN}/${CONTAINER_CONFIG_FILE}"

inherit gyroid-guestos

IMAGE_FEATURES:append = " allow-empty-password"
IMAGE_FEATURES:append = " empty-root-password"
IMAGE_INSTALL:append = " control"
IMAGE_INSTALL:append = " openvswitch"
IMAGE_INSTALL:append = " tcpdump"

# Install additional packages for debugging purposes if DEVELOPMENT_BUILD is set
DEBUG_PACKAGES = "\
       base-passwd \
       shadow \
       stunnel \
       rattestation \
       openssl-bin \
       gptfdisk \
       parted \
       util-linux-sfdisk \
       util-linux \
       openssh-sshd \
       ssh-keys \
       binutils \
       cmld-dbg \
"

IMAGE_INSTALL:append = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', ' ${DEBUG_PACKAGES}', "",d)}'

EXTRA_IMAGE_FEATURES:append = '${@oe.utils.vartrue('DEVELOPMENT_BUILD', ' tools-debug', "",d)}'

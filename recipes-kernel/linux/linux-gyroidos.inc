SRC_URI += " \
	file://0001-video-logo-GyroidOS-specific-logo.patch \
	file://0001-audit-allow-logging-of-user-events-in-non-initial-na.patch \
	file://module_signing.cfg \
	file://netfilter.cfg \
	file://gyroidos.cfg \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/generic:"

# Composable kernel command line. Layers and machine confs append to
# GYROIDOS_KERNEL_CMDLINE; kernel_do_configure:append (below) sed-patches the
# merged .config with the resulting value. This avoids the merge_config.sh
# last-wins race when multiple layers each ship a .cfg fragment that sets
# CONFIG_CMDLINE to its own complete string. Same resolution pattern as the
# CONFIG_MODULE_SIG_KEY override further down in this file.
#
# - audit=1: baseline policy, used by the audit subsystem.
# - GYROIDOS_KERNEL_CMDLINE_DEV: per-machine debug additions, only included
#   when DEVELOPMENT_BUILD=y. BSP machine confs set this to their debug UART
#   (e.g. "earlycon=uart8250,io,0x2f8 console=ttyS1,115200" for x86 QEMU) so
#   the test harness captures kernel printk on a known serial backend.
GYROIDOS_KERNEL_CMDLINE_DEV ?= ""
GYROIDOS_KERNEL_CMDLINE = "audit=1 ${@oe.utils.vartrue('DEVELOPMENT_BUILD', '${GYROIDOS_KERNEL_CMDLINE_DEV}', '', d)}"

DEPENDS += "squashfs-tools-native openssl-native"

MODULE_IMAGE_SUFFIX = "squashfs"

EXTRA_OEMAKE += " INSTALL_MOD_STRIP=1"
KERNEL_EXTRA_ARGS:prepend = "${PARALLEL_MAKE} "

KERNEL_MODULE_SIG_KEY_CERT ?= ""
KERNEL_SYSTEM_TRUSTED_KEYS ?= ""

inherit p11-signing

kernel_do_configure:prepend() {
	# replace or add the config option depending of if it already exists in .config
	# sed command reference: https://superuser.com/a/976712
	if [ "" != "${KERNEL_MODULE_SIG_KEY_CERT}" ]; then
		sed -i '/^CONFIG_MODULE_SIG_KEY=/{h;s|=.*|="${KERNEL_MODULE_SIG_KEY_CERT}"|};${x;/^$/{s||CONFIG_MODULE_SIG_KEY="${KERNEL_MODULE_SIG_KEY_CERT}"|;H};x}' ${B}/.config
	fi

	# if KERNEL_SYSTEM_TRUSTED_KEYS is a pkcs#11 URI, extract the certificate
	if [ "" != "${KERNEL_SYSTEM_TRUSTED_KEYS}" ]; then
		if is_pkcs11_uri ${KERNEL_SYSTEM_TRUSTED_KEYS}; then
			extract_cert "${KERNEL_SYSTEM_TRUSTED_KEYS}" "${WORKDIR}/KERNEL_SYSTEM_TRUSTED_KEYS.pem"
			sed -i '/^CONFIG_SYSTEM_TRUSTED_KEYS=/{h;s|=.*|="${WORKDIR}/KERNEL_SYSTEM_TRUSTED_KEYS.pem"|};${x;/^$/{s||CONFIG_SYSTEM_TRUSTED_KEYS="${WORKDIR}/KERNEL_SYSTEM_TRUSTED_KEYS.pem"|;H};x}' ${B}/.config
		else
			sed -i '/^CONFIG_SYSTEM_TRUSTED_KEYS=/{h;s|=.*|="${KERNEL_SYSTEM_TRUSTED_KEYS}"|};${x;/^$/{s||CONFIG_SYSTEM_TRUSTED_KEYS="${KERNEL_SYSTEM_TRUSTED_KEYS}"|;H};x}' ${B}/.config
		fi
	fi
}

kernel_do_configure:append() {
	# Commit the assembled GYROIDOS_KERNEL_CMDLINE after merge_config.sh has
	# run, so this wins over any .cfg fragment that also sets CONFIG_CMDLINE.
	# Set CONFIG_CMDLINE_BOOL=y here too (rather than in a .cfg fragment) so
	# the bool that enables the feature and the string that fills it live in
	# one place.
	sed -i '/^# CONFIG_CMDLINE_BOOL is not set/d; /^CONFIG_CMDLINE_BOOL=/d; /^CONFIG_CMDLINE=/d' ${B}/.config
	echo 'CONFIG_CMDLINE_BOOL=y' >> ${B}/.config
	echo 'CONFIG_CMDLINE="${GYROIDOS_KERNEL_CMDLINE}"' >> ${B}/.config
}

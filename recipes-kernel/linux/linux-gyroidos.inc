SRC_URI += " \
	file://0001-video-logo-GyroidOS-specific-logo.patch \
	file://0001-audit-allow-logging-of-user-events-in-non-initial-na.patch \
	file://module_signing.cfg \
	file://netfilter.cfg \
	file://gyroidos.cfg \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/generic:"

# Composable kernel command line. Assembled here from three contributions and
# committed by kernel_do_configure:append (below), which sed-patches the merged
# .config with the resulting value. This avoids the merge_config.sh last-wins
# race when multiple layers each ship a .cfg fragment that sets CONFIG_CMDLINE
# to its own complete string. Same resolution pattern as the
# CONFIG_MODULE_SIG_KEY override further down in this file.
#
# - audit=1: baseline policy, used by the audit subsystem.
# - GYROIDOS_KERNEL_CMDLINE_BASE: per-machine, always-on board arguments (serial
#   console, root device). The append below overwrites CONFIG_CMDLINE wholesale,
#   so a board must contribute its args here rather than via a .cfg/defconfig
#   CONFIG_CMDLINE (which would be lost). BSP layers set this per machine, e.g.
#   GYROIDOS_KERNEL_CMDLINE_BASE:apalis-imx8 = "noinitrd console=ttyLP1,115200"
#   in meta-gyroidos-nxp's linux-toradex bbappend.
# - GYROIDOS_KERNEL_CMDLINE_DEV: per-machine CI serial-capture additions, only
#   included when CI_CONSOLE_REDIRECT=y. BSP machine confs set this to their
#   debug UART (e.g. "earlycon=uart8250,io,0x2f8 console=ttyS1,115200" for x86
#   QEMU) so the test harness captures kernel printk on a known serial backend.
#   Gated on CI_CONSOLE_REDIRECT (not DEVELOPMENT_BUILD) because dev images are
#   deployed to real hardware, where a CI serial backend is wrong/absent.
#
# GYROIDOS_KERNEL_CMDLINE_BOOL selects CONFIG_CMDLINE_BOOL, also committed by the
# append. Defaults to "y" (required on x86 for the built-in CONFIG_CMDLINE to
# take effect). A BSP that must take its command line solely from the bootloader
# sets GYROIDOS_KERNEL_CMDLINE_BOOL = "n" (e.g. per machine), which the append
# honors as "# CONFIG_CMDLINE_BOOL is not set". On arm/arm64/riscv there is no
# such symbol, so the value is moot there.
GYROIDOS_KERNEL_CMDLINE_BASE ?= ""
GYROIDOS_KERNEL_CMDLINE_DEV ?= ""
GYROIDOS_KERNEL_CMDLINE_BOOL ?= "y"

GYROIDOS_KERNEL_CMDLINE = "audit=1 ${GYROIDOS_KERNEL_CMDLINE_BASE} ${@oe.utils.vartrue('CI_CONSOLE_REDIRECT', '${GYROIDOS_KERNEL_CMDLINE_DEV}', '', d)}"

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
	# CONFIG_CMDLINE_BOOL is owned here too (rather than in a .cfg fragment) so
	# the bool that enables the feature and the string that fills it live in one
	# place; its value follows GYROIDOS_KERNEL_CMDLINE_BOOL (default "y").
	sed -i '/^# CONFIG_CMDLINE_BOOL is not set/d; /^CONFIG_CMDLINE_BOOL=/d; /^CONFIG_CMDLINE=/d' ${B}/.config
	if [ "n" = "${GYROIDOS_KERNEL_CMDLINE_BOOL}" ]; then
		echo '# CONFIG_CMDLINE_BOOL is not set' >> ${B}/.config
	else
		echo 'CONFIG_CMDLINE_BOOL=y' >> ${B}/.config
	fi
	echo 'CONFIG_CMDLINE="${GYROIDOS_KERNEL_CMDLINE}"' >> ${B}/.config
}

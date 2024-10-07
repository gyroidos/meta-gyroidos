DEPENDS += "libp11-native opensc-native p11-kit-native openssl-native softhsm-native gnutls-native"

def get_pkcs11_module_path(d):
    backend = d.getVar('PKCS11_BACKEND')
    if backend == "opensc":
        return "${RECIPE_SYSROOT_NATIVE}/usr/lib/opensc-pkcs11.so"
    elif backend == "softhsm":
        return "${RECIPE_SYSROOT_NATIVE}/usr/lib/softhsm/libsofthsm2.so"
    else:
        import bb
        bb.fatal(f"PKCS#11 backend \"{backend}\" not set or unsupported.")

# PKCS#11 backend
PKCS11_BACKEND ?= "opensc"
PKCS11_MODULE_PATH = "${@get_pkcs11_module_path(d)}"

# sbsign and evmctl can't handle certificates passed as PKCS#11 URIs.
# This command provides measures to extract a certificate from a PKCS#11 token.
extract_cert () {
        p11tool --provider ${PKCS11_MODULE_PATH} --export-chain $1 > $2
}

# variables passed to OpenSSL
export OPENSSL_ENGINES = "${RECIPE_SYSROOT_NATIVE}/usr/lib/engines-3"
export PKCS11_MODULE_PATH
export SOFTHSM2_CONF = "${RECIPE_SYSROOT_NATIVE}/etc/softhsm2.conf"

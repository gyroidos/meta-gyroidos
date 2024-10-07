DEPENDS += "libp11-native opensc-native p11-kit-native openssl-native softhsm-native gnutls-native"

python () {
    exists_and_pkcs11 = lambda var: var != None and var.startswith('pkcs11:')
    module_sig_key = d.getVar('KERNEL_MODULE_SIG_KEY', True)
    guestos_sig_key = d.getVar('GUESTOS_SIG_KEY', True)
    if exists_and_pkcs11(module_sig_key) or exists_and_pkcs11(guestos_sig_key):
        overrides = d.getVar('OVERRIDES', False)
        d.setVar('OVERRIDES', overrides + ':pkcs11-sign')
}

PKCS11_MODULE ?= "${RECIPE_SYSROOT_NATIVE}/usr/lib/softhsm/libsofthsm2.so"

export OPENSSL_ENGINES = "${RECIPE_SYSROOT_NATIVE}/usr/lib/engines-3"
export PKCS11_MODULE_PATH = "${PKCS11_MODULE}"
export SOFTHSM2_CONF = "${RECIPE_SYSROOT_NATIVE}/etc/softhsm2.conf"

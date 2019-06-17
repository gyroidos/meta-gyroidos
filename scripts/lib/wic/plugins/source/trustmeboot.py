# Copyright (c) 2014, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# DESCRIPTION
# This implements the 'bootimg-efi' source plugin class for 'wic'
#
# AUTHORS
# Tom Zanussi <tom.zanussi (at] linux.intel.com>
#
#
# This file was adapted from it's original version distributed with poky
# Copyright(c) 2018 Fraunhofer AISEC
# Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
#
# Contact Information:
# Fraunhofer AISEC <trustme@aisec.fraunhofer.de>
#

import logging
import os
import shutil

from wic import WicError
from wic.engine import get_custom_config
from wic.pluginbase import SourcePlugin
from wic.misc import (exec_cmd, exec_native_cmd,
                      get_bitbake_var, BOOTDD_EXTRA_SPACE)

logger = logging.getLogger('wic')

class TrustmeBootPlugin(SourcePlugin):
    """
    Creates Trustme  EFI boot partition.
    Based on bootimg-efi.py
    """

    name = 'trustmeboot'


    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        hdddir = "%s/hdd/boot" % cr_workdir

        install_cmd = "install -d %s/EFI/BOOT/" % hdddir
        exec_cmd(install_cmd)


    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        hdddir = "%s/hdd/boot/" % cr_workdir



        machine = get_bitbake_var("MACHINE_ARCH")

        machine = machine.replace("_","-")

        topdir = get_bitbake_var("TOPDIR")

        deploy_dir_image = get_bitbake_var("DEPLOY_DIR_IMAGE")

        sbsign_sysroot = get_bitbake_var("RECIPE_SYSROOT_NATIVE")
        sbsign_cmd = "{0}/usr/bin/sbsign".format(sbsign_sysroot)

        test_cert_dir = "{0}/test_certificates".format(topdir)
        secure_boot_signing_key = "{0}/ssig_subca.key".format(test_cert_dir)
        secure_boot_signing_cert = "{0}/ssig_subca.cert".format(test_cert_dir)
        kernelbin_link="{0}/bzImage-initramfs-{1}.bin".format(deploy_dir_image, machine)
        kernelbin_path="{0}/{1}".format(deploy_dir_image, os.readlink(kernelbin_link))

        try:
            os.symlink("{0}.signed".format(kernelbin_path), "{0}.signed".format(kernelbin_link))

            sign_cmd='{0} --key "{1}" --cert "{2}" --output "{3}.signed" "{3}"'.format(sbsign_cmd, secure_boot_signing_key, secure_boot_signing_cert, kernelbin_path)
            exec_cmd(sign_cmd)

            try:
                cp_cmd = "cp {0} {1}/EFI/BOOT/BOOTX64.EFI".format(kernelbin_path, hdddir)
                exec_cmd(cp_cmd, True)
            except KeyError:
                raise WicError("error while copying kernel")


        except FileExistsError as e:
            bb.warn("Signed binary symlink already existing, skipping signing")


        
        du_cmd = "du -bks %s" % hdddir
        out = exec_cmd(du_cmd)
        blocks = int(out.split()[0])

        extra_blocks = part.get_extra_block_count(blocks)

        if extra_blocks < BOOTDD_EXTRA_SPACE:
            extra_blocks = BOOTDD_EXTRA_SPACE

        blocks += extra_blocks

        logger.debug("Added %d extra blocks to %s to get to %d total blocks",
                     extra_blocks, part.mountpoint, blocks)

        # dosfs image, created by mkdosfs
        bootimg = "%s/boot.img" % cr_workdir

        dosfs_cmd = "mkdosfs -n efi -C %s %d" % (bootimg, blocks)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        mcopy_cmd = "mcopy -i %s -s %s/* ::/" % (bootimg, hdddir)
        exec_native_cmd(mcopy_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % bootimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % bootimg
        out = exec_cmd(du_cmd)
        bootimg_size = out.split()[0]

        part.size = int(bootimg_size)
        part.source_file = bootimg

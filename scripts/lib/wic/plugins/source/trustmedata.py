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

class TrustmeDataPlugin(SourcePlugin):
    """
    Creates trustme data partition
    Based on bootimg-efi.py
    """

    name = 'trustmedata'


    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        hdddir = "%s/hdd/modules" % cr_workdir

        install_cmd = "install -d %s" % hdddir
        exec_cmd(install_cmd)


    @classmethod
    def do_prepare_partition(cls, part, source_params, creator, cr_workdir,
                             oe_builddir, bootimg_dir, kernel_dir,
                             rootfs_dir, native_sysroot):
        if not kernel_dir:
            kernel_dir = get_bitbake_var("DEPLOY_DIR_IMAGE")
            if not kernel_dir:
                raise WicError("Couldn't find DEPLOY_DIR_IMAGE, exiting")

        hdddir = "%s/hdd/userdata/" % cr_workdir

        workdir = get_bitbake_var("WORKDIR")
        if not workdir:
            raise WicError("Can not get WORKDIR directory - not in cooked mode?")

        deploy_dir_image = get_bitbake_var("DEPLOY_DIR_IMAGE")
        if not deploy_dir_image:
            raise WicError("Can not get DEPLOY_DIR_IMAGE directory - not in cooked mode?")


        topdir = get_bitbake_var("TOPDIR")
        if not topdir:
            raise WicError("Can not get TOPDIR directory - not in cooked mode?")

        D = get_bitbake_var("D")
        if not D:
            raise WicError("Can not get D directory - not in cooked mode?")

        trustme_hardware = get_bitbake_var("TRUSTME_HARDWARE")
        if not trustme_hardware:
            raise WicError("Can not get trustme_hardware directory - not in cooked mode?")

        src = "%s/../trustme/build/" % topdir
        cfg_overlay_dir = "%s/config_overlay" % src
        config_creator_dir = "%s/config_creator" %src
        proto_file_dir = "%s/cml/daemon" % workdir
        provisioning_dir = "%s/device_provisioning" % src
        enrollment_dir = "%s/oss_enrollment" % provisioning_dir
        test_cert_dir = "%s/test_certificates" % topdir

        if not os.path.exists(test_cert_dir):
            cert_gen_cmd = "%s/gen_dev_certs.sh %s" % provisioning_dir, test_cert_dir
            exec_cmd(cert_gen_cmd)

        install_cmd = "install -d %s/cml/tokens" % hdddir 
        exec_cmd(install_cmd)


        cp_cmd = "cp {0}/{1}/device.conf {2}/cml/".format(cfg_overlay_dir, trustme_hardware, hdddir)
        exec_cmd(cp_cmd)

        cp_cmd = "cp {0}/ssig_rootca.cert {1}/cml/tokens/".format(test_cert_dir, hdddir)
        exec_cmd(cp_cmd)
        
        cp_cmd = "cp {0}/gen_rootca.cert {1}/cml/tokens/".format(test_cert_dir, hdddir)
        exec_cmd(cp_cmd)

        
        mkdir_cmd = "mkdir -p %s" % deploy_dir_image
        exec_cmd(mkdir_cmd)

        mkdir_cmd = "mkdir -p %s/cml/operatingsystems/" % hdddir
        exec_cmd(mkdir_cmd)
        
        cp_cmd = "cp -r {0}/trustx-guests {1}/cml/operatingsystems".format(deploy_dir_image, hdddir)
        exec_cmd(cp_cmd)

        du_cmd = "du -B 1024 -s %s" % hdddir
        out = exec_cmd(du_cmd)
        blocks = int(out.split()[0])

        blocks += 2**18

        logger.debug("out: %s, blocksfinal: %d", out, blocks)

        userdataimg = "%s/userdata.img" % cr_workdir

        datadir = "{0}/trustx-guests/".format(deploy_dir_image)

        e2fs_cmd = "mke2fs -t ext4 -b 1024 -L data -d {0} {1} {2}".format(datadir, userdataimg, blocks)
        logger.debug("Executing: %s" % e2fs_cmd)
        exec_native_cmd(e2fs_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % userdataimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % userdataimg
        out = exec_cmd(du_cmd)
        userdataimg_size = out.split()[0]

        part.size = int(userdataimg_size)
        part.source_file = userdataimg

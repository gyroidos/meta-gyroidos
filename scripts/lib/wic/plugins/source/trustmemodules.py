# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
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

class TrustmeModulesPlugin(SourcePlugin):
    """
    Creates trustme kernel modules partition
    Based on bootimg-efi.py
    """

    name = 'trustmemodules'


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

        hdddir = "%s/hdd/modules/" % cr_workdir

        machine = get_bitbake_var('MACHINE_ARCH')

        try:
            cp_cmd = "tar -xzf {0}/modules-{1}.tgz --directory {2}".format(kernel_dir, machine, hdddir)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("error while copying kernel modules")


        
        du_cmd = "du -B 1024 -s %s" % hdddir
        out = exec_cmd(du_cmd)
        blocks = int(out.split()[0])

        blocks += 2**18

        logger.debug("out: %s, blocksfinal: %d", out, blocks)

        modulesimg = "%s/modules.img" % cr_workdir

        dosfs_cmd = "mke2fs -t ext4 -b 1024 -L modules -d %s/lib %s %d" % (hdddir, modulesimg, blocks)
        logger.debug("Executing: %s" % dosfs_cmd)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % modulesimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % modulesimg
        out = exec_cmd(du_cmd)
        modulesimg_size = out.split()[0]

        part.size = int(modulesimg_size)
        part.source_file = modulesimg

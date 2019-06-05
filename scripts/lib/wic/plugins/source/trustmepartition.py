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

import math
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

    name = 'trustmepartition'


    @classmethod
    def do_configure_partition(cls, part, source_params, creator, cr_workdir,
                               oe_builddir, bootimg_dir, kernel_dir,
                               native_sysroot):
        hdddir = "%s/hdd/trustme" % cr_workdir

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

        hdddir = "%s/hdd/trustme/" % cr_workdir

        workdir = get_bitbake_var("WORKDIR")
        if not workdir:
            raise WicError("Can not get WORKDIR directory - not in cooked mode?")

        deploy_dir_image = get_bitbake_var("DEPLOY_DIR_IMAGE")
        if not deploy_dir_image:
            raise WicError("Can not get DEPLOY_DIR_IMAGE directory - not in cooked mode?")


        topdir = get_bitbake_var("TOPDIR")
        if not topdir:
            raise WicError("Can not get TOPDIR directory - not in cooked mode?")

        tmpdir = TMPDIR = "%s/tmp_container" % topdir
        if not tmpdir:
            raise WicError("Can not get TMPDIR directory - not in cooked mode?")

        #machine = get_bitbake_var("MACHINE")
        #if not machine:
        #    raise WicError("Can not get MACHINE - not in cooked mode?")

        deploy_dir_image = "{0}/deploy/images/{1}".format(tmpdir, "qemux86-64")
        if not deploy_dir_image:
            raise WicError("Can not get DEPLOY_DIR_IMAGE directory - not in cooked mode?")

        D = get_bitbake_var("D")
        if not D:
            raise WicError("Can not get D directory - not in cooked mode?")

        trustme_hardware = get_bitbake_var("TRUSTME_HARDWARE")
        if not trustme_hardware:
            raise WicError("Can not get trustme_hardware directory - not in cooked mode?")

        src = "%s/../trustme/build/" % topdir
        config_creator_dir = "%s/config_creator" %src
        proto_file_dir = "%s/cml/daemon" % workdir
        provisioning_dir = "%s/device_provisioning" % src
        enrollment_dir = "%s/oss_enrollment" % provisioning_dir
        test_cert_dir = "%s/test_certificates" % topdir

        if not os.path.exists(test_cert_dir):
            raise WicError("Test PKI not generated at {0}\nIs trustx-cml-userdata built?".format(test_cert_dir))

        ## copying /lib/modules contents

        machine_translated = get_bitbake_var('MACHINE_ARCH')

        #underscores in MACHINE_ARCH are replaced by - in filenames
        machine_translated = machine_translated.replace("_","-")

        kernel_stagingdir = get_bitbake_var("STAGING_KERNEL_BUILDDIR")

        rootfs = get_bitbake_var("IMAGE_ROOTFS")

        versionfile = open(kernel_stagingdir + "/kernel-abiversion", "r")
        kernelversion = versionfile.read().rstrip()
        versionfile.close()
        
        modulesname = "{0}/modules-{1}.tgz".format(kernel_dir, machine_translated)
        modulesname = os.readlink(modulesname)

        install_cmd = "install -d {0}/tmp_modules".format(hdddir)
        exec_cmd(install_cmd)

        try:
            cp_cmd = "tar -xzf {0}/{1} --directory {2}/tmp_modules".format(kernel_dir, modulesname, hdddir)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("error while copying kernel modules")

        try:
            cp_cmd = "/sbin/depmod --basedir \"{1}/tmp_modules\" --config \"{0}/etc/depmod.d\" {2}".format(rootfs, hdddir, kernelversion)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("Failed to execute depmod on modules")

        try:
            cp_cmd = "mv {0}/tmp_modules/lib/modules {0}".format(hdddir)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("Failed to execute depmod on modules")

        try:
            cp_cmd = "rm -fr {0}/tmp_modules".format(hdddir)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("Failed to execute depmod on modules")
        
        ## coyping /data contents

        install_cmd = "install -d %s/userdata/cml/tokens" % hdddir 
        exec_cmd(install_cmd)

        install_cmd = "install -d %s/userdata/cml/containers_templates" % hdddir 
        exec_cmd(install_cmd)

        # copy device config
        cp_cmd = "cp {0}/trustx-configs/device.conf {1}/userdata/cml/".format(deploy_dir_image, hdddir)
        exec_cmd(cp_cmd)


        # copy container configs
        cp_cmd = "cp -ar {0}/trustx-configs/container/. {1}/userdata/cml/containers_templates/".format(deploy_dir_image, hdddir)
        exec_cmd(cp_cmd)


        cp_cmd = "cp {0}/ssig_rootca.cert {1}/userdata/cml/tokens/".format(test_cert_dir, hdddir)
        exec_cmd(cp_cmd)
        
        cp_cmd = "cp {0}/gen_rootca.cert {1}/userdata/cml/tokens/".format(test_cert_dir, hdddir)
        exec_cmd(cp_cmd)

        
        mkdir_cmd = "mkdir -p %s" % deploy_dir_image
        exec_cmd(mkdir_cmd)

        mkdir_cmd = "mkdir -p %s/userdata/cml/operatingsystems/" % hdddir
        exec_cmd(mkdir_cmd)

        mkdir_cmd = "mkdir -p %s/userdata/cml/containers/" % hdddir
        exec_cmd(mkdir_cmd)

        
        cp_cmd = "cp -ar {0}/trustx-guests/. {1}/userdata/cml/operatingsystems".format(deploy_dir_image, hdddir)
        exec_cmd(cp_cmd)

        du_cmd = "du --block-size=4096 -s %s" % hdddir
        out = exec_cmd(du_cmd)
        fs_4kblocks = int(out.split()[0])

        fs_4kblocks += 2**16

        logger.debug("out: %s, 1096 byte blocks: %d", out, fs_4kblocks)

        userdataimg = "%s/userdata.img" % cr_workdir

        mkfs_cmd = "dd if=/dev/zero of={0} bs={1} count={2}".format(userdataimg, "4096", fs_4kblocks)
        exec_cmd(mkfs_cmd, native_sysroot)

        #mkfs_cmd = "mkfs.btrfs --label data --byte-count {0} --rootdir {1} {2}".format(fs_bytes, hdddir, userdataimg)
        mkfs_cmd = "mkfs.ext4 -b 4096 -d {0} -L trustme {1} {2}".format(hdddir, userdataimg, fs_4kblocks)
        logger.debug("Executing: %s" % mkfs_cmd)
        exec_native_cmd(mkfs_cmd, native_sysroot)


        chmod_cmd = "chmod 644 %s" % userdataimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % userdataimg
        out = exec_cmd(du_cmd)
        userdataimg_size = out.split()[0]

        part.size = int(userdataimg_size)
        part.source_file = userdataimg
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

        hdddir = "%s/hdd/" % cr_workdir

        machine_translated = get_bitbake_var('MACHINE_ARCH')

        #underscores in MACHINE_ARCH are replaced by - in filenames
        machine_translated = machine_translated.replace("_","-")

        kernel_stagingdir = get_bitbake_var("STAGING_KERNEL_BUILDDIR")

        rootfs = get_bitbake_var("IMAGE_ROOTFS")

        versionfile = open(kernel_stagingdir + "/kernel-abiversion", "r")
        kernelversion = versionfile.read().rstrip()
        versionfile.close()
        
        modulesname = "{0}/modules-{1}.tgz".format(kernel_dir, machine_translated)
        modulesname = os.readlink(modulesname)

        try:
            cp_cmd = "tar -xzf {0}/{1} --directory {2}".format(kernel_dir, modulesname, hdddir)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("error while copying kernel modules")

        try:
            cp_cmd = "/sbin/depmod --basedir \"{1}\" --config \"{0}/etc/depmod.d\" {2}".format(rootfs, hdddir, kernelversion)
            exec_cmd(cp_cmd, True)
        except KeyError:
            raise WicError("Failed to execute depmod on modules")
        
        du_cmd = "du -B 1 -s %s" % hdddir
        out = exec_cmd(du_cmd)
        size_bytes = int(out.split()[0])

        size_bytes += 2**20

        logger.debug("out: %s, final size: %d", out, size_bytes)

        # create filesystem image 
        modulesimg = "%s/modules.img" % cr_workdir

        dosfs_cmd = "mksquashfs \"{0}/lib/modules/{1}\" {2} -b {3} -noI -noD -noF -noX -all-root  ".format(hdddir, kernelversion, modulesimg, "4096")
        logger.debug("Executing: %s" % dosfs_cmd)
        exec_native_cmd(dosfs_cmd, native_sysroot)

        chmod_cmd = "chmod 644 %s" % modulesimg
        exec_cmd(chmod_cmd)

        du_cmd = "du -Lbks %s" % modulesimg
        out = exec_cmd(du_cmd)
        modulesimg_size = out.split()[0]

        part.size = int(modulesimg_size)
        part.source_file = modulesimg

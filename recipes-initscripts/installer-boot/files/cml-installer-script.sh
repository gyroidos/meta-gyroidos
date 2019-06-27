#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

mkdir -p /proc
mkdir -p /sys

mount -t proc proc /proc
mount -t sysfs sysfs /sys
mount -t devtmpfs none /dev

mkdir -p /dev/shm
mkdir -p /run
mkdir -p /var/run

udevd --daemon

udevadm trigger --action=add
udevadm settle 

sleep 5

mount -a

sleep 5

mount --bind /mnt/modules /lib/modules

mount --bind /mnt/userdata /data

mkdir -p /data/logs


#now modules partition is mounted
udevadm trigger --action=add
udevadm settle

modprobe loop
modprobe btrfs

exec /sbin/init

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

#now modules partition is mounted
udevadm trigger --action=add
udevadm settle

modprobe loop
modprobe btrfs


if [ ! -f "/data/cml/containers/00000000-0000-0000-0000-000000000000.conf" ]; then
	cp /data/cml/containers_templates/00000000-0000-0000-0000-000000000000.conf /data/cml/containers/00000000-0000-0000-0000-000000000000.conf
fi

exec /bin/sh

#if device.cert is not present, start scd to initialize device
if [ ! -f data/cml/tokens/device.cert ]; then
	scd
fi

scd&

cmld&

# give kernel some extra time to setup stuff, so
# we get a clear console for user promt
sleep 2

udevadm control --exit

exec /bin/sh

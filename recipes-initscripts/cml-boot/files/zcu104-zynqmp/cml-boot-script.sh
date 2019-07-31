#!/bin/sh

clear

cat /etc/init_ascii

echo "-- TEST cml init log on tty11 [ready]"
echo "-- waiting for c0 to start ..."

PATH=/sbin:/bin:/usr/sbin:/usr/bin

#exec > /dev/tty11
#exec 2>&1



mkdir -p /proc
mkdir -p /sys

mount -t proc proc /proc
mount -t sysfs sysfs /sys
mount -t devtmpfs none /dev

# do not log kernel messages to console
echo 1 > /proc/sys/kernel/printk

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

echo "AAAAAAAAA" > /dev/tty0
echo "BBBBBBBBB" > /dev/tty1
echo "CCCCCCCCC" > /dev/tty2
echo "DDDDDDDD" > /dev/tty3
echo "EEEEEEEE" > /dev/tty4
echo "FFFFFFFF" > /dev/tty5
echo "GGGGGGGG" > /dev/tty6
echo "HHHHHHH" > /dev/tty7
echo "IIIIIIIIIII" > /dev/tty8
echo "JJJJJJJJ" > /dev/tty9
echo "KKKKKKKKK" > /dev/tty10
echo "LLLLLLLLL" > /dev/tty11
echo "MMMMMMMMMM" > /dev/tty12
echo "NNNNNNNNN" > /dev/ttyS0
echo "OOOOOOOOO" > /dev/ttyS1

exec /bin/sh

if [ -e "/dev/disk/by-label/containers" ]; then
	echo "Found dedicated filesystem for containers, mounting it!"
	mount /dev/disk/by-label/containers /data/cml/containers
fi

if [ ! -f "/data/cml/containers/00000000-0000-0000-0000-000000000000.conf" ]; then
	cp /data/cml/containers_templates/00000000-0000-0000-0000-000000000000.conf /data/cml/containers/00000000-0000-0000-0000-000000000000.conf
fi

if [ -e "/dev/tpm0" ]; then
	echo "Starting TPM/TSS 2.0 Helper Daemon (tpm2d)"
	tpm2d &

	if [ ! -S /run/socket/cml-tpm2d-control ]; then
		echo "Waiting for tpm2d's control interface"
	fi
	while [ ! -S /run/socket/cml-tpm2d-control ]; do
		echo -n "."
		sleep 2
	done
fi

# if device.cert is not present, start scd to initialize device
export PATH="/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin"
if [ ! -f /data/cml/tokens/device.cert ]; then
	echo "--- Provisioning/Installing Mode ---" > /etc/motd
	echo "Starting Security Helpder Daemon (scd) in Provisioning Mode"
	scd
	echo "--- Provisioning/Installing Mode on tty12 [ready]" > /dev/console
else
	echo "Starting Security Helpder Daemon (scd)"
	scd &
	if [ ! -S /run/socket/cml-scd-control ]; then
		echo "Waiting for scd's control interface"
	fi
	while [ ! -S /run/socket/cml-scd-control ]; do
		echo -n "."
		sleep 1
	done

	echo "Starting Compartment Manger Daemon (cmld)"
	cmld &
	echo "-- cml debug console on tty12 [ready]" > /dev/console
fi

udevadm control --exit


exec /sbin/init > /dev/tt11 2>&1

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



SHELL="y"
read -p "Launch a shell? [Y/n]" SHELL

if ["$ACTION" = "y" ];then
	exec /bin/sh
	exit
fi


echo "Available devices: "
ls /dev

DISK_OK="n"

while [ ! "$DISK_OK" = "y" ];do
	DISK=""
	read -p "Type device path for installing trustme or \"shell\" to launch a shell:" DISK

	if [ "$DISK" = "shell" ];then
		exec /bin/sh
		echo "exec failed. Exiting..."
		sleep 100
		exit
	fi

	if [ ! -a "$DISK" ];then
		echo "Given path does not exist."
		continue
	fi

	if [ ! -b "$DISK" ];then
		echo "Given path is not a block-special device."
		continue
	fi

	DISK_OK="y"
done

/data/copy_image_to_disk.sh /data/trustmeimage.img "$DISK"

echo "trustme was sucessfully installed to $DISK. Launching shell to inspect installation"
exec /bin/sh
#parted -s "${TRUSTME_IMAGE}" unit B --align none mklabel gpt
#
#sgdisk --move-second-header "${TRUSTME_IMAGE}"
#
#bootsize="$(du --block-size=1024 -sh/boot/EFI)"
#bootsize="$(expr $boosize + 20000000)"
#sgdisk --set-alignment=4096 --new:1:+0:+${bootsize}K "$DISK"
#sgdisk --set-alignment=4096 --largest-new=2 "$DISK"
#
#mkfs.fat -F 16 "${DISK}${INFIX}1"
#mkfs.ext4 "${DISK}${INFIX}2"
#
#mkdir -p /trustmeinstall/boot
#mdkir -p /trustmeinstall/data
#
#mount "${DISK}${INFIX}1"
#mount "${DISK}${INFIX}2"
#
#cp -r /mnt/modules /trustmeinstall/data/
#cp -r /mnt/userdata /trustmeinstall/data/

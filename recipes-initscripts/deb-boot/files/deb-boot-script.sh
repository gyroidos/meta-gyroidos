#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

# setup hostname and nameserver and wait forever
if [ -x /sbin/cservice ]
then
	/sbin/cservice /bin/sh -c "while true; do sleep 86400; done"
elif [ -x /sbin/cml-service-container ]
then
	/sbin/cml-service-container /bin/sh -c "while true; do sleep 86400; done"
fi

# fallthrough setup and just sleep forever
while true; do sleep 86400; done

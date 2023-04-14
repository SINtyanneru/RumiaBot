#! /bin/sh -x

if type "gradle"; then
	echo "Building..."
	gradle shadowjar
else
	echo "Command not found!"
fi
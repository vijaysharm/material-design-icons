#/bin/bash

repository="git@github.com:google/material-design-icons.git"
localFolder="/tmp/material-design-icons"
destIconFolder="icons/src/main/res/"
sourceIconFolders=('action' 'alert' 'av' 'communication' 'content' 'device' 'editor' 'file' 'hardware' 'image' 'maps' 'navigation' 'notification' 'social' 'toggle');

echo "Removing $destIconFolder"
rm -rf "$destIconFolder"

echo "Creating $destIconFolder"
mkdir -p "$destIconFolder"

echo "Removing $localFolder"
rm -rf "$localFolder"

echo "Cloning $repository into $localFolder"
git clone "$repository" "$localFolder"

for sub in "${sourceIconFolders[@]}"
do
	for drawable in $(find $localFolder/$sub/drawable* -maxdepth 1 -type d)
	do
		echo "Copying $drawable to $destIconFolder"
		cp -R "$drawable" "$destIconFolder"
	done
done 

echo "Done"
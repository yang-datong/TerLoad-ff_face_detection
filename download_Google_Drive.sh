#!/bin/bash
# Download zip dataset from Google Drive
tmp="cookies.txt"
file="terload_ff_detection_arm64-v8a.zip"
fileid='1gazdboqeN86m4XUn-yR2MQEooD-cPzF0'
base_url="https://drive.google.com/uc?export=download"

fun_wget(){
	#cookies=$(wget --quiet --save-cookies $tmp --keep-session-cookies --no-check-certificate "${base_url}&id=\${fileid}" -O- | sed -rn 's/.confirm=([0-9A-Za-z_]+)./\1\n/p')
	#wget --no-check-certificate "${base_url}&id=${fileid}" -O ${filename}
	#wget --load-cookies $tmp "${base_url}&confirm=${cookies}&id=${fileid}" -O ${file}
	wget "${base_url}&confirm=${cookies}&id=${fileid}" -O ${file}
}

fun_curl(){
	#curl -L -o ${filename} "https://drive.google.com/uc?export=download&id=${fileid}"
	curl -c $tmp -s -L "${base_url}&id=${fileid}" > /dev/null
	curl -Lb $tmp "${base_url}&confirm=`awk '/download/ {print $NF}' $tmp`&id=${fileid}" -o ${file}
}

fun_wget
#fun_curl
if [ -f $tmp ];then
	rm $tmp
fi

unzip ./terload_ff_detection_arm64-v8a.zip -d ./app/src/main/jniLibs/

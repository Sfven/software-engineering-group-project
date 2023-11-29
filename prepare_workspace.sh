#!/bin/sh

# Run this script if you have cloned the repo for the first time. It will download JavaFX.

curl -o jfx.zip https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_linux-x64_bin-sdk.zip
unzip jfx.zip
rm jfx.zip


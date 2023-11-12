#!/bin/bash
mkdir ./build/lib
cp ./build/libs/*-all.jar ./build/lib
mkdir ./build/documentation
./build/scriptsShadow/hairball src/main/hairball/html.hairball src/main/hairball/md.hairball src/main/documentation/readme.hairball | tail -n +2 >README.md

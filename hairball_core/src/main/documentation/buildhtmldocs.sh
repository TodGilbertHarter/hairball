#!/bin/bash
mkdir ./build/lib
cp ./build/libs/*-all.jar ./build/lib
mkdir ./build/documentation
./build/scriptsShadow/hairball src/main/hairball/html.hairball src/main/documentation/documentation.hairball | tail -n +2 >build/documentation/hairball.html
cp src/main/documentation/hairball.css ./build/documentation/hairball.css

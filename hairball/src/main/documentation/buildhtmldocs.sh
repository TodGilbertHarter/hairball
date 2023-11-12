#!/bin/bash
mkdir -p ./build/lib
cp ./build/libs/*-all.jar ./build/lib
mkdir -p ./build/documentation
mkdir -p ./build/docsrc
cp ./src/main/documentation/* ./build/docsrc
./build/scriptsShadow/hairball src/main/hairball/gencontents.hairball build/docsrc/documentation.hairball | tail -n +2 >build/docsrc/toc.hairball
./build/scriptsShadow/hairball src/main/hairball/html.hairball build/docsrc/documentation.hairball | tail -n +2 >build/documentation/hairball.html
cp src/main/documentation/hairball.css ./build/documentation/hairball.css

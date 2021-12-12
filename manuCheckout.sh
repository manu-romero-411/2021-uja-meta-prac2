#!/bin/zsh

git checkout adriDev
git pull
git checkout manuDev
git merge --no-ff adriDev || code .

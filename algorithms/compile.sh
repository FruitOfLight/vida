#!/bin/bash

source=$1
g++ $source -o $source.bin -O2 -std=c++0x

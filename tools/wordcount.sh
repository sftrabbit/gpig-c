#!/usr/bin/env bash

working_directory=$(pwd)
source_directory=${1}

texcount=${source_directory}/tools/texcount/texcount.pl

$texcount -v0 -brief -nosub -noinc -total ${*:2} \
		| sed -e "s/+/ /g" \
		| awk "{ print \$1 }" \
		> wc.tex

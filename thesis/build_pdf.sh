#!/bin/bash

if [[ "$1" == "clean" ]]; then
    rm -f {bachelor-thesis,master-thesis,master-thesis-en}.{bib,aux,log,bbl,bcf,blg,run.xml,toc,tct,pdf,out}
else
    for i in bachelor-thesis; do
        while true; do echo ''; done | xelatex --shell-escape $i
        while true; do echo ''; done | biber   $i
        while true; do echo ''; done | xelatex --shell-escape $i
        while true; do echo ''; done | xelatex --shell-escape $i
    done

    rm -f {bachelor-thesis,master-thesis,master-thesis-en}.{bib,aux,log,bbl,bcf,blg,run.xml,toc,tct,out}
fi

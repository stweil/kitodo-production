#!/bin/sh -x

# Build data for Coverity.

PATH=/home/stefan/cov-analysis-linux64-7.6.0/bin:$PATH

ant clean
rm -rf cov-int
cov-build --dir cov-int ant dist test
#cov-emit-java --dir cov-int --war /home/stefan/src/github/stweil/goobi-production/dist/goobi-ce-1.11.0.war
tar cvJf goobi-production.tar.xz cov-int

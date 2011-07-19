#!/bin/bash

mvn -Pcocoa-macosx-x86_64 assembly:assembly
mvn -Pcocoa-macosx-x86 assembly:assembly
mvn -Pgtk-linux-x86_64 assembly:assembly
mvn -Pgtk-linux-x86 assembly:assembly
mvn -Pwindows-x86_64 assembly:assembly
mvn -Pwindows-x86 assembly:assembly

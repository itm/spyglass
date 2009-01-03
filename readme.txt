
Prerequisites
-------------

To run Spyglass you need at least JRE 6.0

Install instructions
--------------------

Spyglass can be used in two ways:

a) as a standalone application
b) as a plugin inside the iShell tool

Also, since Spyglass uses the SWT framework, you have to
decide at compile-time which platform you want to use.

Building Spyglass is very easy. You don't need any external
libraries besides a working Java SDK and "ant".
The resulting jar files contain all needed libraries (via the
"one-jar" boot-mechanism) and are directly excecutable.


Building Spyglass as a standalone application
---------------------------------------------

Just run 

    $ ant standalone-linux

or

    $ ant standalone-win32


depending on the platform you want to run Spyglass on 


These commands will build the file spyglass-linux-standalone.jar
or spyglass-win32-standalone.jar. This jar file includes all necessary
dependencies and can be run directly via

    $ java -jar spyglass-linux-standalone.jar



Building iShell with Spyglass included as a plugin
---------------------------------------------

Just run 

    $ ant ishell-linux

or

    $ ant ishell-win32


again depending on the platform you want to use 

These commands will build the file ishell-linux-spyglass.jar
or ishell-win32-spyglass.jar. This jar files includes a version of iShell,
the compiled Spyglass source and all libraries both applications depend on.

Similary, it can be run directly via

    $ java -jar ishell-linux-spyglass.jar




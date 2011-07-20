SpyGlass
===============
SpyGlass is an modular and extensible application designed to visualize sensor networks on a screen.
It displays a set of sensor nodes at their location on a map, as well as arbitrary data sent by the
nodes, e.g. connectivity status between nodes or aggregated data collected by the nodes' sensors.

WISEBED Support: SpyGlass now supports the [WISEBED][wisebed] WSN testbeds! Simply connect to a
running experiment from SpyGlass to use it as a packet source for your visualization.

The SpyGlass application itself is a framework, designed to be extendable by plugins that visualize
arbitrary things. In fact, all visualization and data aggregation that is done, is done by a set of
bundled plugins, each fulfilling a certain functionality. The primary focus of SpyGlass is the
visualization of a set of sensor nodes, forming a network. However, SpyGlass can also be used in
other, nearly arbitrary scenarios, where packet based communication is used. This is because
visualization is done based on network packets, which represent statistical, structural or any other
arbitrary data, sent by the nodes to the SpyGlass application. Those packets are then dispatched to
the various plugins, which in turn, interpret the packet, draw things onto the screen when
appropriate and aggregate data contained in the packets. The whole range of visualization
opportunities, delivered by the currently bundled plugins, is described in the
[SpyGlass github Wiki pages][spyglass-github-wiki].

SpyGlass bases on the requests given by the Institute of Telematics (ITM) of the University of
Lübeck and was developed by [Daniel Bimschas][mail-daniel], [Sebastian Ebers][mail-sebastian],
[Dariush Forouher][mail-dariush] and [Oliver Kleine][mail-oliver] in the context of a case study for
professional product development.

Documentation and Issue Tracking
--------------------------------

The issue tracker can be found on our [SpyGlass github project home][spyglass-github-issues]. Please
report bugs there, you only need a valid github account to do so. The documentation can be found in
the [SpyGlass github Wiki pages][spyglass-github-wiki].

Binary Downloads
----------------

Please see the [Downloads page in the github Wiki][spyglass-github-wiki-downloads] for binary
downloads.

Building
--------

Testbed Runtime is based on the [Apache Maven][maven] build system. Clone the project and
simply run 'mvn install' (or 'mvn clean install' to be on the safe side) for building. For building
the various platform specific distributions (SpyGlass is based on SWT) run one of

```
mvn -Pcocoa-macosx-x86_64 assembly:assembly
mvn -Pcocoa-macosx-x86 assembly:assembly
mvn -Pgtk-linux-x86_64 assembly:assembly
mvn -Pgtk-linux-x86 assembly:assembly
mvn -Pwindows-x86_64 assembly:assembly
mvn -Pwindows-x86 assembly:assembly
```

in the ```core``` submodule depending on what your target platform is. This will build an executable
JAR of the standalone SpyGlass version.

License
-------

The project is made open-source under the terms of the BSD license, was created and is
maintained by the Institute of Telematics, University of Luebeck, Germany.

[wisebed]:http://www.wisebed.eu/
[maven]:http://maven.apache.org/
[spyglass-github-issues]:http://github.com/itm/spyglass/issues
[spyglass-github-wiki]:http://github.com/itm/spyglass/wiki
[spyglass-github-wiki-downloads]:http://github.com/itm/spyglass/wiki/Downloads
[mail-daniel]:mailto:bimschas@itm.uni-luebeck.de
[mail-sebastian]:mailto:ebers@itm.uni-luebeck.de
[mail-dariush]:mailto:forouher@iti.uni-luebeck.de
[mail-oliver]:mailto:kleine@itm.uni-luebeck.de

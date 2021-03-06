# Globetrotter

Globetrotter is an experimental HTML5 Canvas based "slippy map" library for [Google Web Toolkit](http://code.google.com/webtoolkit/).

It currently supports [OpenStreetMap](http://www.openstreetmap.org/)-style Z/X/Y layers and marker overlays (other types of layers can be added by extending the Layer class).
Other features, such as WMS support, are forthcoming.

## Prerequisites

- GWT 2.x
- A recent version of [gwt-incubator](http://code.google.com/p/google-web-toolkit-incubator/) (can be copied from `war/WEB-INF/lib` in this project)

## Installation

1. Clone the git repo
1. `mvn package` in the project directory
1. Copy `target/globetrotter-0.0.1-SNAPSHOT.jar` and `war/WEB-INF/lib/gwt-incubator-20100204-r1747.jar` to your project libraries
1. Add the following to your GWT XML descriptor:
    
        <inherits name='teropa.globetrotter.GlobeTrotter'/>

## Usage

See [Demo.java](http://github.com/teropa/globetrotter/blob/master/src/main/java/teropa/globetrotter/client/Demo.java)

## Hacking

Just clone the Git repo and import it to your Eclipse workspace. 
You should have [Google Plugin for Eclipse](http://code.google.com/eclipse/) installed.
 
## License

Copyright (c) 2010 Tero Parviainen

MIT license (see [LICENSE](http://github.com/teropa/globetrotter/blob/master/LICENSE)).

OSMZmiany
=========


OSMZmiany lets you view [OpenStreetMap](http://www.openstreetmap.org) edits in near real time. It downloads minutely diffs from the OpenStreetMap server and then displays the location of edited nodes as points on a map. The colors are as follows:

- red: node deleted
- blue: new node created
- green: existing node modified

OSMZmiany is based on [LiveMapViewer](http://wiki.openstreetmap.org/wiki/LiveMapViewer) but has some additional features:

- Load edits from the past
- Filtering options (user and bounding box)
- Customizable URL to load diffs from
- Display of basic edited object information within the application (user/changeset)
- Ability to fire up either Potlatch2 or JOSM to inspect the area currently in view.


Running OSMZmiany
------------

This application is distributed as an executable .jar file. Most people should just be able to double click on the file after downloading it. Otherwise you can run it from a command line:

    java -jar OSMZmiany-0.0.2.jar

As it runs and accumulates more and more nodes in memory, it will get slower and take up more memory, especially if you are watching the whole world. From time to time you will want to make use of the "Clear" button on the General tab to get a fresh start.

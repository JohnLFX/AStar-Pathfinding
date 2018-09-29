# A-Pathfinding
Simple A* pathfinding algorithm in Java to tranverse between verticies in a bidirected weighted graph.

This program was developed using Java 8. Gradle was used to manage the structure of the project. A gradle wrapper is included in the source code for convience when building the application.

### Compiling

Navigate to the directory "sources".

On Windows,
Issue the command: gradlew.bat build

On Mac/Linux/UNIX,
Issue the command: ./gradlew build

The target file is located at build/libs/IntroToAI_Project1-1.0.jar

### Running

Issue the command: java -jar IntroToAI_Project1-1.0.jar

By default, the program will load connections.txt and locations.txt in the working directory. This can optionally be changed by supplying additional arguments:

java -jar IntroToAI_Project1-1.0.jar [path of locations.txt] [path of connections.txt]

Inputs such as the start city, end city, exclusions, etc. will be prompted by the program during runtime.

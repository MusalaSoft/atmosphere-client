[![Build Status](https://travis-ci.org/MusalaSoft/atmosphere-client.svg?branch=master)](https://travis-ci.org/MusalaSoft/atmosphere-client) [ ![Download](https://api.bintray.com/packages/musala/atmosphere/atmosphere-client/images/download.svg) ](https://bintray.com/musala/atmosphere/atmosphere-client/_latestVersion)  

See our site for better context of this readme. [Click here](http://atmosphereframework.com/)

# atmosphere-client
The client library of the ATMOSPHERE mobile testing framework. This library must be included as a dependency in your project to run ATMOSPHERE tests successfully. More information can be found [here](https://github.com/MusalaSoft/atmosphere-docs).

## Build the project
You can build the project using the included Gradle wrapper by running:
* `./gradlew build` on Linux/macOS
* `gradlew build` on Windows

## Making changes
If you make changes to this project and would like to use your new version in another ATMOSPHERE framework project that depends on this one, after a successful build also run:
* `./gradlew publishToMavenLocal` (Linux/macOS)
* `gradlew publishToMavenLocal` (Windows)

to publish the jar to your local Maven repository. The ATMOSPHERE framework projects are configured to use the artifact published in the local Maven repository first.

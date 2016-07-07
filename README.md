# atmosphere-client
The client library of the ATMOSPHERE mobile testing framework. This library must be included as a dependency in your project to run ATMOSPHERE tests successfully. More information can be found [here](https://github.com/MusalaSoft/atmosphere-docs).

## Project setup
> This project depends on:
* [atmosphere-client-server-lib](https://github.com/MusalaSoft/atmosphere-client-server-lib)
* [atmosphere-bitmap-comparison](https://github.com/MusalaSoft/atmosphere-bitmap-comparison)

>Make sure you publish these projects to your local Maven repository (follow each project's setup instructions).


### Build the project
You can build the project using the included gradle wrapper by running:
* `./gradlew build` on Linux/macOS<br/>
* `gradlew build` on Windows

### Publish to Maven Local
If the build is successful, also run:
* `./gradlew publishToMavenLocal` on Linux/macOS
* `gradlew publishToMavenLocal` on Windows

to publish the jar to the local Maven repository, so other projects that depend on it can use it.

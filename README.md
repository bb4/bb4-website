# applets
This project manages the content deployed to [barrybecker4.com/applets](http://barrybecker4.com/applet_index_en.html).

## What is in this project?

This project contains a lot of miscellaneous stuff that needs to be cleaned up or split out, but the major items are:
- Aikido technique builder application: This is java which reads an xml configuration file and outputs a DHTML application for the web.
- imagebreeder: Allows you to use a genetic algorithm to apply image transformations in a creative way.
- Subprojects:
  - imageproc: Experiments using java2d (derived from code in Java 2d Graphics from Knudsen)
  - webdeployment: code to deploy applets from all my other projects to [my website](http://barrybecker4.com)

## Steps to build applet projects:

1. Building requires java, git, and (optionally) intellij or eclipse.
  - Install java JDK 1.8 or higher from http://www.oracle.com/technetwork/java/javase/downloads.
  - Install the latest Intellij (free community edition version) from http://www.jetbrains.com/idea/
  - Gradle wrapper is used now, so it is not necessary to install gradle.
2. Get the source
  - Do "git clone https://github.com/barrybecker4/applets.git"
  - Set environment variables in Windows or Linux.
    Set JAVA_HOME to the JDK install location (e.g. D:\apps\Program Files\Java\jdk1.8.0_92).
3. Building
  - run "./gradlew" at the root. This will compile all subprojects and run all tests.
  - try running gradle --gui (suffix & if running with cygwin or *nix) to see a list of all tasks in a nice UI.
4. Deploying
  - If you want to deploy to a local apache web-server, you can set the following in webdeployment/gradle.properties
      - codebase=localhost/dist, and
      - distributionDir=<apache install location>/Apache2.2/htdocs
      - after deployement access at http://localhost/dist/applet_index_en.html
  - If you want to deploy to pythons simple webserver, you navigate to webdeployment under applets project, then enter
      - python -m http.server 1337
      - after deployment accesss at http://localhost:1337/dist/applet_index_en.html
  - You must sign all jars that are deployed or applets/webstart will complain.
    This step requires having a keystore with a certificate.
  - First setup a keystore using something like this<br>
   `keytool -genkeypair -dname "cn=Barry G Becker, ou=software, o=barrybecker4, c=US"
 -alias bb4 -keypass <pw> -keystore C:/users/becker/bb4-keystore -storepass <pw> -validity 999999`
  - Then do `./gradlew deploy`
### Using Intellij (recommended)
- run "gradle idea" once to get Intellij files configured to match gradle configuration (If you get an error try "gradle cleanIdea" first).
  If you prefer eclipse you can use "gradle eclipse".
- Update Project Structure | Project SDK so that the JDK 1.8 points to the install location of your JDK.
- In Intellij | project structure, select SDK and compiler output path to be something like "output" (no longer needed).
- Make sure that the language level is set to 1.8.
- In Intellij | settings |compiler, verify ?*.au is in the patterns list, and -ea (enable assertions) is in the JVM param list.
- Run the "deploy task from the gradle panel (on right) within Intellij.
- Run individual programs using the corresponding task in the gradle build window,
  or right click on run* tasks in the build.gradle file to run them as applications.

### Additional sources
  I split out jigo (used by go) and jhlabs (used by image breeder) into separate gradle projects which build separate
  jars because they are based on other people's open source code. In the rare case that you need to modify the source in
  these jars, get them using the following.
  - git clone https://github.com/barrybecker4/jhlabs.git
  - git clone https://github.com/barrybecker4/bb4-sgf.git
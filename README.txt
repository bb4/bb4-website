NOTE: I was forced to make this repository was made public on github because projectLocker no longer allowed me to have a free private repositories. The code in this repository is not clean nor ready for public consumption.

Steps to build applet projects:

I. Building requires java, svn, intellij, gradle.
 - Install java JDK 1.6 or higher from http://www.oracle.com/technetwork/java/javase/downloads.
 - Install the latest Intellij (free community edition version) from http://www.jetbrains.com/idea/
 - Gradle wrapper is used now, so it should not be necessary to install gradle.

II. Get the source
    1. Do "git clone https://github.com/barrybecker4/applets.git"
    4. Set environment variables in Windows or Linux
       Set JAVA_HOME to the JDK install location (e.g. D:\apps\Program Files\Java\jdk1.7.0_12).

III. Building

 - run "gradle" at the root. This will compile all subprojects and run all tests.
 - try running gradle --gui (suffix & if running with cygwin or *nix) to see a list of all tasks in a nuice UI.

option 1 - ant only
 - Verify entries in ant/common.properties and ant/build.xml. No changes should be needed.
    - If you want to deploy to a local webserver, you can set
       codebase=localhost/dist, and
       distributionDir=<apache install location>/Apache2.2/htdocs
 - If you plan to run ant from the command line and not through intellij, you will need to install it.
 - run ant -projecthelp  to see what tasks are available.

option 2 - intellij (recommended)
 - run "gradle idea" once to get Intellij files configured to match gradle configuration.
   (If you get an error try "gradle cleanIdea" first)
 - Follow the option 1 step for building with ant (you do not need to install ant though).
 - Update Project Structure | Project SDK so that the JDK 1.6 points to the install location of your JDK.
 - In Intellij | project structure, select SDK and compiler output path to be something like "output"
 - In Intellij | settings |compiler, verify ?*.au is in the patterns list,
   and -ea (fenable assertions) is in the JVM param list.
 - Run the applets|deploy task from the Ant Build window (on right) within Intellij.
 - Run individual programs using the corresponding task in the Ant Build window, or right click on main files
   in the project view and run them as applications.
 - Gradle tasks can also be run from with intellij by configuring build configurations for them.

Additional sources
  I split out jigo (used by go) and jhlabs (used by image breeder) into separate gradle projects which build separate
  jars because they are based on other peoples open source code. In the rare case that you need to modify the source in
  these jars, get them using the following.
  - svn co https://free1.projectlocker.com/Tesujisoft/jhlabs/svn
  - git clone https://github.com/barrybecker4/bb4-sgf.git
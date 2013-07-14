Steps to build applet projects:

I. Building requires java, svn, intellij, gradle.
 - Install java JDK 1.6 or higher from http://www.oracle.com/technetwork/java/javase/downloads.
 - Install Tortoise SVN (1.7 or higher from http://tortoisesvn.net/)
 - Install Intellij (free community edition version. 12.x or higher from http://www.jetbrains.com/idea/)
 - Install gradle 1.6 or higher (http://www.gradle.org/)

II. Get the source
    1. Create a java_projects/applets directory.
       You can name the directory anything, but the instructions will assume this name.
    2. Right click applets dir and select SVN checkout.
       Url of repo = "https://free1.projectlocker.com/Tesujisoft/applets/svn/trunk".
       Alternatively, run "svn co https://free1.projectlocker.com/Tesujisoft/applets/svn/trunk" from the new applets dir
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
  these jars, do the following.
  - svn co https://free1.projectlocker.com/Tesujisoft/jhlabs/svn  (in java_projects/jhlabs)
  - svn co https://free1.projectlocker.com/Tesujisoft/jigo/svn    (in java_projects/jigo)
Steps to build applet projects:

Initial setup
 - Install Tortoise SVN (1.7 or higher from http://tortoisesvn.net/)
 - Install Intellij (free community edition version. 11.x or higher from http://www.jetbrains.com/idea/)
 - Get the source
    1. create a new java_projects/applets directory and CD into it
    2. run "svn co https://free1.projectlocker.com/Tesujisoft/applets/svn/trunk"
 - Install java JDK 1.6 or higher from http://www.oracle.com/technetwork/java/javase/downloads.
 - The ant tasks rely on foreach which is in ant-contrib.
 - Get ant-contrib-0.6.jar from http://sourceforge.net/projects/ant-contrib/files/ant-contrib/ant-contrib-0.6/
   and drop it into  <JetBrains install location>\IntelliJ IDEA Community Edition 11.1.1\lib\ant\lib
   (note: it downloads as a zip file so pull it out of /lib in the zip)
 - Set PROJECT_HOME to the location of the "trunk" directory (e.g. D:\projects\java_projects\applets)
 - Set JAVA_HOME to the JDK install location (e.g. D:\apps\Program Files\Java\jdk1.6.0_32).

Building
 - Verify entries in ant/common.properties and ant/build.xml. No changes should be needed.
    - If you want to deploy to a local webserver, you can set
       codebase=localhost, and
       distributionDir=<apache install location>/Apache2.2/htdocs
 - Update Project Structure | Project SDK so that the JDK 1.6 points to the install location of your JDK.
 - You should now be able to run the applets/deploy task from the And Build window within Intellij.
 - Run individual programs using the corresponding task in the And Build window, or right click on main files
   in the project view and run them as applications.


Additional source
  I split out jigo (used by go) and jhlabs (used by image breeder) into separate gradle projects which build separate
  jars because they are based on other peoples open source code. In the rare case that you need to modify the source in
  these jars, do the following.
  - svn co https://free1.projectlocker.com/Tesujisoft/jhlabs/svn  (in java_projects/jhlabs)
  - svn co https://free1.projectlocker.com/Tesujisoft/jigo/svn    (in java_projects/jigo)
# bb4-website

This repository contains the content for [http://barrybecker4.com](http://barrybecker4.com). 

## Steps to deploy (using Github Pages)

Simply commit content to /source/html/barrybecker4 and it will appear at https://barrybecker4.org.

The scala software projects can be generated from respective bb4 projects. There is a `deploy` task in each which will produce a `dist` directory - the contents of which can then be copied into barrybecker4/bb4-projects.

### Library dependencies

Here is the dependency structure of the bb4 projects. Each is built independently and deployed to Sonatype maven repository.


       bb4-math    __bb4-common______
             \   /                   \
            bb4-ui             bb4-sound     bb4-A-star
             |                  /  \___________________________________
        bb4-optimization      /     /    \       \      \              \
          /          \      /      /      \       \     bb4-adventure  bb4-aikido-app
    bb4-imageproc     \    /      /        \       \    
      |         bb4-experiments  /          \       \      bb4-expression
      |                         /            \       \       /
      |                   bb4-puzzles   bb4-games    bb4-simulations  
     bb4-image-breeder


All modules are open-source projects in github (https://github.com/bb4)
Pure library projects are `bb4-common`, `bb4-math`, `bb4-expression`, `bb4-ui, bb4-sound`, `bb4-A-star`, `bb4-optimization`, `bb4-imageproc`.

### Additional notes
I split out jigo (used by go) and jhlabs (used by image breeder) into separate gradle projects which build separate
jars because they are based on other people's open source code. In the rare case that you need to modify the source in
these jars, get them using the following.
- git clone https://github.com/barrybecker4/jhlabs.git
- git clone https://github.com/barrybecker4/bb4-sgf.git

## Old Steps to deploy (using InfinityFree)

Use Filezilla (or similar) to copy everything (or changed parts) in source/html to the root of http://barrybecker4.com.
Don't change anything directly on the website itself. Change and test locally before deployment.

![upload to infinity free site](images/filezilla-site.png)

The applications from different `bb4` projects can be deployed to the `bb4-projects` subdirectory under the root of the website. Each project has a gradle "deploy" task that will put everything in a local `dist` directory that can then be ftp'd to the website using Filezilla. If you want to test locally before deploying to the live site, you can copy the bb4-common deployment to the local dist directory so that common files can be found locally.




# Print Bootstrap 3.1 Page in IE8 with "Size To Fit"

Add to CSS

    .container {
        max-width: 97% \9; // IE8 only
    }


# Links

## Enable CPU Profiling for OSGI Container (eg. CQ5)

Edit or create `$tomcatpath/bin/setenv.sh` and add

    CATALINA_OPTS=-Xbootclasspath/p:$JAVA_HOME/lib/visualvm/profiler/lib/jfluid-server.jar org.osgi.framework.bootdelegation=org.netbeans.lib.profiler.*

[More >>](http://blog.knowhowlab.org/2010/03/osgi-tips-osgi-profiling-yourkit.html)

# Create a release fromd develop branch

    git checkout develop
    mvn release:prepare
    mvn release:perform
    git branch release/v0.6 HEAD~1

    git checkout master
    git merge --no-ff release/v0.6
    git push --all && git push --tags
    git checkout develop
    
## More information on gitflow

[Maven Release Gitflow](http://vincent.demeester.fr/2012/07/maven-release-gitflow/)

[git-flow cheatsheet](http://danielkummer.github.io/git-flow-cheatsheet/)

[A successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/)

[Git extensions to provide high-level repository operations for Vincent Driessen's branching model.](https://github.com/nvie/gitflow)
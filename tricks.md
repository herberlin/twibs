

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


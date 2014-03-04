twibs
=====

This is Twibs


# Create a release

    git checkout -b release/v0.1 develop
    mvn release:prepare
    mvn release:perform

    // TODO: Check that this works (next time)
    // cd twibs/target/checkout; git commit --amend -C HEAD; cd ../../.,

    git checkout develop
    git merge --no-ff release/v0.1
    git checkout master
    git merge --no-ff release/v0.1~1
    git push --all && git push --tags

More information is found [here](http://vincent.demeester.fr/2012/07/maven-release-gitflow/)


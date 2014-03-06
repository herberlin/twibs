Twibs
=====

Twibs is a suite of utility classes to ease the development of internet applications with scala.

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

## More information

[Maven Release Gitflow](http://vincent.demeester.fr/2012/07/maven-release-gitflow/)

[A successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/)

[Git extensions to provide high-level repository operations for Vincent Driessen's branching model.](https://github.com/nvie/gitflow)

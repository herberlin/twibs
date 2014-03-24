Twibs
=====

Twibs is a suite of utility classes to ease the development of internet applications with scala.

# Create a release


    git checkout develop
    mvn release:prepare
    mvn release:perform
    git branch release/v0.6 HEAD~1

    git checkout master
    git merge --no-ff release/v0.6
    git push --all && git push --tags
    git checkout develop

## More information

[Maven Release Gitflow](http://vincent.demeester.fr/2012/07/maven-release-gitflow/)

[A successful Git branching model](http://nvie.com/posts/a-successful-git-branching-model/)

[Git extensions to provide high-level repository operations for Vincent Driessen's branching model.](https://github.com/nvie/gitflow)


## Date picker component

Two formats need to be defined:

1. "data-time-format": Java Format described [here](http://download.java.net/jdk8/docs/api/java/time/format/DateTimeFormatter.html). Example "dd.MM.yyy HH:mm"

2. "date-time-format-browser": JavaScript Format described [here](http://www.malot.fr/bootstrap-datetimepicker). Example "yyyy-mm-ddThh:ii:ssZ"


## Deprecations

The use of *joda-time* is deprecated. Use *threeten (backport)* instead. Will be removed soon.



SystemSettings
    host
    starttime
    systemusername
    runmode
    locale

ApplicationSettingsFactory
    defaultApplicationSettings (Reloadable)
    applicationSettingForName(name)  (Reloadable in DEV)

ApplicationSettings
    name
    configuration
    locales
    translators
    defaultUserSettings

UserSettings
    name
    locale

RequestSettings
    locale
    translator
    userSettings
    applicationSettings







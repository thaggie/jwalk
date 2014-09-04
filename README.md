#jwalk
jwalk is a simple tool for looking in directories and archives for class files, typically you'd use it to look in a directory full of jars to find which jar has a named class. It looks inside jar, war and zip archives recursively so it can find a class within a jar inside a war inside a zip for instance.

##Building
```
gradle jar
```

##Running
```
java -jar jwalk.jar {path you want to search} {text to search for in the class name}
```
If you don't specify search text all classes are returned and so can be piped to `grep` or something for more sophisticated matching.

##Example
This is an example of running jwalk straight after `gradle jar` on the build directory (where gradle builds things to) see how it finds the classes in both the classes sub-directory and the jar.

###Command

```
java -jar build/libs/jwalk.jar build Proc
```

###Output

```
/Users/tom/dev/jwalk/build/classes/main/thaggie/jwalk/Main$ThreadLocalZipFileProcessor.class
/Users/tom/dev/jwalk/build/classes/main/thaggie/jwalk/ZipFileProcessor.class
/Users/tom/dev/jwalk/build/libs/jwalk.jar:thaggie/jwalk/Main$ThreadLocalZipFileProcessor.class
/Users/tom/dev/jwalk/build/libs/jwalk.jar:thaggie/jwalk/ZipFileProcessor.class
```

##Run Script
Typically you'd want a script that runs the jar to make it less awkward to call:
```
#!/bin/bash

scriptdir=`dirname "$BASH_SOURCE"`
java -jar $scriptdir/jwalk.jar $@
```
So this would be somewhere in your `PATH` with jwalk.jar in the same directory. 


This woud mean the example above would become:
```
jwalk build Proc
```

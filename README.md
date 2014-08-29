#jwalk
jwalk is a simple tool for looking in directories and archives for class files, typically you'd use it to look in a directory full of jars to find which jar has a named class.

##Building
```
gradle jar
```

##Running
```
java -jar jwalk.jar {path you want to search} | grep ClassName
```

##Example
This is an example of running jwalk straight after `gradle jar` on the build directory (where gradle builds things to) see how it finds the classes in both the classes sub-directory and the jar.

###Command

```
java -jar build/libs/jwalk.jar build | grep Proc
```

###Output

```
/Users/tom/dev/jwalk/build/classes/main/thaggie/jwalk/Main$ThreadLocalZipFileProcessor.class
/Users/tom/dev/jwalk/build/classes/main/thaggie/jwalk/ZipFileProcessor.class
/Users/tom/dev/jwalk/build/libs/jwalk.jar:thaggie/jwalk/Main$ThreadLocalZipFileProcessor.class
/Users/tom/dev/jwalk/build/libs/jwalk.jar:thaggie/jwalk/ZipFileProcessor.class
```

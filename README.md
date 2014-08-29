#jwalk
jwalk a simple tool for looking in directories and archives for class files, typically you'd use it to look in a directory full of jars to find which jar has a named class.

##Building
```
gradle jar
```

##Running
```
java -jar jwalk.jar {path you want to search} | grep ClassName
```


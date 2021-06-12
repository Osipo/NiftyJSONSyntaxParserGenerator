@echo off
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_161
set OPATH=%PATH%
set PATH=%JAVA_HOME%\bin;%PATH%
java -XX:+PrintCommandLineFlags -version
mvn clean package && cd target && del NiftyJSONCompilerGenerator.jar.original && java -XX:+UseG1GC -XX:G1ConcRefinementThreads=4 -XX:GCDrainStackTargetSize=64 -jar NiftyJSONCompilerGenerator.jar --spring.profiles.active=foo & set PATH=%OPATH%
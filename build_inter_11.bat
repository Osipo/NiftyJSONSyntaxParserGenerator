@echo off
cd target
mvn clean package 
&& cd target && del NiftyJSONCompilerGenerator.jar.original 
&& %JAVA_HOME%/bin/java -XX:+UseG1GC -XX:G1ConcRefinementThreads=4 -XX:GCDrainStackTargetSize=64 -jar NiftyJSONCompilerGenerator.jar --spring.profiles.active=foo
@echo off
mvn clean package 
&& cd target
&& del NiftyJSONCompilerGenerator.jar.original
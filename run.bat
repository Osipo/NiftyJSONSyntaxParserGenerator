@echo off
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_161
set OPATH=%PATH%
set PATH=%JAVA_HOME%\bin;%PATH%
cd target
java -jar NiftyJSONCompilerGenerator.jar && set PATH=%OPATH%
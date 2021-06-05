@echo off
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_161
set OPATH=%PATH%
set PATH=%JAVA_HOME%\bin;%PATH%
mvn clean package && cd target && del NiftyJSONCompilerGenerator.jar.original && java -jar NiftyJSONCompilerGenerator.jar --spring.profiles.active=foo & set PATH=%OPATH%
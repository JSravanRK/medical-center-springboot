@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __ MVNW_CMD__=%MAVEN_PROJECTBASEDIR%

@setlocal

@SET MAVEN_PROJECTBASEDIR=%~dp0
@IF NOT "%MAVEN_BASEDIR%"=="" SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%

@SET MVNW_REPOURL=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=

@SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

@FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties") DO (
    @IF "%%A"=="wrapperUrl" SET WRAPPER_URL=%%B
)

@IF NOT EXIST %WRAPPER_JAR% (
    @IF NOT "%MVNW_REPOURL%"=="" (
        SET WRAPPER_URL="%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    )
    @echo Downloading %WRAPPER_URL%
    powershell -Command "&{"^
		"$webclient = new-object System.Net.WebClient;"^
		"if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
		"$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
		"}"^
		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%WRAPPER_URL%', %WRAPPER_JAR%)"^
		"}"
    if "%ERRORLEVEL%"=="0" goto execute
    echo "Failed to download %WRAPPER_URL%"
    exit /B 1
)

:execute
@SET JAVA_EXE=%JAVA_HOME%/bin/java.exe
@IF NOT EXIST "%JAVA_EXE%" (
    @SET JAVA_EXE=java.exe
)

@SET MVN_CMD=%JAVA_EXE% -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %*

@%MVN_CMD%
@IF "%ERRORLEVEL%"=="0" GOTO end
@EXIT /B %ERRORLEVEL%
:end
@endlocal

REM Call to check for DB schema update
for /f "delims=" %%A in ('sqlcmd.exe -s "" -W -h -1 -S "%APPSETTING_db_host%" -U "%APPSETTING_db_user%" -P "%APPSETTING_db_password%" -d "%APPSETTING_db_name%" -Q "exit(set nocount on; select count(*) from sys.tables where name='User')"') do set "DBConfigured=%%A"

if "%DBConfigured%"=="0" (
	echo "configuring database"
	sqlcmd.exe -S "%APPSETTING_db_host%" -U "%APPSETTING_db_user%" -P "%APPSETTING_db_password%" -d "%APPSETTING_db_name%" -i "C:\Users\brhacke\Documents\GitHub\java-webapp-oidc-migrate-poc\dbschema.sql"
) else (
	echo "configuration done"
)

REM Get Ivy to help us repopulate our dependencies
curl -L -O http://search.maven.org/remotecontent?filepath=org/apache/ivy/ivy/2.3.0/ivy-2.3.0.jar

REM Setup dir structure
md "%HOME%\site\repository\target"
md "%HOME%\site\repository\target\oidcpoc"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF\lib"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF\classes"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF\classes\com"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF\classes\com\microsoft"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF\classes\com\microsoft\aad"
md "%HOME%\site\repository\target\oidcpoc\WEB-INF\classes\com\microsoft\aad\oidcpoc"
md "%HOME%\site\repository\cache"

REM Copy main source to target
xcopy "%HOME%\site\repository\src\main\webapp\*.*" "%HOME%\site\repository\target\oidcpoc\" /s/e/v/d/c/y

REM Use Ivy to download our dependencies based on the Maven pom.xml from the Eclipse project
"%JAVA_HOME%\bin\java.exe" -jar ivy-2.3.0.jar -ivy "%HOME%\site\repository\pom.xml" -m2compatible -cache "%HOME%\site\repository\cache"

REM Grab the jar files from the cache and put in the target lib folder
For /R "%HOME%\site\repository\cache" %%G in (*.jar) do copy "%%G" "%HOME%\site\repository\target\oidcpoc\WEB-INF\lib"

REM Build our classes with javac
SET tSourcePath=%HOME%/site/repository/src/main/java/com/microsoft/aad/oidcpoc
SET tTargetPath=%HOME%/site/repository/target/oidcpoc/WEB-INF/classes/com/microsoft/aad/oidcpoc
SET tLibPath="%HOME%\site\repository\target\oidcpoc\WEB-INF\lib\*"
"%JAVA_HOME%\bin\javac.exe" -cp %tLibPath%;"%HOME%\site\repository\target\oidcpoc\WEB-INF\classes\*" -sourcepath %tSourcePath% -d %HOME%\site\repository\target\oidcpoc\WEB-INF\classes\ %HOME%\site\repository\src\main\java\com\microsoft\aad\oidcpoc\*.java

REM Create our war file - Azure Web Apps will automatically use that to update the deployment
cd %HOME%\site\repository\target\oidcpoc
del /f/q "%HOME%\site\wwwroot\webapps\ROOT.war"
"%JAVA_HOME%\bin\jar.exe" -cvf "%HOME%\site\wwwroot\webapps\ROOT.war" *

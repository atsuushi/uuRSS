
@rem set JDBCMODE if you use JDBC mode.
rem set JDBCMODE=true

set CLASSPATH=bin;uurss.jar
set CLASSPATH=%CLASSPATH%;lib\rome-1.0.jar;lib\log4j-1.2.12.jar;lib\velocity-dep-1.5.jar;lib\jdom-1.0.jar
set CLASSPATH=%CLASSPATH%;lib\sqlite-jdbc-3.6.13.jar
set OP=-Dresult.dir=result

if "%JDBCMODE%"=="true" goto jdbcmode
:csvmode
set OP=%OP% -Dcsv=db/feedlist.csv
goto endif
:jdbcmode
set OP=%OP% -Djdbc.drivers=org.sqlite.JDBC
set OP=%OP% -Dconnection.url=jdbc:sqlite:db/feedlist.sqlite -Dconnection.user= -Dconnection.password=
:endif

java %OP% uurss.Main %*

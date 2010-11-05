#!/bin/sh

## set JDBCMODE if you use JDBC mode.
# export JDBCMODE=true

CP=uuRSS.jar:lib/rome-1.0.jar:lib/log4j-1.2.15.jar:lib/velocity-dep-1.5.jar:lib/jdom-1.0.jar:${CLASSPATH}
OP=-Dresult.dir=result

if [ "${JDBCMODE}" = "true" ]; then
	CP=${CP}:lib/sqlite-jdbc-3.6.13.jar
	OP=${OP} -Djdbc.drivers=org.sqlite.JDBC \
             -Dconnection.url=jdbc:sqlite:db/feedlist.sqlite \
             -Dconnection.user= -Dconnection.password=
else
	OP="${OP} -Dcsv=db/feedlist.csv"
fi

java -cp "${CP}" ${OP} uurss.Main $*

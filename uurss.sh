#!/bin/sh

## set JDBCMODE if you use JDBC mode.
# export JDBCMODE=true

CP=uuRSS.jar:lib/rome-1.0.0.jar
for f in lib/*.jar
do
  CP=${CP}:${f}
done
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

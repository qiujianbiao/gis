# JVMFLAGS
JVMFLAGS="\
-server \
-Xms256m \
-Xmx512m \
-XX:-UseGCOverheadLimit"
echo "JVMFLAGS: " ${JVMFLAGS}


# BASE_DIR
BASE_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )
echo "BASE_DIR: " ${BASE_DIR}

# CLASSPATH
for d in "$BASE_DIR"/lib/*.jar
do
 CLASSPATH="$d:$CLASSPATH"
done

# MAIN_JAR
MAIN_CLASS=com.ericsson.fms.Application
echo "MAIN_CLASS: " ${MAIN_CLASS}

nohup java -Dspring.profiles.active=dev -cp  "$CLASSPATH" ${JVMFLAGS}  ${MAIN_CLASS}  --spring.config.location=../conf/ --logging.config=../conf/logback-spring.xml  2>&1 < /dev/null &

:set fileformat=unix
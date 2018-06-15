#!/bin/sh
# ===================================================================
# $Id: start.sh 344894 2017-03-28 10:12:12Z eliohoi $
#
# Copyright (c) Ericsson AB Corporation 2017. All rights reserved.
# ===================================================================


# Execution directory
_startdir=`pwd`
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_PATH="$( cd $SCRIPT_DIR/.. && pwd )"
LOG_PATH="$( cd $SCRIPT_DIR/../log && pwd )"
LIB_PATH="$( cd $SCRIPT_DIR/../lib && pwd )"

# Command line option to specify 'silent mode'.
OPT_SILENT=''

# Pid file for the gis-service process
PID_FILE=${SCRIPT_DIR}/pid

#
# Simple check to see if a process associated with a pid file is running.
#
# Input parameters
#   $1 - PID file
#
# Returns value 1 if a pid file exits and a running process is associated with the pid.
#
check_pid() {
    if [ -f ${1} ]; then
        pid=`cat ${1}`
        if [ x"${pid}" != x"" -a -d /proc/${pid} ]; then
            # Process running
            return 1
        fi
    fi

    # No pid file found or pid file not removed when process terminated.
    return 0
}

# Check if we have a pid file and a process associated to the pid.
if [ x"${OPT_SILENT}" = x"" ]; then
    check_pid ${PID_FILE}
    rc=$?
    if [ $rc -eq 0 ]; then
        echo "gis-service has already stopped."
        exit 0		
    fi
fi

# stop gis-service
kill $pid

if [ $? -eq 0 ]; then
    echo "stop gis-service successful!"
else
    echo "stop gis-service fail!"
fi

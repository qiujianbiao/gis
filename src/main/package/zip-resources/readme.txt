
1 create a new account user, eg: gis
  login into this new account, eg: $sudo su  $su gis

2 copy .zip file to the directory: /home/gis/software/gis-service/
  the directory trip-mgmt should belong to this new account user.

3 unzip .zip file, then you can see the following directory structure.
  /bin
  /conf
  /data
  /log
  /lib

4 start command: sh bin/start.sh

5 stop command:  sh bin/stop.sh

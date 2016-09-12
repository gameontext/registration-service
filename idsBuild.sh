#!/bin/bash

#
# This script is only intended to run in the IBM DevOps Services Pipeline Environment.
#

#!/bin/bash
echo Informing slack...
curl -X 'POST' --silent --data-binary '{"text":"A new build for the registration service has started."}' $WEBHOOK > /dev/null

echo Setting up Docker...
mkdir dockercfg ; cd dockercfg
echo -e $KEY > key.pem
echo -e $CA_CERT > ca.pem
echo -e $CERT > cert.pem
echo Key `echo $KEY | md5sum`
echo Ca Cert `echo $CA_CERT | md5sum`
echo Cert `echo $CERT | md5sum`
cd ..

echo Obtaining docker.
wget http://security.ubuntu.com/ubuntu/pool/main/a/apparmor/libapparmor1_2.8.95~2430-0ubuntu5.3_amd64.deb -O libapparmor.deb
sudo dpkg -i libapparmor.deb
rm libapparmor.deb
wget https://get.docker.com/builds/Linux/x86_64/docker-1.9.1 --quiet -O docker
chmod +x docker

echo Building projects using gradle...
./gradlew build
if [ $? != 0 ]
then
  echo Gradle Build failed.
  curl -X 'POST' --silent --data-binary '{"text":"Build for the registration service has failed."}' $SLACK_WEBHOOK_PATH > /dev/null
  exit -1
else
  cd regsvc-wlpcfg
  ../docker build -t gameon-regsvc -f Dockerfile.live .
  if [ $? != 0 ]
  then
    echo Docker Build failed.
    curl -X 'POST' --silent --data-binary '{"text":"Docker build for the registration service has failed."}' $SLACK_WEBHOOK_PATH > /dev/null
    exit -2
  else
    echo Attempting to remove old containers.
    ../docker stop -t 0 gameon-regsvc || true
    ../docker rm gameon-regsvc || true
    echo Starting new container.
    ../docker run -d -p 9009:9080 -p 9043:9443 --restart=always --link etcd -e LICENSE=accept -e ETCDCTL_ENDPOINT=http://etcd:4001 --name=gameon-regsvc gameon-regsvc
    if [ $? != 0 ]
    then
      echo Docker Run failed.
      curl -X 'POST' --silent --data-binary '{"text":"Docker run for the registration service has failed."}' $SLACK_WEBHOOK_PATH > /dev/null
      exit -3
    else
      cd ..
      rm -rf dockercfg
    fi
  fi
fi

FROM websphere-liberty:latest
MAINTAINER Erin Schnabel @ebullientworks

ENV LIBERTY_MODE run

ADD https://download.elastic.co/logstash-forwarder/binaries/logstash-forwarder_linux_amd64 /opt/forwarder
ADD ./servers/regsvc /opt/ibm/wlp/usr/servers/defaultServer/

RUN apt-get update; apt-get install -y curl

COPY ./forwarder.conf /opt/forwarder.conf
COPY ./startup.sh /opt/startup.sh

EXPOSE 9080

RUN /opt/ibm/wlp/bin/installUtility install  --acceptLicense /opt/ibm/wlp/usr/servers/defaultServer/server.xml

CMD ["/opt/startup.sh"]

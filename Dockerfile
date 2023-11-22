#
# Dockerfile for my Zookeeper application
#
# docker build -t ece465_zkapp:v1 .

ARG BASE_IMAGE=3.8.3
FROM zookeeper:$BASE_IMAGE

RUN /bin/rm -rf /opt

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys A122542AB04F24E3

RUN apt-get update && apt-get update -y

RUN apt-get install gpg

RUN wget -O - https://apt.corretto.aws/corretto.key | gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg && echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list

#RUN echo "deb https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list

RUN apt-get update; apt-get install -y java-21-amazon-corretto-jdk


# Copy the application into the container
##COPY target/zkApp-1.0-SNAPSHOT-jar-with-dependencies.jar /zkApp.jar
COPY target/zkApp-1.0-SNAPSHOT.jar /zkApp.jar

# Copy the entrypoint script into the container
COPY zkApp-entrypoint.sh /zkApp-entrypoint.sh
RUN chmod +x /zkApp-entrypoint.sh

# Set the entrypoint script to be executable
#RUN chmod +x /zkApp-entrypoint.sh

# port for ZK admin
EXPOSE 8080
# port for app 8080
EXPOSE 8081
# port for ZK protocol
EXPOSE 2181
# port for spring boot mgmt
EXPOSE 9090

# Set the entrypoint script to be the entrypoint for the container
ENTRYPOINT ["/zkApp-entrypoint.sh"]

# Set the default command to be the Zookeeper application
#CMD ["java", "-jar", "/zkApp.jar"]
CMD ["/bin/bash","-c","/zkApp-entrypoint.sh"]


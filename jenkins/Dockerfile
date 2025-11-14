FROM jenkins/jenkins:lts-jdk17

USER root

# Install docker CLI and docker-compose (client only). The Docker daemon is provided by the host via mounted socket.
RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates curl gnupg lsb-release docker.io \
 && curl -sSL "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
 && chmod +x /usr/local/bin/docker-compose \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# Switch back to the jenkins user
USER jenkins

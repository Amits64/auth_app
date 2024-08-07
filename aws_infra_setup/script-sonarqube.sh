#!/bin/bash
# Update and install necessary packages
sudo apt-get update -y
sudo apt-get install -y openjdk-11-jdk

# Download and install SonarQube
wget https://binaries.sonarsource.com/Distribution/sonarqube/sonarqube-8.9.0.43852.zip
sudo apt-get install -y unzip
sudo unzip sonarqube-8.9.0.43852.zip -d /opt
sudo mv /opt/sonarqube-8.9.0.43852 /opt/sonarqube

# Create SonarQube user
sudo useradd -m -d /opt/sonarqube sonarqube
sudo chown -R sonarqube:sonarqube /opt/sonarqube

# Start SonarQube
sudo su - sonarqube -c "/opt/sonarqube/bin/linux-x86-64/sonar.sh start"

#!/bin/bash

# Update system packages and install Java and wget
sudo apt-get update
sudo apt install openjdk-8-jre-headless wget -y

# Create necessary directories
sudo mkdir -p /opt/nexus/
cd /opt/nexus/

# Download Nexus
NEXUSURL="https://download.sonatype.com/nexus/3/latest-unix.tar.gz"
wget $NEXUSURL -O nexus.tar.gz

# Extract Nexus
tar xzvf nexus.tar.gz
NEXUSDIR=$(ls -d nexus-*)

# Clean up
rm -rf nexus.tar.gz

# Create nexus user and set permissions
sudo useradd -r -d /opt/nexus -s /bin/false nexus
sudo chown -R nexus:nexus /opt/nexus
sudo chmod -R 755 /opt/nexus

# Configure Nexus to run as nexus user
echo 'run_as_user="nexus"' | sudo tee /opt/nexus/$NEXUSDIR/bin/nexus.rc

# Create systemd service file
sudo bash -c 'cat <<EOT > /etc/systemd/system/nexus.service
[Unit]
Description=Nexus Repository Manager
After=network.target

[Service]
Type=forking
LimitNOFILE=65536[^1^][1]
ExecStart=/opt/nexus/'$NEXUSDIR'/bin/nexus start
ExecStop=/opt/nexus/'$NEXUSDIR'/bin/nexus stop
User=nexus
Restart=on-abort

[Install]
WantedBy=multi-user.target
EOT'

# Reload systemd and start Nexus
sudo systemctl daemon-reload
sudo systemctl start nexus
sudo systemctl enable nexus

# Open firewall port if UFW is running
sudo ufw allow 8081/tcp[^2^][2]

echo "Nexus Repository Manager installation and setup is complete."

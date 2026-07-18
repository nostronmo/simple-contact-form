#!/bin/bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

sudo mkdir -p /etc/apt/keyrings/
wget -q -O - https://apt.grafana.com/gpg.key | gpg --dearmor | sudo tee /etc/apt/keyrings/grafana.gpg > /dev/null
echo "deb [signed-by=/etc/apt/keyrings/grafana.gpg] https://apt.grafana.com stable main" | sudo tee /etc/apt/sources.list.d/grafana.list

sudo apt-get update -y
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y nginx wget iptables-persistent alloy certbot python3-certbot-nginx

echo "Baixando e instalando IBM Semeru OpenJ9 JRE 21..."
wget -q https://github.com/ibmruntimes/semeru21-binaries/releases/download/jdk-21.0.3%2B9_openj9-0.44.0/ibm-semeru-open-jre_x64_linux_21.0.3_9_openj9-0.44.0.tar.gz -O /tmp/semeru.tar.gz
sudo tar -xzf /tmp/semeru.tar.gz -C /opt
sudo mv /opt/jdk-21.0.3+9-jre /opt/semeru21

sudo ln -sf /opt/semeru21/bin/java /usr/bin/java

sudo iptables -t nat -F
sudo sh -c "iptables-save > /etc/iptables/rules.v4"

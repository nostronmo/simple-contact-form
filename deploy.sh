#!/bin/bash
set -e

echo "Step 0: Extracting SSH private key from Terraform..."
if [ "$CIRCLECI" != "true" ]; then
    rm -f key.pem
    sleep 2
    terraform output -raw private_key > key.pem
    chmod 400 key.pem
    EC2_IP=$(terraform output -raw ec2_public_ip)
else
    chmod 400 key.pem
    EC2_IP=$(cat ec2_ip.txt)
fi


echo "Step 1: Compiling Angular production assets..."
cd form-frontend
npm install --legacy-peer-deps
ng build --configuration production --output-path=dist/contact-form
cd ..

echo "Step 2: Packaging executable Spring Boot JAR..."
cd form-backend
mvn clean package -DskipTests
cd ..

echo "Step 3: Getting Environment Variables to Production..."
grep -v "^AWS_" .env > app.env

echo "Step 4: Sending Payload to AWS ($EC2_IP)..."
ssh -o StrictHostKeyChecking=no -i key.pem ubuntu@$EC2_IP "mkdir -p /home/ubuntu/frontend /home/ubuntu/templates"
scp -o StrictHostKeyChecking=no -i key.pem app.env ubuntu@$EC2_IP:/home/ubuntu/app.env
scp -o StrictHostKeyChecking=no -i key.pem nginx.conf ubuntu@$EC2_IP:/home/ubuntu/nginx.conf
scp -o StrictHostKeyChecking=no -i key.pem config.alloy ubuntu@$EC2_IP:/home/ubuntu/config.alloy
scp -o StrictHostKeyChecking=no -i key.pem form-backend/target/*.jar ubuntu@$EC2_IP:/home/ubuntu/app.jar
scp -o StrictHostKeyChecking=no -i key.pem -r form-backend/templates/* ubuntu@$EC2_IP:/home/ubuntu/templates/
scp -o StrictHostKeyChecking=no -i key.pem -r form-frontend/dist/contact-form/* ubuntu@$EC2_IP:/home/ubuntu/frontend/

echo "Step 5: Verifying Environment and Starting Services..."
ssh -o StrictHostKeyChecking=no -i key.pem ubuntu@$EC2_IP << 'EOF'

  echo "Waiting for EC2 system initialization cloud-init to complete..."
  until which java &>/dev/null && [ -d /etc/nginx ] && id -u alloy &>/dev/null; do
    echo "Dependencies (Java/Nginx/Alloy) are still installing via script.sh... sleeping 10s"
    sleep 10
  done

  set -a
  source /home/ubuntu/app.env
  set +a

  echo "System preparation complete. Moving forward with security isolation setup!"

  echo "Deploying Angular files to web root..."
  sudo mkdir -p /var/www/contact-form
  sudo rm -rf /var/www/contact-form/*
  sudo mv /home/ubuntu/frontend/* /var/www/contact-form/
  sudo chown -R www-data:www-data /var/www/contact-form
  sudo find /var/www/contact-form -type d -exec chmod 755 {} \;
  sudo find /var/www/contact-form -type f -exec chmod 644 {} \;
  rm -rf /home/ubuntu/frontend

  echo "Securing Environment files and application paths..."
  sudo groupadd -r apps || true
  sudo useradd -r -s /bin/false -g apps springapp || true
  sudo mkdir -p /etc/springapp
  sudo mkdir -p /etc/springapp/logs

  sudo mv /home/ubuntu/app.env /etc/springapp/backend.env
  sudo chown springapp:apps /etc/springapp/backend.env
  sudo chmod 600 /etc/springapp/backend.env

  sudo mv /home/ubuntu/app.jar /etc/springapp/app.jar
  sudo mv /home/ubuntu/templates /etc/springapp/templates
  rm -rf /home/ubuntu/templates

  sudo chown -R springapp:apps /etc/springapp/
  sudo chmod 755 /etc/springapp/logs

  echo "Applying Grafana Alloy Configuration..."
  sudo usermod -aG apps alloy || true
  sudo usermod -aG adm alloy || true

  sudo mv /home/ubuntu/config.alloy /etc/alloy/config.alloy
  sudo chown alloy:alloy /etc/alloy/config.alloy
  sudo chmod 644 /etc/alloy/config.alloy

  sudo cp /etc/springapp/backend.env /etc/alloy/alloy.env
  sudo chown alloy:alloy /etc/alloy/alloy.env
  sudo chmod 600 /etc/alloy/alloy.env

  sudo mkdir -p /etc/systemd/system/alloy.service.d
  echo -e "[Service]\nEnvironmentFile=/etc/alloy/alloy.env" | sudo tee /etc/systemd/system/alloy.service.d/override.conf

  echo "Applying Nginx Configuration..."
  if [ -f /etc/nginx/sites-available/default ] && grep -q "managed by Certbot" /etc/nginx/sites-available/default; then
      echo "Manual SSL configuration detected! Preserving your Nginx settings."
      rm -f /home/ubuntu/nginx.conf
      sudo systemctl restart nginx
  else
    echo "Deploying base HTTP configuration..."
    if [ -n "$DOMAIN_SSL" ]; then
    envsubst '${DOMAIN_SSL}' < /home/ubuntu/nginx.conf | sudo tee /etc/nginx/sites-available/default > /dev/null
    else
    sudo mv /home/ubuntu/nginx.conf /etc/nginx/sites-available/default
    fi

    rm -f /home/ubuntu/nginx.conf
    sudo systemctl restart nginx
    echo "Base Nginx configured. Ready for manual Certbot execution."
  fi

  echo "Writing systemd service file..."
  sudo bash -c 'cat << SERVICE_EOF > /etc/systemd/system/contact-form.service
[Unit]
Description=contact-form Spring Boot API
After=network.target

[Service]
User=springapp
Group=apps
WorkingDirectory=/etc/springapp
EnvironmentFile=/etc/springapp/backend.env

ExecStart=/usr/bin/java -Xshareclasses -Xquickstart -Xmx256m -jar /etc/springapp/app.jar

SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
SERVICE_EOF'

  echo "Reloading systemd manager configuration..."
  sudo systemctl daemon-reload

  echo "Enabling and restarting contact-form service..."
  sudo systemctl enable contact-form.service
  sudo systemctl restart contact-form.service

  if [ -z "$GRAFANA_PROMETHEUS_URL" ]; then
    echo "No Grafana Variables Found. Disabling telemetry agent."
    sudo systemctl stop alloy || true
    sudo systemctl disable alloy || true
  else
    echo "Grafana Variables Found. Starting telemetry agent..."
    sudo systemctl enable alloy
    sudo systemctl restart alloy
    sudo systemctl status alloy --no-pager
  fi

  echo "Checking backend service status..."
  sudo systemctl status contact-form.service --no-pager

EOF

echo "Pipeline complete! Your application is running at: http://$EC2_IP"

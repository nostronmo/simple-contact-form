sudo certbot --nginx --non-interactive --agree-tos -m "$MAIL_SSL" -d "$DOMAIN_SSL" --redirect

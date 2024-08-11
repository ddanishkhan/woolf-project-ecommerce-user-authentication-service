# woolf-project-ecommerce-user-authentication-service
Handles user registration, login, and authorization

## OAuth2 Token generation curl.
curl --location 'localhost:8005/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Cookie: JSESSIONID=CA55370223EDBFA6508668B25159DBED' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_id=product_service' \
--data-urlencode 'client_secret=secret'

`docker run -d -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=user-authentication --name ecommerce-user-authentication -p 3308:3306 mysql:9.0`
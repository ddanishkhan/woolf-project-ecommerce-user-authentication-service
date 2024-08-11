-- Picked from Spring Repo itself.
-- https://github.com/spring-projects/spring-authorization-server/tree/main/oauth2-authorization-server/src/main/resources/org/springframework/security/oauth2/server/authorization
CREATE TABLE oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

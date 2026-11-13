# Deploy the hubs backend WAR to Tomcat

1. Build:
   `mvn -f lpu-hubs-portal clean package`
2. Copy into the Tomcat webapps mount:
   `cp lpu-hubs-portal/target/hubs-portal.war data/tomcat/webapps/`
3. Start the stack:
   `docker compose up -d`

Context path: `/hubs-portal`

Public catalog:
`GET http://localhost:8080/hubs-portal/api/public/portals/ADMIN`
`GET http://localhost:8080/hubs-portal/api/public/portals/LEARNER`

Login:
`POST http://localhost:8080/hubs-portal/api/auth/login`

Hostnames (nginx on port 80, then the Cloudflare tunnel):

- `https://learnershub.lpulaguna.com` → Learner's Hub
- `https://adminportal.lpulaguna.com` → Admin Portal

On localhost, `/` is the Learner's Hub and `/admin` is the Admin Portal.

The first admin password is written to `data/secrets/initial-admin-password` and must be changed at first sign-in. Link editing is at `/manage`.

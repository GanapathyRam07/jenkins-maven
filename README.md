# Task API — Java + Jenkins CI/CD starter

A practical starter repository: Spring Boot REST API, PostgreSQL, Flyway migrations, tests, Docker, Jenkins, image scanning, registry publishing, and gated deployment to a Linux VM.

This is a production-oriented learning baseline, not a complete production certification. Before real production use, add your organization's authentication/authorization, TLS, secret management, backups/restore tests, monitoring/alerting, capacity planning, and operational review.

## Stack
- Java 21, Spring Boot 3.5.x, Maven
- REST endpoints under `/api/v1/tasks`
- PostgreSQL 16 + Flyway migration
- Actuator health endpoint `/actuator/health`
- Docker multi-stage build; runtime uses a non-root user
- Jenkins pipeline with Maven tests, JUnit reporting, Trivy scan, registry push, and approval-gated deployment

## Run locally
Prerequisites: JDK 21, Maven 3.9+, PostgreSQL. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`, then run:
```bash
mvn clean verify
mvn spring-boot:run
```
Do not use the sample default password outside a disposable local environment.

## API examples
Create:
```bash
curl -i -X POST http://localhost:8080/api/v1/tasks -H 'Content-Type: application/json' -d '{"title":"Learn Jenkins","description":"Build a Java pipeline","status":"TODO"}'
```
List:
```bash
curl http://localhost:8080/api/v1/tasks
```
Update:
```bash
curl -i -X PUT http://localhost:8080/api/v1/tasks/1 -H 'Content-Type: application/json' -d '{"title":"Learn Jenkins deeply","description":"CI/CD practice","status":"IN_PROGRESS"}'
```
Delete:
```bash
curl -i -X DELETE http://localhost:8080/api/v1/tasks/1
```

## Jenkins compatibility note
The Jenkinsfile targets a **Linux agent** labelled `linux-docker`. Your current Jenkins is installed on Windows; the earlier `sh` error happened because Windows does not provide Unix `sh` by default. Configure a Linux agent with JDK 21, Maven, Git, Docker CLI/Engine access, Trivy, and network access. Do not simply replace `sh` with `bat`: the pipeline uses Linux shell, SSH, and Docker deployment commands.

## Jenkins plugins
Install/verify: Pipeline, Git, Credentials Binding, SSH Agent, and JUnit.

## Jenkins credentials
Create in **Manage Jenkins → Credentials**:
1. `docker-registry-creds`: username/password or registry token.
2. `production-server-host`: Secret text with the Linux VM DNS name/IP.
3. `production-server-ssh`: SSH Username with private key; username is the deployment account.

Replace `YOUR_DOCKERHUB_USERNAME/task-api` in `Jenkinsfile`. Adjust registry hostname/repository for another registry.

All branches run tests, build, scan, and push. The `main` branch requires manual approval before production deployment. Restrict branch protection and approver permissions.

## Prepare the Linux deployment VM
Install Docker Engine and Docker Compose plugin using official Docker documentation. Create `/opt/task-api`, copy `compose.prod.yaml` and `deploy/deploy.sh` there, and make the script executable:
```bash
sudo install -d -m 750 -o "$USER" -g "$USER" /opt/task-api
chmod 750 /opt/task-api/deploy.sh
```
Create `/opt/task-api/.env` on the server (never commit it):
```dotenv
POSTGRES_PASSWORD=REPLACE_WITH_A_LONG_RANDOM_SECRET
```
Then protect it with `chmod 600 /opt/task-api/.env`. The deployment SSH account needs permission to update the compose file and operate this application. The Jenkinsfile copies `compose.prod.yaml`; `deploy.sh` must be copied during initial provisioning.

The compose file binds the app to `127.0.0.1:8080` and does not publish the database port. Configure a TLS reverse proxy or internal load balancer for users. Back up PostgreSQL and test restore procedures.

## Production gaps to close
- Add authentication and authorization; this sample API intentionally does not expose a public production service.
- Add TLS, secret manager integration, secret rotation, logs/metrics/alerts, resource limits, and backup/restore drills.
- Consider managed PostgreSQL, SBOM generation, dependency/SAST scanning, image signing, and provenance.
- Pin base images by digest in high-assurance environments.
- Review migration compatibility and test rollback in staging.
- Docker socket access is effectively root-level; isolate build agents and untrusted jobs.
- Provision SSH host keys securely. Do not disable host-key verification in production.

## Troubleshooting
- `sh: not found`: use a Linux agent for this Jenkinsfile.
- Docker permission denied: check Docker installation and agent permissions.
- Trivy not found: install Trivy on the agent.
- Registry push denied: verify repository and token scope.
- App unhealthy: inspect `docker compose logs app db` and check DB configuration/migrations.

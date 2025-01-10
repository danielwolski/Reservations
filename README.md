# Restaurant Table Reservations

# Stack
* **Language:** Java
* **Framework:** Spring Boot
* **Database:** PostgreSQL, JPA
* **Build tool:** Gradle

# Local installation and setup
1. **Clone the repository:**
```bash
git clone https://github.com/danielwolski/Reservations-WIP.git
```
2. **Start PostgreSQL in Docker:**

To do it, PostgreSQL docker image is needed
```
docker pull postgres
```
```bash
 cd ./postgresql
 docker-compose -f docker-compose-postgresql.yml up -d
```
You may need to change this part of /postgresql/docker-compose-postgresql.yml file:
```
image: bitnami/postgresql
```
to your PostgreSQL docker image name.

3. **Build and run the project:**
```bash
 cd ..
./gradlew build
./gradlew bootRun
```

# Additional resources:
**Gradle installation**
```
https://gradle.org/install/
```

**Getting started with Docker**
```
https://www.docker.com/get-started/
```

**DockerHub PostgreSQL**
```
https://hub.docker.com/_/postgres
```
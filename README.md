# Sistema Aeropuerto Los Primos

Sistema web para la gestión de operaciones del **Aeropuerto Los Primos**.
Permite administrar vuelos, pasajeros, aerolíneas, tripulación, reservas, abordaje y reportes.

El sistema está dividido en dos partes:

* **Backend:** API REST desarrollada con Spring Boot.
* **Frontend:** Aplicación web desarrollada con Angular.

## Tecnologías utilizadas

### Backend

* Java 17
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA / Hibernate
* PostgreSQL
* Maven
* OpenPDF
* Apache POI

### Frontend

* Angular
* TypeScript
* HTML
* CSS
* Bootstrap

## Módulos principales

* Registro e inicio de sesión de usuarios.
* Gestión de pasajeros.
* Gestión de aerolíneas.
* Gestión de tripulación.
* Creación y administración de vuelos.
* Reserva de vuelos.
* Abordaje de pasajeros.
* Consulta pública de vuelos.
* Reportes en PDF y Excel.

## Estructura del proyecto

```txt
aeropuerto-backend/
│
├── aeropuerto-backend/      # Backend Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── aeropuertofront/         # Frontend Angular
│   ├── src/
│   ├── package.json
│   └── ...
│
├── .gitignore
└── README.md
```

## Requisitos previos

Antes de ejecutar el proyecto, se debe tener instalado:

* Java 17
* Maven
* Node.js
* npm
* PostgreSQL
* Angular CLI

Para verificar las versiones instaladas:

```bash
java -version
mvn -version
node -v
npm -v
ng version
```

## Configuración de la base de datos

Crear una base de datos en PostgreSQL con el siguiente nombre:

```sql
aeropuertolosprimos
```

Ejemplo:

```sql
CREATE DATABASE aeropuertolosprimos;
```

## Configuración del backend

Dentro de la carpeta del backend existe un archivo de ejemplo:

```txt
application-example.properties
```

Este archivo sirve como plantilla de configuración.

Para ejecutar el proyecto localmente, se debe crear un archivo llamado:

```txt
application.properties
```

en la ruta:

```txt
aeropuerto-backend/src/main/resources/application.properties
```

Puede copiarse el contenido de `application-example.properties` y modificar los datos según la configuración local de PostgreSQL.

Ejemplo:

```properties
spring.application.name=aeropuerto-backend

spring.datasource.url=jdbc:postgresql://localhost:5432/aeropuertolosprimos
spring.datasource.username=usuario_bd
spring.datasource.password=contrasena_bd

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database=POSTGRESQL
spring.sql.init.platform=postgres

app.jwt.secret=colocar_clave_segura_minimo_32_caracteres
```

## Ejecución del backend

Desde la carpeta del backend:

```bash
cd aeropuerto-backend
mvn clean package -DskipTests
```

Si la compilación finaliza correctamente, se mostrará:

```txt
BUILD SUCCESS
```

Para ejecutar el backend:

```bash
java -jar target/aeropuerto-backend-0.0.1-SNAPSHOT.jar
```

También puede ejecutarse con Maven:

```bash
mvn spring-boot:run
```

Por defecto, el backend se ejecuta en:

```txt
http://localhost:8080
```

## Configuración y ejecución del frontend

Desde la carpeta del frontend:

```bash
cd aeropuertofront
npm ci
```

Luego, para ejecutar el frontend en modo desarrollo:

```bash
ng serve
```

Por defecto, Angular se ejecuta en:

```txt
http://localhost:4200
```

## Compilación del frontend

Para generar la versión compilada del frontend:

```bash
npm run build
```

Si la compilación finaliza correctamente, Angular generará los archivos en:

```txt
dist/aeropuertofront
```
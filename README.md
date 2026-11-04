# Gestión de Enfermedades - Corte 3

## Desarrollo de Aplicaciones Móviles

**Estudiante:** Fabrizio Antonio D'Amato Osorio  
**Correo institucional:** fdamatoo@unicartagena.edu.co  
**Ejercicio:** 9 - Gestión de Enfermedades  
**Corte:** 3

## Descripción

Aplicación móvil desarrollada en Android Studio con Java para la gestión de usuarios y enfermedades.

La aplicación consume una API REST desplegada en la nube y utiliza una base de datos PostgreSQL en línea.

## Tecnologías

- Android Studio
- Java
- XML
- API REST
- Spring Boot
- PostgreSQL
- Render
- JSON
- JWT
- Brevo

## API

Backend desplegado en:

https://gestion-enfermedades-api.onrender.com

## Funcionalidades

### Autenticación

- Inicio de sesión mediante usuario y contraseña.
- Autenticación mediante JWT.
- Persistencia local de la sesión.
- Cierre de sesión.
- Registro de usuarios.
- Recuperación de contraseña por correo electrónico.

### Usuarios

CRUD completo:

- Crear usuario.
- Consultar usuario.
- Actualizar usuario.
- Eliminar usuario.

Campos:

- username
- password
- nombre
- email

### Enfermedades

CRUD completo:

- Crear enfermedad.
- Consultar enfermedad.
- Actualizar enfermedad.
- Eliminar enfermedad.

Campos:

- id
- nombre
- nombreCientifico
- nivelGravedad
- sintomas
- medicamentos
- esContagiosa
- esCubiertaPorPost
- requiereIncapacidad

### Reportes parametrizados

Usuarios:

- Reporte por nombre.
- Reporte por correo electrónico.

Enfermedades:

- Reporte por nivel de gravedad.
- Reporte por condición contagiosa.

### Recuperación de contraseña

La aplicación permite solicitar un recordatorio de contraseña mediante el correo electrónico registrado.

El envío del mensaje se realiza mediante el servicio de correo integrado en el backend.

### Sensores

La aplicación implementa dos sensores:

1. **Ubicación:** muestra latitud y longitud mediante los servicios de localización de Android.
2. **Acelerómetro:** detecta movimientos del dispositivo y muestra el mensaje `Movimiento detectado`.

## Instalación

1. Clonar o descargar este repositorio.
2. Abrir el proyecto en Android Studio.
3. Esperar la sincronización de Gradle.
4. Ejecutar la aplicación en un dispositivo Android o emulador.
5. Tener conexión a Internet para consumir la API REST.

## APK

El APK generado se encuentra en:

`entregables/GestionEnfermedadesCorte3.apk`

## Repositorio

https://github.com/fdamatoo-bit/GestionEnfermedades-Corte3

## Estado

Proyecto funcional correspondiente al Corte 3 del ejercicio 9 - Gestión de Enfermedades.

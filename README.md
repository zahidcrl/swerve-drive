# FRC Swerve Drive 2026 - Equipo 6896

Código base para chasis Swerve utilizando la librería
**[YAGSL](https://github.com/Yet-Another-Software-Suite/YAGSL)** (Yet Another Generic Swerve Library) 
y **WPILib** para la temporada 2026.

---

## Especificaciones del Hardware:

* **Módulos Swerve:** SDS MK4i
* **Motores de Tracción:** REV NEO Brushless
* **Motores de Giro:** REV NEO Brushless
* **Encoders Absolutos:** CTRE CANcoder
* **Giroscopio (IMU):** CTRE Pigeon 2.0

---

## Características del Software

* **Framework:** WPILib 2026 (Java 17)
* **Librería de Swerve:** [YAGSL](https://github.com/Yet-Another-Software-Suite/YAGSL)
* **Control de Trayectorias:** PathPlanner (Autónomos y Pathfinding)
* **Cinemática:** Odometría de WPILib con estimación de pose mediante visión (Limelight)

---

## Configuración e Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/zahidcrl/swerve-drive.git
   ```
2. Abrir la carpeta raíz del proyecto directamente en **WPILib VS Code 2026**.
3. **VendorDeps:** Las librerías requeridas ya se encuentran incluidas en la carpeta `vendordeps/`.
   Gradle las descargará y actualizará automáticamente al compilar por primera vez.
   Las principales dependencias son:
  * `YAGSL-Lib`
  * `REVLib` (Motores NEO)
  * `Phoenix6` / `Phoenix5` (Pigeon 2.0 y CANcoders)
  * `PathPlannerLib`
4. **Compilar y Desplegar:** Conecta la computadora a la red del robot y presiona `F5` o selecciona **Deploy Robot Code** desde la paleta de comandos (Command Palette) de WPILib.

> **Note de configuración:** Toda la configuración física del Swerve (dimensiones, relaciones de transmisión y offsets) se encuentran mapeada en archivos JSON dentro de la ruta `src/main/deploy/swerve`.

---

## Controles del Driver (Xbox Controller)

El robot utiliza un esquema de control orientado al campo (Field-Oriented) optimizado 
mediante `SwerveInputStream` de [YAGSL](https://github.com/Yet-Another-Software-Suite/YAGSL) con una zona muerta (deadband) configurada 
en las constantes `Constants.java`.

* **Joystick Izquierdo:**
  * **Eje Y (Arriba/Abajo):** Translación hacia Adelante / Atrás.
  * **Eje X (Izquierda/Derecha):** Translación hacia Izquierda / Derecha.
  * *Nota: La velocidad de tranlación está limitada por software al 80% de la capacidad máxima.*
* **Joystick Derecho:**
  * **Eje X (Izquierda/Derecha):** Rotación de chasis (Velocidad Angular).
* **Botón B:** Resetea la orientación del Giroscopio tomando en cuenta la alianza actual (`zeroGyroWithAlliance`). Útil si el frente del robot se descalibra en la partida.
* **Botón X:** Bloquea las ruedas en posición de "X" (`lock`). Sirve como freno de mano estructural para evitar que otros robots empujen el chasis.

---

## Distribución de IDs de CAN

| Dispositivo         | ID CAN | Descripción                           |
|---------------------|--------|---------------------------------------|
| Pigeon 2.0          |     21 | Giroscopio IMU                        |
| Front Left Drive    |      1 | Motor de tracción delantero izquierdo |
| Front Left Angle    |      2 | Motor de giro delantero izquierdo     |
| Front Left Encoder  |      3 | CANcoder delantero izquierdo          |
| Front Right Drive   |      5 | Motor de tracción delantero derecho   |
| Front Right Angle   |      4 | Motor de giro delantero derecho       |
| Front Right Encoder |      6 | CANcoder delantero derecho            |
| Back Left Drive     |      7 | Motor de tracción trasero izquierdo   |
| Back Left Angle     |      8 | Motor de giro trasero izquierdo       |
| Back Left Encoder   |      9 | CANcoder trasero izquierdo            |
| Back Right Drive    |     10 | Motor de tracción trasero derecho     |
| Back Right Angle    |     11 | Motor de giro trasero derecho         |
| Back Right Encoder  |     12 | CANcoder trasero derecho              |

---

## Guía de Calibración de Encoders (YAGSL Offsets)

Cuando el chasis se descalibre o los módulos no apunten hacia enfrente al iniciar, sigue estos pasos para actualizar los offsets de los **CANcoders**:

1. **Alineación Física:**
   * Enciende el robot y coócalo sobre bloques (para que las ruedas no toquen el suelo).
   * Gira manualmente los 4 módulos hasta que las ruedas de tracción queden **perfectamente paralelas y apuntando hacia el frente** del robot.
   * *Asegúrate de que los engranajes/biseles de los motores MK4i miren todos hacia el mismo lado correcto (usualmente hacia la izquierda del frente del robot).*

2. **Obtener los valores actuales:**
   * Abre el **Dashboard** (Shuffleboard o AvantageScope).
   * Busca los valores reportados de posición absoluta para cada CANcoder en grados (valores entre `-180` y `180`, o `0` y `360`). Sin mover las ruedas, anota el valor exacto de cada módulo.

3. **Modificar los archivos JSON:**
  * Dirígete a la carpeta del proyecto: `src/main/deploy/swerve/modules/`.
  * Abre el archivo `.json` correspondiente a cada módulo (ej. `frontleft.json`, `frontright.json`, etc).
  * Localiza la propiedad `"absoluteEncoderOffset"` y reemplaza su valor con el ángulo anotado en el paso anterior.
  * *Nota: Si el robot se desvía drásticamente al acelerar después del cambio, verifica si necesitas sumar o restar el valor obtenido al offset que ya existía.*
   
4. **Guardar y Desplegar:**
   * Guardar los archivos modificados.
   * Realizar un **Deploy** (`F5`) para cargar la nueva configuración al RoboRIO.

---

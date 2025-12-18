# PruebaE2E ⭐
Este proyecto fue generado con el patron de diseño de Screenplay, Serenity BDD, principios solid, principios dry, programación orientada a objetos, Cucumber y Gherkin.

1. Requisitos previos: 📃
   - Equipo local con Windows 11 (probado)
   - IDE IntelliJ IDEA Comunity Edition 2023.3 o
-   JDK: Java SE - JDK 21 (project sourceCompatibility = 21 en build.gradle). Configure el Project SDK a JDK 21 en IntelliJ.
-   Gradle 8.7: use el Gradle Wrapper incluido (no se requiere Gradle global). El repositorio incluye gradlew/gradlew.bat y gradle/wrapper.
-   Edge: Microsoft Edge instalado. Se recomienda usar la versión de EdgeDriver que coincida exactamente con la versión de Microsoft Edge instalada.
Compruebe la versión en Edge -> Settings -> About Microsoft Edge. Si necesita descargar EdgeDriver, descárguelo desde: https://developer.microsoft.com/es-es/microsoft-edge/tools/webdriver?form=MA13LH.
Añada el binario a PATH o pase la propiedad JVM -Dwebdriver.edge.driver="C:\\drivers\\msedgedriver.exe" al ejecutar.

2. Configuración del proyecto en IntelliJ: 🧰
   - Abra IntelliJ -> Open o Import Project -> seleccione la raíz del proyecto (la carpeta que contiene build.gradle).
   - Cuando se le solicite, importe como proyecto Gradle y elija "Use Gradle wrapper".
   - Configure el Project SDK a JDK 21 (File -> Project Structure -> Project SDK).
   - Habilite la importación automática (Gradle) para que las dependencias se descarguen automáticamente.

3. Instrucciones para ejecutar las pruebas: ✔️
   - Opción A — Desde la interfaz de IntelliJ:
     - Abra la ventana de Gradle (lado derecho), expanda Tasks -> verification -> test y ejecute la tarea 'test'.
     - O abra la clase runner de pruebas (RunnerTest.java) y haga clic en Run sobre la clase.

   - Opción B — Desde terminal (recomendado y reproducible):
     - Abra una terminal en la raíz del proyecto y ejecute (Windows PowerShell o CMD):
       - .\gradlew.bat clean test
     - El Gradle wrapper descargará las dependencias y ejecutará las pruebas.

   **** EJECUTAR PRUEBAS ****

   - Abra el archivo RunnerTest.java
     ![imagen](https://github.com/user-attachments/assets/47f06028-60b3-4743-9e8c-4db74ab7f170)

   - Haga clic con el botón derecho sobre el cuerpo de la clase y seleccione "Run 'RunnerTest'" en IntelliJ
     ![imagen](https://github.com/user-attachments/assets/d04765ff-9d88-4195-819d-82a07b4d044c)

   - O ejecute en terminal: .\gradlew.bat clean test
     ![imagen](https://github.com/user-attachments/assets/32970be2-faee-4030-bcbe-eb406c36f9eb)

   **** VER REPORTES ****
   - Reportes alternativos JUnit/Gradle:
     - build/reports/tests/test/index.html
     - test-results/test/ (archivos XML)

   - Si usa la vista de proyecto de IntelliJ: abra la carpeta 'target' o 'build', busque y haga doble clic en index.hltml para abrirlo en su navegador.
     ![imagen](https://github.com/user-attachments/assets/2cad1c37-1f47-4061-a530-99b7ffe81ba7)

5. Información adicional: ➕
   - Las dependencias y plugins están declarados en build.gradle (Serenity BDD v4.1.14, Selenium 3.141.59, JUnit 5/4). El proyecto utiliza el Gradle wrapper provisto.

Buenas prácticas y consejos
- Use la misma versión de Edge/EdgeDriver en local y CI para evitar incompatibilidades.
- Si alguna espera resulta inestable, aumente el timeout localmente en el archivo correspondiente.
- Para depuración: ejecutar una prueba desde IntelliJ con breakpoints.

AUTOR 📍
@BryanFreire

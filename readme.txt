# PruebaE2E ⭐
This project was generated with Patron Automation Test - Screenplay, Serenity bdd, Cucumber and Gherkin.

1. Prerequisites: 📃
   -  Local Machine with OS Windows 10 (tested)
   -  IDE IntelliJ IDEA 2023.1.2 or newer (Community or Ultimate)
-   JDK: Java SE - JDK 21 (project sourceCompatibility = 21 in build.gradle). Configure Project SDK to JDK 21 in IntelliJ.
-   Gradle 8.7: use the included Gradle Wrapper (no global Gradle required). The repository includes gradlew/gradlew.bat and gradle/wrapper.
-   Edge: Microsoft Edge installed. Se recomienda usar la versión de EdgeDriver que coincida exactamente con la versión de Microsoft Edge instalada. Comprueba la versión en Edge -> Settings -> About Microsoft Edge. Si necesitas descargar EdgeDriver, descárgalo desde: https://developer.microsoft.com/es-es/microsoft-edge/tools/webdriver?form=MA13LH . Añade el binario a PATH o pasa la propiedad JVM -Dwebdriver.edge.driver="C:\\ruta\\msedgedriver.exe" al ejecutar.

2. Project configuration in IntelliJ: 🧰
   - Open IntelliJ -> Open or Import Project -> select the project root (the folder containing build.gradle).
   - When prompted, import as a Gradle project and choose "Use Gradle wrapper".
   - Set Project SDK to JDK 21 (File -> Project Structure -> Project SDK).
   - Enable Auto-import (Gradle) so dependencies are downloaded automatically.

3. Instructions for running the tests: ✔️
   - Option A — From IntelliJ GUI:
     - Open the Gradle tool window (right side), expand Tasks -> verification -> test and run the 'test' task.
     - Or open the test runner class (RunnerTest.java) and click Run on the class.

   - Option B — From terminal (recommended and reproducible):
     - Open a terminal in the project root and run (Windows PowerShell or CMD):
       - .\gradlew.bat clean test
     - The Gradle wrapper will download dependencies and execute the tests.

   **** EJECUTE TEST ****

   - Open file RunnerTest.java
     ![imagen](https://github.com/user-attachments/assets/47f06028-60b3-4743-9e8c-4db74ab7f170)

   - Click with right mouse button on the class body and select "Run 'RunnerTest'" in IntelliJ
     ![imagen](https://github.com/user-attachments/assets/d04765ff-9d88-4195-819d-82a07b4d044c)

   - Or run in terminal: .\gradlew.bat clean test
     ![imagen](https://github.com/user-attachments/assets/32970be2-faee-4030-bcbe-eb406c36f9eb)

   **** VIEW REPORTS ****
   - Alternative JUnit/Gradle reports:
     - build/reports/tests/test/index.html
     - test-results/test/ (XML files)

   - If using IntelliJ project view: open the 'target' or 'build' folder, then site/serenity and double-click index.html to open in your browser.
     ![imagen](https://github.com/user-attachments/assets/2cad1c37-1f47-4061-a530-99b7ffe81ba7)

5. Additional Information: ➕
   - Dependencies and plugins are declared in build.gradle (Serenity BDD v4.1.14, Selenium 3.141.59, JUnit 5/4). The project uses the Gradle wrapper provided.
   - A short README (readme.txt) with a step-by-step guide was also added to the repository.

Cambios importantes aplicados (política de esperas)
- Se unificaron las esperas explícitas en el proyecto a 2 segundos para acelerar validaciones cortas (WaitUntil.the(...).forNoMoreThan(2).seconds() y WebDriverWait con Duration.ofSeconds(2)).
- Archivos donde se aplicó la unificación (ejemplos):
  - src/test/java/pruebaE2E/interactions/WaitFor.java
  - src/test/java/pruebaE2E/interactions/WaitForElementsCount.java
  - src/test/java/pruebaE2E/interactions/AcceptAlert.java
  - src/test/java/pruebaE2E/interactions/SafeClick.java
  - src/test/java/pruebaE2E/tasks/GoCartPage.java
  - src/test/java/pruebaE2E/tasks/LoginPage.java
  - src/test/java/pruebaE2E/tasks/PurchaseFormPage.java
  - src/test/java/pruebaE2E/tasks/AddDevicesCartPage.java
  - src/test/java/pruebaE2E/tasks/CloseSesionPage.java
  - src/test/java/pruebaE2E/glue/buyingGlue.java

Buenas prácticas y consejos
- Usa la misma versión de Chrome/ChromeDriver en local y CI para evitar incompatibilidades.
- Si alguna espera resulta inestable, aumenta el timeout localmente en el archivo correspondiente.
- Para depuración: ejecutar un test desde IntelliJ con breakpoints.

AUTOR 📍
@BryanFreire

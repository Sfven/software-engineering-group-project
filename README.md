# Gamer Central

A launcher for various minigames like tic-tac-toe and Wordle.
## Presentation
https://www.canva.com/design/DAF1SUt49vc/PgEh5GtnmGD6bjalanSJ4g/edit?utm_content=DAF1SUt49vc&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

Meeting Mondays/Wednesday/Saturdays at 4pm
- Monday: Review
- Wednesday: Planning
- Saturday: Misc.

## Compilation Instructions
### Prerequesites
Multiple things must be preinstalled prior to running or compiling the program:
- Java
- JavaFX SDK 21.0.1 or newer.
- The JDBC driver (https://dev.mysql.com/downloads/connector/j/)
- Python

### Instructions
- JDBC driver must be added to your classpath.
    - See https://dev.mysql.com/doc/connector-j/en/connector-j-binary-installation.html
- From there, simply run your compilation command:
    - `javac --module-path ./javafx-sdk-21.0.1/lib/ --add-modules javafx.controls,javafx.fxml Login.java`

## Runtime Instructions
- Run the program. Note that if the JDBC driver is setup incorrectly, you will get a `classNotFoundException`.
    - `java --module-path ./javafx-sdk-21.0.1/lib/ --add-modules javafx.controls,javafx.fxml Login.java`
    - Note that `Login.java` is the first thing to run and will call the other classes.
 

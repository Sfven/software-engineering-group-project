@echo off

rem Run this script to start the program. Ensure you have run prepare_workspace once and have compiled the rest of the program using the compile script.

cd .\gamehub\src\gamehub\
java -cp "..\..\..\mysql-connector-j-8.2.0\mysql-connector-j-8.2.0.jar;." --module-path ..\..\..\javafx-sdk-21.0.1\lib\ --add-modules javafx.controls,javafx.fxml Login
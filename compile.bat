@echo off

rem Run this script to compile all classes in the program.

cd .\gamehub\src\gamehub\
echo "Compiling Login.java"
javac --module-path ..\..\..\javafx-sdk-21.0.1\lib\ --add-modules javafx.controls,javafx.fxml Login.java
echo "Compiling gamehub.java"
javac --module-path ..\..\..\javafx-sdk-21.0.1\lib\ --add-modules javafx.controls,javafx.fxml gamehub.java
echo "Compiling wordle.java"
javac --module-path ..\..\..\javafx-sdk-21.0.1\lib\ --add-modules javafx.controls,javafx.fxml wordle.java
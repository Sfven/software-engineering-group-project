#!/bin/sh
cd ./gamehub/src/gamehub/
export CLASSPATH=../../../mysql-connector-j-8.2.0/mysql-connector-j-8.2.0.jar:$CLASSPATH
java --module-path ../../../javafx-sdk-21.0.1/lib/ --add-modules javafx.controls,javafx.fxml Login.java


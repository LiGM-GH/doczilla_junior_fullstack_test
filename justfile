default:
    just --list

run:
    mvn process-resources
    mvn compile
    mvn exec:java -Dexec.mainClass=com.lipkingm.App

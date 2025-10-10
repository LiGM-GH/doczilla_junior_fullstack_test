default:
    just --list

run:
    mvn exec:java -Dexec.mainClass=com.lipkingm.App

test:
    mvn test

default:
    just --list

run:
    mvn dependency:resolve
    mvn process-resources
    mvn compile
    mvn exec:java -Dexec.mainClass=com.lipkingm.App

test:
    mvn test

clean:
    mvn clean
    mvn post-clean
    mvn build-helper:remove-project-artifact

bubble:
    mvn exec:java -Dexec.mainClass=com.lipkingm.Bubbles

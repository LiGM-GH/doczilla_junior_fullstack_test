default:
    just --list

run:
    mvn dependency:resolve
    mvn process-resources
    mvn compile
    mvn exec:java -Dexec.mainClass=com.lipkingm.App

clean:
    mvn clean
    mvn post-clean
    mvn build-helper:remove-project-artifact

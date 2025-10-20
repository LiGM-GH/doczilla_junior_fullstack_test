container_name := "redis-doczilla-junior-fullstack"

default:
    just --list

run:
    podman pull docker.io/redis
    if podman container exists {{container_name}}; then echo "Exists already"; else podman run --detach --name {{container_name}} -p 6379:6379 redis; fi
    mvn dependency:resolve
    mvn process-resources
    mvn compile
    mvn exec:java -Dexec.mainClass=com.lipkingm.App

test:
    mvn test

clean:
    # Each line is processed individually, so we can't wrap both "stop" and "rm" in one `if`.
    # This behaviour can be changed, but the current state is actually good enough.

    if podman container exists {{container_name}}; then podman container stop {{container_name}}; else echo "CONTAINER DIDN'T EXIST"; fi
    if podman container exists {{container_name}}; then podman container rm {{container_name}}; else echo "CONTAINER DIDN'T EXIST"; fi

    # Just aborts the execution on first error, and for `rm` it's an error if there's nothing to remove.
    # Thus, we need to be careful here not to abort the execution early.
    if rm weather-temp/*.js; then echo "Removed successfully"; else echo "NOTHING TO REMOVE"; fi

    mvn clean
    mvn post-clean
    mvn build-helper:remove-project-artifact

bubble:
    mvn dependency:resolve
    mvn process-resources
    mvn compile
    mvn exec:java -Dexec.mainClass=com.lipkingm.Bubbles

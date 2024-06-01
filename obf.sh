#!/bin/bash

# Run Gradle build
./gradlew build

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Gradle build successful."

    # Obfuscate with Grunt
    cd ./obf/
    java -jar grunt-1.5.7.jar
    cd ..

    echo "Obfuscation complete."

    # SFTP upload
    SFTP_HOST="192.168.1.199"
    SFTP_USER="trouper"
    SFTP_PASSWORD="Trouper12()1"
    SFTP_REMOTE_DIR="/home/trouper/docker/data/plugins/"

    # Specify the local file to upload
    LOCAL_FILE="./build/libs/Sentinel-0.2.5.jar"

    # Create a temporary file with a unique name
    TEMP_FILE=$(mktemp)

    # Write the SFTP commands to the temporary file
    echo "put $LOCAL_FILE $SFTP_REMOTE_DIR" > "$TEMP_FILE"
    echo "bye" >> "$TEMP_FILE"

    # Use sftp non-interactively with the specified commands
    sftp -oStrictHostKeyChecking=no -oBatchMode=no -b "$TEMP_FILE" "$SFTP_USER@$SFTP_HOST" <<EOF
    $SFTP_PASSWORD
EOF

    # Remove the temporary file
    rm -f "$TEMP_FILE"

    echo "File uploaded via SFTP."

    # SSH commands to reload the plugin on the host
    SSH_HOST="trouper@$SFTP_HOST"
    SSH_PASSWORD="Trouper12()1"

    SSH_COMMANDS=(
        "docker exec docker_mc_1 mc-send-to-console pm reload Sentinel"
        "docker exec docker_mc_1 mc-send-to-console tellraw @a '\"'[Server] Reload Complete, Upload Successful.'\"'"
    )

    for cmd in "${SSH_COMMANDS[@]}"; do
        sshpass -p "$SSH_PASSWORD" ssh -oStrictHostKeyChecking=no -oBatchMode=no "$SSH_HOST" "$cmd"
    done

    echo "Plugin reloaded."
else
    echo "Gradle build failed."
fi

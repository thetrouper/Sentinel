#!/bin/bash

# Navigate to the directory containing the Gradle project
cd "/run/media/trouper/1TB drive/IJ/IdeaProjects/Sentinel/" || exit

# Run the Gradle build command
./gradlew build

# Check if the build was successful
if [ $? -ne 0 ]; then
    echo "Gradle build failed"
    exit 1
fi

# Specify the output file path (modify this according to your build configuration)
OUTPUT_FILE_PATH="/run/media/trouper/1TB drive/IJ/IdeaProjects/Sentinel/build/libs/Sentinel-0.2.6.jar"

# Check if the output file exists
if [ ! -f "$OUTPUT_FILE_PATH" ]; then
    echo "Output file not found: $OUTPUT_FILE_PATH"
    exit 1
fi

# Upload the file to the SFTP server
HOST="yessir.network"
PORT="2022"
USER="obvwolf.1f8509dc"
PASSWORD='^8u%eQ2u6^TyuDU&$NNmW52s'  # Enclose the password in single quotes if it has special characters
REMOTE_DIR="/plugins/"

# Use 'lftp' to handle the SFTP upload
lftp -u "$USER","$PASSWORD" sftp://"$HOST":"$PORT" <<EOF
cd "$REMOTE_DIR"
put "$OUTPUT_FILE_PATH"
bye
EOF

if [ $? -ne 0 ]; then
    echo "File upload failed"
    exit 1
fi

echo "Build and upload completed successfully"

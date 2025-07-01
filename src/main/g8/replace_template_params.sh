#!/bin/bash

# Function to replace variables in a file based on a properties file
# Usage: replace_variables <file_path> <properties_file>
replace_variables() {
  local file_path="$1"
  local properties_file="$2"

  if [[ ! -f "$file_path" ]]; then
    echo "Error: File not found at '$file_path'"
    return 1
  fi

  if [[ ! -f "$properties_file" ]]; then
    echo "Error: Properties file not found at '$properties_file'"
    return 1
  fi

  echo "Processing file: $file_path"
  echo "Using properties from: $properties_file"

  # Read properties into an associative array (Bash 4+ required)
  declare -A props
  while IFS='=' read -r key value; do
    # Remove leading/trailing whitespace from key and value
    key=$(echo "$key" | xargs)
    value=$(echo "$value" | xargs)
    # Store in associative array
    props["$key"]="$value"
  done <"$properties_file"

  # Apply __norm rule: remove hyphens and convert to lowercase for __norm variables
  for key in "${!props[@]}"; do
    local original_value="${props[$key]}"
    # Normalize the value: remove hyphens and convert to lowercase
    local normalized_value=$(echo "$original_value" | sed 's/-//g' | tr '[:upper:]' '[:lower:]')
    props["${key}__norm"]="$normalized_value"
  done

  # Create temporary files for processing
  local temp_file_intermediate=$(mktemp)
  local temp_file_final=$(mktemp)

  # --- Pass 1: Un-escape \${VAR_NAME} to ${VAR_NAME} ---
  # This sed command replaces '\${' with '${' globally.
  # It targets patterns like \${IMAGE_NAME_FULL}
  sed 's/\\\${/\${/g' "$file_path" >"$temp_file_intermediate"

  # --- Pass 2: Replace \$variableName\$ with values from properties ---
  # Read from the intermediate file and apply property replacements
  while IFS= read -r line; do
    local modified_line="$line"
    # Iterate through keys in reverse order of length to ensure longer keys (like __norm) are replaced first
    # This prevents 'name' from being replaced before 'name__norm'
    for key in $(printf "%s\n" "${!props[@]}" | awk '{ print length, $0 }' | sort -rn | cut -d" " -f2-); do
      local value="${props[$key]}"
      # Escape slashes, ampersands, and the sed delimiter itself (if it's part of the value)
      local escaped_value=$(echo "$value" | sed -e 's/[\/&]/\\&/g')
      # Replace the variable pattern \$key\$ with its escaped value
      modified_line=$(echo "$modified_line" | sed "s/\\\$${key}\\\$/${escaped_value}/g")
    done
    echo "$modified_line" >>"$temp_file_final"
  done <"$temp_file_intermediate"

  # Overwrite the original file with the modified content from the final temporary file
  mv "$temp_file_final" "$file_path"
  # Clean up intermediate temporary file
  rm "$temp_file_intermediate"
  echo "Successfully replaced variables in '$file_path'"
}

# --- Main script execution ---

echo "Creating dummy default.properties..."
cat <<EOF >default.properties
name = spark-template
organization = net.underpost
scalaVersionUsed = 2.12.18
scalaVersionUsedMajor = 2.12
sparkVersion = 3.5.5
rapidsVersion = 25.04.0
description = An sbt template for Apache Spark applications with GPU support and ScalaTest.
version = 0.0.11
EOF
echo "default.properties created."
echo ""

echo "Running variable replacement on Dockerfile..."
replace_variables "Dockerfile" "default.properties"
echo ""

echo "Running variable replacement on build.sbt..."
replace_variables "build.sbt" "default.properties"
echo ""

echo "Running variable replacement on build.sh..."
replace_variables "build.sh" "default.properties"
echo ""

echo "Running variable replacement on manifests/sparkapplication/spark-application.yaml..."
replace_variables "manifests/sparkapplication/spark-application.yaml" "default.properties"
echo ""

echo "Running variable replacement on manifests/sparkapplication/spark-test-runner-gpu.yaml..."
replace_variables "manifests/sparkapplication/spark-test-runner-gpu.yaml" "default.properties"
echo ""

echo "Cleaning up dummy default.properties..."
rm default.properties
echo "Cleanup complete."

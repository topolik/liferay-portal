#!/bin/bash
echo -n '' > dependencies.xml

find -name 'build.gradle' | (while read buildFile; do 
    DIR=$(dirname "$buildFile")
    DIR_NAME=$(basename "$DIR")

    grep "compile[a-zA-Z]* group" "$buildFile" > /dev/null || continue

    # compileOnly group: "com.liferay.portal", name: "com.liferay.portal.kernel", version: "default"
    IFS=$'\n'
    for dep in $(grep "compile[a-zA-Z]* group" "$buildFile"); do
        GROUP=$(echo "$dep" | cut -d '"' -f 2)
        NAME=$(echo "$dep" | cut -d '"' -f 4)
        VERSION=$(echo "$dep" | cut -d '"' -f 6)

        cat >> dependencies.xml <<EOF
        <dependency><groupId>$GROUP</groupId><artifactId>$NAME</artifactId><version>$VERSION</version></dependency>
EOF
    done
    
done)

cat > "pom.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.liferay.portal</groupId>
    <artifactId>liferay-portal</artifactId>
    <name>liferay-portal</name>
    <version>master</version>
    <packaging>jar</packaging>
    <dependencies>
EOF

cat dependencies.xml | sort -u >> "pom.xml"

cat >> "pom.xml" <<EOF
    </dependencies>
</project>
EOF


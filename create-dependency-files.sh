#!/bin/bash

echo -n '' > package.json
echo -n '' > package-dependencies.json

git ls-files | grep 'package\(-lock\)\?.json' | while read p; do cat $p | jq '.dependencies?' >> package-dependencies.json; done
git ls-files | grep 'package\(-lock\)\?.json' | xargs git rm

grep ':[". ^0-9,]\+' package-dependencies.json | sed 's/\([^,]\)$/\1,/g' | sort -u | tr '\n' '#' | sed 's/^\(.*\),#$/{\1}/' | tr '#' '\n' | jq '{"name":"liferay-portal", "version": "master", "dependencies": .}' > package.json

git add package.json

echo -n '' > pom-dependencies-portal.xml
echo -n '' > pom-dependencies-modules.xml
echo -n '' > pom.xml

cat lib/portal/dependencies.properties | cut -d '=' -f 2 | while read dep; do
        GROUP=$(echo "$dep" | cut -d ':' -f 1)
        NAME=$(echo "$dep" | cut -d ':' -f 2)
        VERSION=$(echo "$dep" | cut -d ':' -f 3)

        cat >> pom-dependencies-portal.xml <<EOF
        <dependency><groupId>$GROUP</groupId><artifactId>$NAME</artifactId><version>$VERSION</version></dependency>
EOF
done

git ls-files | grep 'build.gradle' | while read buildFile; do
    DIR=$(dirname "$buildFile")
    DIR_NAME=$(basename "$DIR")

    grep "compileInclude group" "$buildFile" > /dev/null || continue

    # compileOnly group: "com.liferay.portal", name: "com.liferay.portal.kernel", version: "default"
    IFS=$'\n'
    for dep in $(grep "compileInclude group" "$buildFile"); do
        GROUP=$(echo "$dep" | cut -d '"' -f 2)
        NAME=$(echo "$dep" | cut -d '"' -f 4)
        VERSION=$(echo "$dep" | cut -d '"' -f 6)

        # String elasticsearchVersion = "6.5.0"
        # compileInclude group: "com.liferay", name: "org.elasticsearch.analysis.common", version: elasticsearchVersion
        if [ "$VERSION" == "" ]; then
            versionVar=$(echo "$dep" | sed 's/.*version: //')
            VERSION=$(grep "$versionVar[ ]\?=[^=]" "$buildFile" | tail -n 1 | cut -d '"' -f 2)
        fi

        cat >> pom-dependencies-modules.xml <<EOF
        <dependency><groupId>$GROUP</groupId><artifactId>$NAME</artifactId><version>$VERSION</version></dependency>
EOF
    done
done

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

$(cat pom-dependencies-*.xml | sort -u)

    </dependencies>
</project>
EOF

git add pom.xml

mvn validate


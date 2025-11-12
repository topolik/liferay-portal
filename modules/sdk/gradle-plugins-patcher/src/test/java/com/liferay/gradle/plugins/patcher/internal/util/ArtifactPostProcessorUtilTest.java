/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.patcher.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Istvan Sajtos
 */
public class ArtifactPostProcessorUtilTest {

	@Test
	public void testPostProcess() throws Exception {
		Path tempDirPath = Files.createTempDirectory("jartest");

		File jar1 = _getTestJar(
			tempDirPath, "test.jar", "old.group", "old-artifact", "0.0.1",
			"OldVendor", "old.vendor");

		String groupId = "new.group";
		String artifactId = "new-artifact";
		String version = "1.2.3";

		File jar2 = ArtifactPostProcessorUtil.getPostProcessedArtifact(
			jar1, groupId, artifactId, version);

		try (JarFile jarFile = new JarFile(jar2)) {
			_assertManifestValues(jarFile, artifactId, version);
			_assertPomPropertiesValues(jarFile, groupId, artifactId, version);
			_assertPomXmlValues(jarFile, groupId, artifactId, version);
		}

		Files.delete(jar1.toPath());
		Files.delete(jar2.toPath());
		Files.delete(tempDirPath);
	}

	private void _assertManifestValues(
			JarFile jarFile, String artifactId, String version)
		throws Exception {

		Manifest manifest = jarFile.getManifest();

		Attributes attributes = manifest.getMainAttributes();

		Assert.assertEquals(
			artifactId, attributes.getValue("Application-Name"));
		Assert.assertEquals(
			artifactId, attributes.getValue("Automatic-Module-Name"));
		Assert.assertEquals(artifactId, attributes.getValue("Bundle-Name"));
		Assert.assertEquals(
			artifactId, attributes.getValue("Bundle-SymbolicName"));
		Assert.assertEquals(artifactId, attributes.getValue("Extension-Name"));
		Assert.assertEquals(
			artifactId, attributes.getValue("Implementation-Title"));
		Assert.assertEquals(
			"Liferay, Inc", attributes.getValue("Bundle-Vendor"));
		Assert.assertEquals(
			"Liferay, Inc", attributes.getValue("Implementation-Vendor"));
		Assert.assertEquals(
			"com.liferay", attributes.getValue("Implementation-Vendor-Id"));
		Assert.assertEquals(version, attributes.getValue("Build-Id"));
		Assert.assertEquals(version, attributes.getValue("Build-Version"));
		Assert.assertEquals(version, attributes.getValue("Bundle-Version"));
		Assert.assertEquals(
			version, attributes.getValue("Implementation-Build-Id"));
		Assert.assertEquals(
			version, attributes.getValue("Implementation-Version"));
		Assert.assertEquals(version, attributes.getValue("Major-Version"));
	}

	private void _assertPomPropertiesValues(
			JarFile jarFile, String groupId, String artifactId, String version)
		throws Exception {

		try (InputStream inputStream = jarFile.getInputStream(
				jarFile.getJarEntry("pom.properties"))) {

			Properties props = new Properties();

			props.load(inputStream);

			Assert.assertEquals(groupId, props.getProperty("groupId"));
			Assert.assertEquals(artifactId, props.getProperty("artifactId"));
			Assert.assertEquals(version, props.getProperty("version"));
		}
	}

	private void _assertPomXmlValues(
			JarFile jarFile, String groupId, String artifactId, String version)
		throws Exception {

		try (InputStream inputStream = jarFile.getInputStream(
				jarFile.getJarEntry("pom.xml"))) {

			String content = new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);

			Assert.assertTrue(
				content.contains("<groupId>" + groupId + "</groupId>"));
			Assert.assertTrue(
				content.contains(
					"<artifactId>" + artifactId + "</artifactId>"));
			Assert.assertTrue(
				content.contains("<version>" + version + "</version>"));
		}
	}

	private File _getTestJar(
			Path dirPath, String fileName, String groupId, String artifactId,
			String version, String vendor, String vendorId)
		throws Exception {

		Path jarPath = dirPath.resolve(fileName);

		Manifest manifest = new Manifest();

		Attributes attributes = manifest.getMainAttributes();

		attributes.putValue("Application-Name", artifactId);
		attributes.putValue("Automatic-Module-Name", artifactId);
		attributes.putValue("Build-Id", version);
		attributes.putValue("Build-Version", version);
		attributes.putValue("Bundle-Name", artifactId);
		attributes.putValue("Bundle-SymbolicName", artifactId);
		attributes.putValue("Bundle-Vendor", vendor);
		attributes.putValue("Bundle-Version", version);
		attributes.putValue("Extension-Name", artifactId);
		attributes.putValue("Implementation-Build-Id", version);
		attributes.putValue("Implementation-Title", artifactId);
		attributes.putValue("Implementation-Version", version);
		attributes.putValue("Implementation-Vendor", vendor);
		attributes.putValue("Implementation-Vendor-Id", vendorId);
		attributes.putValue("Major-Version", version);
		attributes.putValue("Manifest-Version", "1.0");

		try (JarOutputStream jarOutputStream = new JarOutputStream(
				Files.newOutputStream(jarPath), manifest)) {

			jarOutputStream.putNextEntry(new JarEntry("pom.xml"));

			String pomXml =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project>\n" +
					"  <artifactId>old-artifact</artifactId>\n</project>\n";

			jarOutputStream.write(pomXml.getBytes(StandardCharsets.UTF_8));

			jarOutputStream.closeEntry();

			jarOutputStream.putNextEntry(new JarEntry("pom.properties"));

			Properties props = new Properties();

			props.setProperty("groupId", groupId);
			props.setProperty("artifactId", artifactId);
			props.setProperty("version", version);

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			props.store(byteArrayOutputStream, null);

			jarOutputStream.write(byteArrayOutputStream.toByteArray());

			jarOutputStream.closeEntry();
		}

		return jarPath.toFile();
	}

}
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.patcher.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Istvan Sajtos
 */
public class ArtifactPostProcessorUtil {

	public static File getPostProcessedArtifact(
			File jar, String groupId, String artifactId, String version)
		throws Exception {

		if (jar == null) {
			return null;
		}

		JarFile jarFile = new JarFile(jar);

		Manifest manifest = jarFile.getManifest();

		if (manifest != null) {
			_updateAttributes(manifest, _MANIFEST_NAME_KEYS, artifactId);
			_updateAttributes(
				manifest, _MANIFEST_VENDOR_ID_KEYS, "com.liferay");
			_updateAttributes(manifest, _MANIFEST_VENDOR_KEYS, "Liferay, Inc");
			_updateAttributes(manifest, _MANIFEST_VERSION_KEYS, version);
		}

		File newJar = new File(jar.getParent(), "new-" + jar.getName());

		try (JarOutputStream jarOutputStream = _getJarOutputStream(
				manifest, newJar)) {

			Enumeration<JarEntry> enumeration = jarFile.entries();

			while (enumeration.hasMoreElements()) {
				JarEntry entry = enumeration.nextElement();

				Path path = Paths.get(entry.getName());

				String fileName = String.valueOf(path.getFileName());

				if (fileName.equalsIgnoreCase("manifest.mf")) {
					continue;
				}

				try (InputStream entryInputStream = jarFile.getInputStream(
						entry)) {

					byte[] content = entryInputStream.readAllBytes();

					if (!_isMultiPomJar(jar)) {
						if (fileName.equalsIgnoreCase("pom.xml")) {
							content = _getUpdatedPomXml(
								content, groupId, artifactId, version);
						}
						else if (fileName.equalsIgnoreCase("pom.properties")) {
							content = _getUpdatedPomProperties(
								content, groupId, artifactId, version);
						}
					}

					jarOutputStream.putNextEntry(new JarEntry(entry.getName()));

					jarOutputStream.write(content);

					jarOutputStream.closeEntry();
				}
			}
		}

		return newJar;
	}

	private static JarOutputStream _getJarOutputStream(
			Manifest manifest, File jar)
		throws IOException {

		if (manifest != null) {
			return new JarOutputStream(new FileOutputStream(jar), manifest);
		}

		return new JarOutputStream(new FileOutputStream(jar));
	}

	private static byte[] _getUpdatedPomProperties(
			byte[] bytes, String groupId, String artifactId, String version)
		throws IOException {

		Properties props = new Properties();

		props.load(new ByteArrayInputStream(bytes));

		props.setProperty("groupId", groupId);
		props.setProperty("artifactId", artifactId);
		props.setProperty("version", version);

		try (ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream()) {

			props.store(byteArrayOutputStream, null);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private static byte[] _getUpdatedPomXml(
			byte[] bytes, String groupId, String artifactId, String version)
		throws Exception {

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		documentBuilderFactory.setIgnoringElementContentWhitespace(true);
		documentBuilderFactory.setFeature(
			"http://apache.org/xml/features/nonvalidating/load-external-dtd",
			false);

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		Document document;

		try (ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(bytes)) {

			document = documentBuilder.parse(byteArrayInputStream);
		}

		Element project = document.getDocumentElement();

		_updateOrCreateChild(
			project, "groupId", groupId, document, "artifactId", true);

		_updateOrCreateChild(
			project, "artifactId", artifactId, document, null, null);

		_updateOrCreateChild(
			project, "version", version, document, "artifactId", false);

		_removeWhitespaceNodes(project);

		TransformerFactory transformerFactory =
			TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		try (ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream()) {

			transformer.transform(
				new DOMSource(document),
				new StreamResult(byteArrayOutputStream));

			return byteArrayOutputStream.toByteArray();
		}
	}

	private static boolean _isMultiPomJar(File jar) throws IOException {
		int count = 0;

		JarFile jarFile = new JarFile(jar);

		Enumeration<JarEntry> enumeration = jarFile.entries();

		while (enumeration.hasMoreElements()) {
			JarEntry entry = enumeration.nextElement();

			Path path = Paths.get(entry.getName());

			String fileName = String.valueOf(path.getFileName());

			if (fileName.equals("pom.xml")) {
				count++;
			}
		}

		jarFile.close();

		if (count > 1) {
			return true;
		}

		return false;
	}

	private static void _removeWhitespaceNodes(Element element) {
		NodeList children = element.getChildNodes();

		for (int i = children.getLength() - 1; i >= 0; i--) {
			Node child = children.item(i);

			if (child.getNodeType() == Node.TEXT_NODE) {
				String textContent = child.getTextContent();

				textContent = textContent.trim();

				if (textContent.isEmpty()) {
					element.removeChild(child);
				}
			}
			else if (child.getNodeType() == Node.ELEMENT_NODE) {
				_removeWhitespaceNodes((Element)child);
			}
		}
	}

	private static void _updateAttributes(
		Manifest manifest, String[] keys, String value) {

		Attributes attributes = manifest.getMainAttributes();

		for (String key : keys) {
			if (attributes.containsKey(new Attributes.Name(key))) {
				attributes.putValue(key, value);
			}
		}
	}

	private static void _updateOrCreateChild(
		Element parent, String tagName, String value, Document document,
		String siblingTag, Boolean insertBefore) {

		NodeList nodes = parent.getElementsByTagName(tagName);

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getParentNode() == parent) {
				node.setTextContent(value);

				return;
			}
		}

		Element newElement = document.createElement(tagName);

		newElement.setTextContent(value);

		if (siblingTag != null) {
			NodeList siblingNodes = parent.getElementsByTagName(siblingTag);

			for (int i = 0; i < siblingNodes.getLength(); i++) {
				Node node = siblingNodes.item(i);

				if (node.getParentNode() == parent) {
					if (!insertBefore && (node.getNextSibling() != null)) {
						node = node.getNextSibling();
					}

					parent.insertBefore(newElement, node);

					return;
				}
			}
		}

		parent.appendChild(newElement);
	}

	private static final String[] _MANIFEST_NAME_KEYS = {
		"Application-Name", "Automatic-Module-Name", "Bundle-Name",
		"Bundle-SymbolicName", "Extension-Name", "Implementation-Title"
	};

	private static final String[] _MANIFEST_VENDOR_ID_KEYS = {
		"Implementation-Vendor-Id"
	};

	private static final String[] _MANIFEST_VENDOR_KEYS = {
		"Bundle-Vendor", "Implementation-Vendor"
	};

	private static final String[] _MANIFEST_VERSION_KEYS = {
		"Build-Id", "Build-Version", "Bundle-Version",
		"Implementation-Build-Id", "Implementation-Version", "Major-Version"
	};

}
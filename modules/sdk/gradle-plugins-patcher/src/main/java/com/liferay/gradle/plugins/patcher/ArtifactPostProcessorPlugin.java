/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.patcher;

import aQute.bnd.osgi.Constants;

import com.liferay.gradle.plugins.extensions.BundleExtension;
import com.liferay.gradle.plugins.patcher.internal.util.ArtifactPostProcessorUtil;
import com.liferay.gradle.plugins.util.BndUtil;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;

/**
 * @author Istvan Sajtos
 */
public class ArtifactPostProcessorPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.afterEvaluate(
			p -> {
				TaskContainer taskContainer = p.getTasks();

				_configurePostProcess(
					project, (Jar)taskContainer.findByName("jar"));
				_configurePostProcess(
					project, (Jar)taskContainer.findByName("jarSources"));
			});
	}

	private void _configurePostProcess(Project project, Jar jarTask) {
		if (jarTask == null) {
			return;
		}

		jarTask.doLast(
			task -> {
				Provider<RegularFile> provider = jarTask.getArchiveFile();

				RegularFile regularFile = provider.get();

				File jar = regularFile.getAsFile();

				if (jar == null) {
					return;
				}

				BundleExtension bundleExtension = BndUtil.getBundleExtension(
					project.getExtensions());

				File tempJar = null;

				try {
					tempJar =
						ArtifactPostProcessorUtil.getPostProcessedArtifact(
							jar, String.valueOf(project.getGroup()),
							bundleExtension.getInstruction(
								Constants.BUNDLE_SYMBOLICNAME),
							String.valueOf(project.getVersion()));
				}
				catch (Exception exception) {
					Logger logger = project.getLogger();

					StringBuilder sb = new StringBuilder();

					sb.append("Failed to post-process ");
					sb.append(jar.getName());
					sb.append(", which may therefore retain obsolete naming ");
					sb.append("and versioning information.");

					logger.warn(sb.toString(), exception);

					return;
				}

				try {
					Files.move(
						tempJar.toPath(), jar.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				}
				catch (IOException ioException) {
					throw new UncheckedIOException(ioException);
				}
			});
	}

}
/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.tool.cache.CachedToolInstaller;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptOptions;

import freemarker.template.Configuration;

public class ArtifactoryGradleInspectorResolver implements GradleInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String GRADLE_DIR_NAME = "gradle";
    private static final String GENERATED_GRADLE_SCRIPT_NAME = "init-detect.gradle";

    private final Configuration configuration;
    private final GradleInspectorScriptOptions gradleInspectorScriptOptions;
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final DirectoryManager directoryManager;
    private final CachedToolInstaller cachedToolInstaller;

    private File generatedGradleScriptPath = null;
    private boolean hasResolvedInspector = false;

    public ArtifactoryGradleInspectorResolver(Configuration configuration, GradleInspectorScriptOptions gradleInspectorScriptOptions,
        AirGapInspectorPaths airGapInspectorPaths,
        DirectoryManager directoryManager, CachedToolInstaller cachedToolInstaller) {
        this.configuration = configuration;
        this.gradleInspectorScriptOptions = gradleInspectorScriptOptions;
        this.airGapInspectorPaths = airGapInspectorPaths;
        this.directoryManager = directoryManager;
        this.cachedToolInstaller = cachedToolInstaller;
    }

    @Override
    public File resolveGradleInspector() throws DetectableException {
        if (!hasResolvedInspector) {
            hasResolvedInspector = true;
            try {
                Optional<File> airGapPath = airGapInspectorPaths.getGradleInspectorAirGapFile();
                File generatedGradleScriptFile = directoryManager.getSharedFile(GRADLE_DIR_NAME, GENERATED_GRADLE_SCRIPT_NAME);
                GradleInspectorScriptCreator gradleInspectorScriptCreator = new GradleInspectorScriptCreator(configuration);
                if (airGapPath.isPresent()) {
                    generatedGradleScriptPath = gradleInspectorScriptCreator.createOfflineGradleInspector(generatedGradleScriptFile, gradleInspectorScriptOptions, airGapPath.get().getCanonicalPath());
                } else {
                    generatedGradleScriptPath = gradleInspectorScriptCreator.createOnlineGradleInspector(generatedGradleScriptFile, gradleInspectorScriptOptions);
                }
            } catch (Exception e) {
                throw new DetectableException(e);
            }

            if (generatedGradleScriptPath == null) {
                throw new DetectableException("Unable to initialize the gradle inspector.");
            } else {
                logger.trace(String.format("Derived generated gradle script path: %s", generatedGradleScriptPath));
            }
        } else {
            logger.debug("Already attempted to resolve the gradle inspector script, will not attempt again.");
        }
        if (generatedGradleScriptPath == null) {
            throw new DetectableException("Unable to find or create the gradle inspector script.");
        }

        return generatedGradleScriptPath;
    }
}

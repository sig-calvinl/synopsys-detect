/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.tool.cache.InstalledTool;
import com.synopsys.integration.detect.tool.cache.InstalledToolData;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.ArtifactoryConstants;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.exception.IntegrationException;

public class DockerInspectorInstaller {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArtifactResolver artifactResolver;
    private final EventSystem eventSystem;

    public DockerInspectorInstaller(ArtifactResolver artifactResolver, EventSystem eventSystem) {
        this.artifactResolver = artifactResolver;
        this.eventSystem = eventSystem;
    }

    public File installJar(File dockerDirectory, Optional<String> dockerVersion) throws IntegrationException, IOException, DetectUserFriendlyException {
        logger.info("Determining the location of the Docker inspector.");
        String location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.DOCKER_INSPECTOR_REPO, ArtifactoryConstants.DOCKER_INSPECTOR_PROPERTY, dockerVersion.orElse(""),
            ArtifactoryConstants.DOCKER_INSPECTOR_VERSION_OVERRIDE);
        return download(location, dockerDirectory);
    }

    public File installAirGap(File dockerDirectory) throws IntegrationException, IOException, DetectUserFriendlyException {
        logger.info("Determining the location of the Docker inspector.");
        String location = artifactResolver.resolveArtifactLocation(ArtifactoryConstants.ARTIFACTORY_URL, ArtifactoryConstants.DOCKER_INSPECTOR_REPO, ArtifactoryConstants.DOCKER_INSPECTOR_AIR_GAP_PROPERTY, "", "");
        return download(location, dockerDirectory);
    }

    private File download(String location, File dockerDirectory) throws IntegrationException, IOException, DetectUserFriendlyException {
        logger.info("Finding or downloading the docker inspector.");
        logger.debug(String.format("Downloading docker inspector from '%s' to '%s'.", location, dockerDirectory.getAbsolutePath()));
        File jarFile = artifactResolver.downloadOrFindArtifact(dockerDirectory, location);
        logger.info("Found online docker inspector: " + jarFile.getAbsolutePath());

        eventSystem.publishEvent(Event.InstalledTool, new InstalledToolData(InstalledTool.DOCKER_INSPECTOR, jarFile.getAbsolutePath()));

        return jarFile;
    }

}

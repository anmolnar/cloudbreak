package com.sequenceiq.cloudbreak.service.image;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;
import com.sequenceiq.cloudbreak.core.CloudbreakImageCatalogException;
import com.sequenceiq.cloudbreak.core.CloudbreakImageNotFoundException;
import com.sequenceiq.cloudbreak.domain.stack.Stack;
import com.sequenceiq.cloudbreak.service.stack.StackService;

@Component
public class StackUpgradeImagesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackUpgradeImagesService.class);

    @Inject
    private StackService stackService;

    @Inject
    private ImageCatalogProvider imageCatalogProvider;

    @Inject
    private ImageService imageService;

    @Inject
    private StackUpgradeImageFilter stackUpgradeImageFilter;

    public Images getImagesToUpgrade(Long workspaceId, String stackName) {
        Images filteredImages = null;
        try {
            LOGGER.info(String.format("Retrieving images for upgrade stack %s", stackName));
            Stack stack = getStack(workspaceId, stackName);
            Image currentImage = getImage(stack);
            Images imagesFromCatalog = getImagesFromCatalog(currentImage.getImageCatalogUrl());
            filteredImages = filterImages(imagesFromCatalog, currentImage, stack.cloudPlatform());
            LOGGER.info(String.format("%d possible image found for stack upgrade.", filteredImages.getCdhImages().size()));
        } catch (CloudbreakImageNotFoundException | CloudbreakImageCatalogException e) {
            LOGGER.error("Failed to get images", e);
        }
        return filteredImages;
    }

    private Stack getStack(Long workspaceId, String stackName) {
        return stackService.getByNameInWorkspace(stackName, workspaceId);
    }

    private Image getImage(Stack stack) throws CloudbreakImageNotFoundException {
        return imageService.getImage(stack.getId());
    }

    private Images getImagesFromCatalog(String imageCatalogUrl) throws CloudbreakImageCatalogException {
        return imageCatalogProvider.getImageCatalogV2(imageCatalogUrl).getImages();
    }

    private Images filterImages(Images images, Image currentImage, String cloudPlatform) {
        return stackUpgradeImageFilter.filter(images, currentImage, cloudPlatform);
    }
}

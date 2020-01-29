package com.sequenceiq.cloudbreak.service.image;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.MaintenanceVersionComparator;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;

@Component
public class StackUpgradeImageFilter {

    private static final String CM_PACKAGE_KEY = "cm";

    private static final String STACK_PACKAGE_KEY = "stack";

    private static final String CFM_PACKAGE_KEY = "cfm";

    private static final String CSP_PACKAGE_KEY = "csp";

    private static final String SALT_PACKAGE_KEY = "salt";

    @Inject
    private MaintenanceVersionComparator maintenanceVersionComparator;

    Images filter(Images availableImages, Image currentImage, String cloudPlatform) {
        return new Images(null, null, null, availableImages.getCdhImages().stream()
                .filter(validateCmVersion(currentImage).or(validateStackVersion(currentImage)))
                .filter(validateCloudPlatform(cloudPlatform))
                .filter(validateOsVersion(currentImage))
                .filter(validateCfmVersion(currentImage))
                .filter(validateCspVersion(currentImage))
                .filter(validateSaltVersion(currentImage))
                .filter(filterCurrentImage(currentImage))
                .collect(Collectors.toList()), null);
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateOsVersion(Image currentImage) {
        return image -> isOsVersionsMatch(currentImage, image);
    }

    private boolean isOsVersionsMatch(Image currentImage, com.sequenceiq.cloudbreak.cloud.model.catalog.Image newImage) {
        return newImage.getOs().equalsIgnoreCase(currentImage.getOs())
                && newImage.getOsType().equalsIgnoreCase(currentImage.getOsType());
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateCmVersion(Image currentImage) {
        return image -> compareVersion(currentImage.getPackageVersions().get(CM_PACKAGE_KEY), image.getPackageVersions().get(CM_PACKAGE_KEY));
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateStackVersion(Image currentImage) {
        return image -> compareVersion(currentImage.getPackageVersions().get(STACK_PACKAGE_KEY), image.getPackageVersions().get(STACK_PACKAGE_KEY));
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateCloudPlatform(String cloudPlatform) {
        return image -> image.getImageSetsByProvider().keySet().stream().anyMatch(key -> key.equalsIgnoreCase(cloudPlatform));
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateCfmVersion(Image currentImage) {
        return image -> image.getPackageVersions().get(CFM_PACKAGE_KEY).equals(currentImage.getPackageVersions().get(CFM_PACKAGE_KEY));
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateCspVersion(Image currentImage) {
        return image -> image.getPackageVersions().get(CSP_PACKAGE_KEY).equals(currentImage.getPackageVersions().get(CSP_PACKAGE_KEY));
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> validateSaltVersion(Image currentImage) {
        return image -> image.getPackageVersions().get(SALT_PACKAGE_KEY).equals(currentImage.getPackageVersions().get(SALT_PACKAGE_KEY));
    }

    private Predicate<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> filterCurrentImage(Image currentImage) {
        return image -> !image.getUuid().equals(currentImage.getImageId());
    }

    private boolean compareVersion(String currentVersion, String newVersion) {
        return maintenanceVersionComparator.isGreaterVersion(currentVersion, newVersion);
    }
}

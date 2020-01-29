package com.sequenceiq.cloudbreak.service.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.sequenceiq.cloudbreak.cloud.MaintenanceVersionComparator;
import com.sequenceiq.cloudbreak.cloud.model.Image;
import com.sequenceiq.cloudbreak.cloud.model.catalog.Images;

@RunWith(MockitoJUnitRunner.class)
public class StackUpgradeImageFilterTest {

    private static final String CLOUD_PLATFORM = "aws";

    private static final String OS_TYPE = "redhat7";

    private static final String OS = "centos7";

    private static final String V_7_0_3 = "7.0.3";

    private static final String V_7_0_2 = "7.0.2";

    private static final String CMF_VERSION = "2.0.0.0-121";

    private static final String CSP_VERSION = "3.0.0.0-103";

    private static final String SALT_VERSION = "2017.7.5";

    private static final String CURRENT_IMAGE_ID = "f58c5f97-4609-4b47-6498-1c1bc6a4501c";

    private static final String IMAGE_ID = "f7f3fc53-b8a6-4152-70ac-7059ec8f8443";

    private static final Map<String, String> IMAGE_MAP = Collections.emptyMap();

    @InjectMocks
    private StackUpgradeImageFilter underTest;

    @Mock
    private MaintenanceVersionComparator maintenanceVersionComparator;

    private Image currentImage;

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image properImage;

    @Before
    public void before() {
        currentImage = createCurrentImage();
        properImage = createProperImage();
    }

    @Test
    public void testFilterShouldReturnTheAvailableImage() {
        Images availableImages = createAvailableImages(List.of(properImage));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertEquals(1, actual.getCdhImages().size());
    }

    @Test
    public void testFilterShouldReturnTheProperImageWhenTheCloudPlatformIsNotMatches() {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image azureImage = createImageWithDifferentPlatform();
        Images availableImages = createAvailableImages(List.of(properImage, azureImage));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertEquals(1, actual.getCdhImages().size());
    }

    @Test
    public void testFilterShouldReturnTheProperImageWhenTheCmVersionIsNotGreater() {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image lowerCmImage = createImageWithLowerCmVersion();
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image lowerCmAndCdpImage = createImageWithLowerCmAndCdpVersion();
        Images availableImages = createAvailableImages(List.of(properImage, lowerCmImage, lowerCmAndCdpImage));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);
        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_2)).thenReturn(false);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertTrue(actual.getCdhImages().contains(lowerCmImage));
        assertEquals(2, actual.getCdhImages().size());
    }

    @Test
    public void testFilterShouldReturnTheProperImageWhenTheCmfVersionIsNotMatches() {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image differentCmfVersionImage = createImageWithDifferentCmfVersion();
        Images availableImages = createAvailableImages(List.of(properImage, differentCmfVersionImage));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertEquals(1, actual.getCdhImages().size());
    }

    @Test
    public void testFilterShouldReturnTheProperImageWhenTheCspVersionIsNotMatches() {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image differentCspVersionImage = createImageWithDifferentCspVersion();
        Images availableImages = createAvailableImages(List.of(properImage, differentCspVersionImage));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertEquals(1, actual.getCdhImages().size());
    }

    @Test
    public void testFilterShouldReturnTheProperImageWhenTheSaltVersionIsNotMatches() {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image differentSaltVersionImage = createImageWithDifferentSaltVersion();
        Images availableImages = createAvailableImages(List.of(properImage, differentSaltVersionImage));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertEquals(1, actual.getCdhImages().size());
    }

    @Test
    public void testFilterShouldReturnTheProperImageWhenTheCurrentImageIsAlsoAvailable() {
        com.sequenceiq.cloudbreak.cloud.model.catalog.Image imageWithSameId = createImageWithCurrentImageId();
        Images availableImages = createAvailableImages(List.of(properImage, imageWithSameId));

        when(maintenanceVersionComparator.isGreaterVersion(V_7_0_2, V_7_0_3)).thenReturn(true);

        Images actual = underTest.filter(availableImages, currentImage, CLOUD_PLATFORM);

        assertTrue(actual.getCdhImages().contains(properImage));
        assertEquals(1, actual.getCdhImages().size());
    }

    private Image createCurrentImage() {
        return new Image(null, null, OS, OS_TYPE, null, null, CURRENT_IMAGE_ID,
                createPackageVersions(V_7_0_2, V_7_0_2, CMF_VERSION, CSP_VERSION, SALT_VERSION));
    }

    private Images createAvailableImages(List<com.sequenceiq.cloudbreak.cloud.model.catalog.Image> images) {
        return new Images(null, null, null, images, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createProperImage() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null,
                Map.of(CLOUD_PLATFORM, Collections.emptyMap()), null, OS_TYPE,
                createPackageVersions(V_7_0_3, V_7_0_3, CMF_VERSION, CSP_VERSION, SALT_VERSION),
                null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithDifferentPlatform() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null, Map.of("azure", IMAGE_MAP), null, OS_TYPE,
                createPackageVersions(V_7_0_3, V_7_0_3, CMF_VERSION, CSP_VERSION, SALT_VERSION),
                null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithLowerCmVersion() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null,
                Map.of(CLOUD_PLATFORM, IMAGE_MAP), null, OS_TYPE, createPackageVersions(V_7_0_2, V_7_0_3, CMF_VERSION, CSP_VERSION, SALT_VERSION),
                null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithLowerCmAndCdpVersion() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null, Map.of(CLOUD_PLATFORM, IMAGE_MAP), null,
                OS_TYPE, createPackageVersions(V_7_0_2, V_7_0_2, CMF_VERSION, CSP_VERSION, SALT_VERSION), null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithDifferentCmfVersion() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null, Map.of(CLOUD_PLATFORM, IMAGE_MAP), null,
                OS_TYPE, createPackageVersions(V_7_0_3, V_7_0_3, "3.0.0.0-121", CSP_VERSION, SALT_VERSION), null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithDifferentCspVersion() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null, Map.of(CLOUD_PLATFORM, IMAGE_MAP), null,
                OS_TYPE, createPackageVersions(V_7_0_3, V_7_0_3, CMF_VERSION, "4.0.0.0-103", SALT_VERSION), null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithDifferentSaltVersion() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, IMAGE_ID, null, null, Map.of(CLOUD_PLATFORM, IMAGE_MAP), null,
                OS_TYPE, createPackageVersions(V_7_0_3, V_7_0_3, CMF_VERSION, CSP_VERSION, "2018.7.5"), null, null, null);
    }

    private com.sequenceiq.cloudbreak.cloud.model.catalog.Image createImageWithCurrentImageId() {
        return new com.sequenceiq.cloudbreak.cloud.model.catalog.Image(null, null, null, OS, CURRENT_IMAGE_ID, null, null, Map.of(CLOUD_PLATFORM, IMAGE_MAP),
                null, OS_TYPE, createPackageVersions(V_7_0_3, V_7_0_3, CMF_VERSION, CSP_VERSION, SALT_VERSION), null, null, null);
    }

    private Map<String, String> createPackageVersions(String cmVersion, String cdhVersion, String cmfVersion, String cspVersion, String saltVersion) {
        return Map.of(
                "cm", cmVersion,
                "stack", cdhVersion,
                "cfm", cmfVersion,
                "csp", cspVersion,
                "salt", saltVersion);
    }
}
package com.sequenceiq.cloudbreak.common.service;

import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.DISK;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.INSTANCE;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.IP;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.NETWORK;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.NOSQL;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.SECURITY;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.STORAGE;
import static com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType.TEMPLATE;
import static com.sequenceiq.cloudbreak.common.type.DefaultApplicationTag.CDP_CB_VERSION;
import static com.sequenceiq.cloudbreak.common.type.DefaultApplicationTag.CDP_CREATION_TIMESTAMP;
import static com.sequenceiq.cloudbreak.common.type.DefaultApplicationTag.CDP_USER_NAME;
import static com.sequenceiq.cloudbreak.common.type.DefaultApplicationTag.ENVIRONMENT_CRN;
import static com.sequenceiq.cloudbreak.common.type.DefaultApplicationTag.OWNER;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.sequenceiq.cloudbreak.common.cost.CostTagging;
import com.sequenceiq.cloudbreak.common.type.CloudConstants;
import com.sequenceiq.cloudbreak.common.type.CloudbreakResourceType;
import com.sequenceiq.cloudbreak.common.type.DefaultApplicationTag;

@Service
public class DefaultCostTaggingService implements CostTagging {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCostTaggingService.class);

    @Value("${info.app.version:}")
    private String cbVersion;

    @Inject
    private Clock clock;

    public Map<String, String> prepareInstanceTagging() {
        return prepareResourceTag(INSTANCE);
    }

    public Map<String, String> prepareNetworkTagging() {
        return prepareResourceTag(NETWORK);
    }

    public Map<String, String> prepareTemplateTagging() {
        return prepareResourceTag(TEMPLATE);
    }

    public Map<String, String> prepareSecurityTagging() {
        return prepareResourceTag(SECURITY);
    }

    public Map<String, String> prepareIpTagging() {
        return prepareResourceTag(IP);
    }

    public Map<String, String> prepareDiskTagging() {
        return prepareResourceTag(DISK);
    }

    public Map<String, String> prepareNoSqlTagging() {
        return prepareResourceTag(NOSQL);
    }

    public Map<String, String> prepareStorageTagging() {
        return prepareResourceTag(STORAGE);
    }

    public Map<String, String> prepareAllTagsForTemplate() {
        Map<String, String> result = new HashMap<>();
        for (CloudbreakResourceType cloudbreakResourceType : CloudbreakResourceType.values()) {
            result.put(cloudbreakResourceType.templateVariable(), cloudbreakResourceType.key());
        }
        return result;
    }

    @Override
    public Map<String, String> prepareDefaultTags(String cbUser, Map<String, String> sourceMap, String platform, String environmentCrn) {
        LOGGER.debug("About to prepare default tag(s)...");
        Map<String, String> result = new HashMap<>();
        result.put(transform(CDP_USER_NAME.key(), platform), transform(cbUser, platform));
        result.put(transform(CDP_CB_VERSION.key(), platform), transform(cbVersion, platform));
        addEnvironmentCrnIfPresent(result, environmentCrn, platform);
        if (sourceMap == null || Strings.isNullOrEmpty(sourceMap.get(transform(OWNER.key(), platform)))) {
            result.put(transform(OWNER.key(), platform), transform(cbUser, platform));
        }
        result.put(transform(CDP_CREATION_TIMESTAMP.key(), platform), transform(String.valueOf(clock.getCurrentInstant().getEpochSecond()), platform));
        LOGGER.debug("The following default tag(s) has prepared: {}", result);
        return result;
    }

    public void addEnvironmentCrnIfPresent(Map<String, String> mapOfTags, String environmentCrn, String platform) {
        if (StringUtils.isNotEmpty(environmentCrn)) {
            mapOfTags.put(transform(ENVIRONMENT_CRN.key(), platform), environmentCrn);
        } else {
            LOGGER.debug("Unable to add \"{}\" - cost - tag to the resource's default tags because it's value is empty or null!", ENVIRONMENT_CRN.key());
        }
    }

    public String transform(String value, String platform) {
        String valueAfterCheck = Strings.isNullOrEmpty(value) ? "unknown" : value;
        return CloudConstants.GCP.equals(platform)
                ? valueAfterCheck.split("@")[0].toLowerCase().replaceAll("[^\\w]", "-") : valueAfterCheck;
    }

    public Map<String, String> prepareResourceTag(CloudbreakResourceType cloudbreakResourceType) {
        Map<String, String> result = new HashMap<>();
        result.put(DefaultApplicationTag.CDP_RESOURCE_TYPE.key(), cloudbreakResourceType.key());
        return result;
    }

}

package com.sequenceiq.environment.environment.validation.validators;

import static com.sequenceiq.cloudbreak.common.mappable.CloudPlatform.AWS;
import static com.sequenceiq.cloudbreak.common.mappable.CloudPlatform.AZURE;
import static com.sequenceiq.cloudbreak.common.mappable.CloudPlatform.MOCK;
import static com.sequenceiq.cloudbreak.common.mappable.CloudPlatform.YARN;
import static com.sequenceiq.cloudbreak.common.mappable.CloudPlatform.valueOf;
import static com.sequenceiq.cloudbreak.util.ConditionBasedEvaluatorUtil.doIfFalse;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.cloudbreak.validation.SubnetValidator;
import com.sequenceiq.cloudbreak.validation.ValidationResult;
import com.sequenceiq.cloudbreak.validation.ValidationResult.ValidationResultBuilder;
import com.sequenceiq.environment.environment.dto.EnvironmentDto;
import com.sequenceiq.environment.environment.dto.SecurityAccessDto;
import com.sequenceiq.environment.environment.validation.network.EnvironmentNetworkValidator;
import com.sequenceiq.environment.environment.validation.securitygroup.EnvironmentSecurityGroupValidator;
import com.sequenceiq.environment.network.dto.NetworkDto;

@Component
public class EnvironmentNetworkProviderValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentNetworkProviderValidator.class);

    private static final String EXPECTED_NETWORK_MASK = "16";

    private final Map<CloudPlatform, EnvironmentNetworkValidator> environmentNetworkValidatorsByCloudPlatform;

    private final Map<CloudPlatform, EnvironmentSecurityGroupValidator> environmentSecurityGroupValidatorsByCloudPlatform;

    public EnvironmentNetworkProviderValidator(
            Map<CloudPlatform, EnvironmentNetworkValidator> environmentNetworkValidatorsByCloudPlatform,
            Map<CloudPlatform, EnvironmentSecurityGroupValidator> environmentSecurityGroupValidatorsByCloudPlatform) {
        this.environmentNetworkValidatorsByCloudPlatform = environmentNetworkValidatorsByCloudPlatform;
        this.environmentSecurityGroupValidatorsByCloudPlatform = environmentSecurityGroupValidatorsByCloudPlatform;
    }

    public ValidationResult validate(EnvironmentDto environmentDto) {
        NetworkDto network = environmentDto.getNetwork();
        String cloudPlatform = environmentDto.getCloudPlatform();
        ValidationResultBuilder resultBuilder = ValidationResult.builder();
        validateNetworkHasTheSamePropertyFilledAsTheDesiredCloudPlatform(network, cloudPlatform, resultBuilder);
        validateNetwork(network, cloudPlatform, resultBuilder);
        validateSecurityGroup(environmentDto, cloudPlatform, resultBuilder);
        return resultBuilder.build();
    }

    private void validateNetworkHasTheSamePropertyFilledAsTheDesiredCloudPlatform(NetworkDto networkDto, String cloudPlatform,
            ValidationResultBuilder resultBuilder) {

        if (nonNull(networkDto) && isEmpty(networkDto.getNetworkCidr())) {
            Map<CloudPlatform, Optional<Object>> providerNetworkParamPair = Map.of(
                    AWS, optional(networkDto.getAws()),
                    AZURE, optional(networkDto.getAzure()),
                    MOCK, optional(networkDto.getMock()),
                    YARN, optional(networkDto.getYarn())
            );

            LOGGER.debug("About to validate network properties for cloud platform \"{}\" against the following supported platforms: {}",
                    cloudPlatform, String.join(", ", providerNetworkParamPair.keySet().stream().map(Enum::name).collect(Collectors.toSet())));

            providerNetworkParamPair.keySet().stream()
                    .filter(cloudProviderName -> cloudProviderName.name().equalsIgnoreCase(cloudPlatform))
                    .findFirst()
                    .ifPresentOrElse(cloudProvider -> evaluateProviderNetworkRelation(providerNetworkParamPair.get(cloudProvider).isPresent(), cloudPlatform,
                            resultBuilder), () -> resultBuilder.error("Unable to find cloud platform (\"" + cloudPlatform + "\") for network property!"));
        }
    }

    private void evaluateProviderNetworkRelation(boolean networkParamExists, String cloudPlatform, ValidationResultBuilder resultBuilder) {
        doIfFalse(networkParamExists, cloudPlatform,
                ignore -> resultBuilder.error(String.format("The related network parameter for the cloud platform \"%s\" has not given!",
                        cloudPlatform)));
    }

    private Optional<Object> optional(Object o) {
        return Optional.ofNullable(o);
    }

    private void validateNetwork(NetworkDto networkDto, String cloudPlatform, ValidationResultBuilder resultBuilder) {
        if (networkDto != null && Strings.isNullOrEmpty(networkDto.getNetworkCidr())) {
            EnvironmentNetworkValidator environmentNetworkValidator = environmentNetworkValidatorsByCloudPlatform.get(valueOf(cloudPlatform));
            if (networkDto.getNetworkCidr() != null && isInvalidNetworkMask(networkDto.getNetworkCidr())) {
                resultBuilder.error(String.format("The netmask must be /%s.", EXPECTED_NETWORK_MASK));
            }
            if (environmentNetworkValidator != null) {
                environmentNetworkValidator.validateDuringFlow(networkDto, resultBuilder);
            } else {
                resultBuilder.error(String.format("Environment specific network is not supported for cloud platform: '%s'!", cloudPlatform));
            }
        }
    }

    private boolean isInvalidNetworkMask(String networkCidr) {
        return !new SubnetValidator().isValid(networkCidr, null) || !networkCidr.split("/")[1].equals(EXPECTED_NETWORK_MASK);
    }

    private void validateSecurityGroup(EnvironmentDto request, String cloudPlatform, ValidationResultBuilder resultBuilder) {
        SecurityAccessDto securityAccess = request.getSecurityAccess();
        NetworkDto networkDto = request.getNetwork();
        if (securityAccess != null && networkDto != null) {
            EnvironmentSecurityGroupValidator environmentSecurityGroupValidator =
                    environmentSecurityGroupValidatorsByCloudPlatform.get(valueOf(cloudPlatform));
            if (environmentSecurityGroupValidator != null) {
                environmentSecurityGroupValidator.validate(request, resultBuilder);
            } else if (!MOCK.equalsIgnoreCase(cloudPlatform) && !YARN.equalsIgnoreCase(cloudPlatform)) {
                resultBuilder.error(String.format("Environment specific security group is not supported for cloud platform: '%s'!", cloudPlatform));
            }
        }
    }
}
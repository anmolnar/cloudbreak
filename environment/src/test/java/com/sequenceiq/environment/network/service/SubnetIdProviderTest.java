package com.sequenceiq.environment.network.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sequenceiq.cloudbreak.cloud.model.CloudSubnet;
import com.sequenceiq.common.api.type.Tunnel;
import com.sequenceiq.environment.network.dto.NetworkDto;

class SubnetIdProviderTest {

    private static final String SUBNET_ID_1 = "subnetId1";

    private static final String SUBNET_ID_2 = "subnetId2";

    private static final String SUBNET_ID_3 = "subnetId3";

    private SubnetIdProvider underTest = new SubnetIdProvider();

    @Test
    void testProvideShouldReturnAPrivateWhenCcm() {
        Map<String, CloudSubnet> subnetMetas = createSubnetMetas();
        NetworkDto networkDto = NetworkDto.builder()
                .withSubnetMetas(subnetMetas)
                .build();

        String actual = underTest.provide(networkDto, Tunnel.CCM);

        Assertions.assertTrue(StringUtils.isNotBlank(actual));
        Assertions.assertTrue(subnetMetas.get(actual).isPrivateSubnet());
    }

    @Test
    void testProvideShouldReturnAPublicSubnetIDWhenNoCcm() {
        Map<String, CloudSubnet> subnetMetas = createSubnetMetas();
        NetworkDto networkDto = NetworkDto.builder()
                .withSubnetMetas(subnetMetas)
                .build();

        String actual = underTest.provide(networkDto, Tunnel.DIRECT);

        Assertions.assertTrue(StringUtils.isNotBlank(actual));
        Assertions.assertFalse(subnetMetas.get(actual).isPrivateSubnet());
    }

    @Test
    void testProvideShouldReturnAPublicSubnetIDWhenCcmNoPrivate() {
        Map<String, CloudSubnet> subnetMetas = createPublicSubnetMetas();
        NetworkDto networkDto = NetworkDto.builder()
                .withSubnetMetas(subnetMetas)
                .build();

        String actual = underTest.provide(networkDto, Tunnel.CCM);

        Assertions.assertTrue(StringUtils.isNotBlank(actual));
        Assertions.assertFalse(subnetMetas.get(actual).isPrivateSubnet());
    }

    @Test
    void testProvideShouldReturnAPrivateWhenNoCcmAndNoPublic() {
        Map<String, CloudSubnet> subnetMetas = createPrivateSubnetMetas();
        NetworkDto networkDto = NetworkDto.builder()
                .withSubnetMetas(subnetMetas)
                .build();

        String actual = underTest.provide(networkDto, Tunnel.DIRECT);

        Assertions.assertTrue(StringUtils.isNotBlank(actual));
        Assertions.assertTrue(subnetMetas.get(actual).isPrivateSubnet());
    }

    private Map<String, CloudSubnet> createSubnetMetas() {
        return Map.of(
                SUBNET_ID_1, new CloudSubnet(SUBNET_ID_1, SUBNET_ID_1, "az", "cidr", true, true, true),
                SUBNET_ID_2, new CloudSubnet(SUBNET_ID_2, SUBNET_ID_2, "az", "cidr", false, true, true),
                SUBNET_ID_3, new CloudSubnet(SUBNET_ID_3, SUBNET_ID_3, "az", "cidr", true, true, true));
    }

    private Map<String, CloudSubnet> createPublicSubnetMetas() {
        return Map.of(
                SUBNET_ID_1, new CloudSubnet(SUBNET_ID_1, SUBNET_ID_1, "az", "cidr", false, true, true),
                SUBNET_ID_2, new CloudSubnet(SUBNET_ID_2, SUBNET_ID_2, "az", "cidr", false, true, true),
                SUBNET_ID_3, new CloudSubnet(SUBNET_ID_3, SUBNET_ID_3, "az", "cidr", false, true, true));
    }

    private Map<String, CloudSubnet> createPrivateSubnetMetas() {
        return Map.of(
                SUBNET_ID_1, new CloudSubnet(SUBNET_ID_1, SUBNET_ID_1, "az", "cidr", true, true, true),
                SUBNET_ID_2, new CloudSubnet(SUBNET_ID_2, SUBNET_ID_2, "az", "cidr", true, true, true),
                SUBNET_ID_3, new CloudSubnet(SUBNET_ID_3, SUBNET_ID_3, "az", "cidr", true, true, true));
    }

}
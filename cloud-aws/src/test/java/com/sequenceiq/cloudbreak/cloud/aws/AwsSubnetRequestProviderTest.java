package com.sequenceiq.cloudbreak.cloud.aws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.sequenceiq.cloudbreak.cloud.model.network.SubnetRequest;

@RunWith(MockitoJUnitRunner.class)
public class AwsSubnetRequestProviderTest {

    private static final String CIDR_1 = "1.1.1.1/24";

    private static final String CIDR_2 = "2.2.2.2/24";

    private static final String CIDR_3 = "3.3.3.3/24";

    private static final String CIDR_4 = "4.4.4.4/24";

    private static final String CIDR_5 = "5.5.5.5/24";

    private static final String CIDR_6 = "6.6.6.6/24";

    private static final String AZ_1 = "London";

    private static final String AZ_2 = "Bristol";

    private static final String AZ_3 = "Manchester";

    private static final String AZ_4 = "Oxford";

    @InjectMocks
    private AwsSubnetRequestProvider underTest;

    @Test
    public void testProvideWhenTwoAzAvailable() {
        AmazonEC2Client ec2Client = createEc2Client(List.of(createAZ(AZ_1), createAZ(AZ_2)));
        List<String> subnetCidrs = List.of(CIDR_1, CIDR_2, CIDR_3, CIDR_4, CIDR_5, CIDR_6);

        List<SubnetRequest> actual = underTest.provide(ec2Client, subnetCidrs);

        assertEquals(CIDR_1, actual.get(0).getPublicSubnetCidr());
        assertEquals(CIDR_2, actual.get(0).getPrivateSubnetCidr());
        assertEquals(AZ_1, actual.get(0).getAvailabilityZone());
        assertEquals(CIDR_3, actual.get(1).getPublicSubnetCidr());
        assertEquals(CIDR_4, actual.get(1).getPrivateSubnetCidr());
        assertEquals(AZ_2, actual.get(1).getAvailabilityZone());
        assertEquals(CIDR_5, actual.get(2).getPublicSubnetCidr());
        assertEquals(CIDR_6, actual.get(2).getPrivateSubnetCidr());
        assertEquals(AZ_2, actual.get(2).getAvailabilityZone());
    }

    @Test
    public void testProvideWhenFourAzAvailable() {
        AmazonEC2Client ec2Client = createEc2Client(List.of(createAZ(AZ_1), createAZ(AZ_2), createAZ(AZ_3), createAZ(AZ_4)));
        List<String> subnetCidrs = List.of(CIDR_1, CIDR_2, CIDR_3, CIDR_4, CIDR_5, CIDR_6);

        List<SubnetRequest> actual = underTest.provide(ec2Client, subnetCidrs);

        assertEquals(CIDR_1, actual.get(0).getPublicSubnetCidr());
        assertEquals(CIDR_2, actual.get(0).getPrivateSubnetCidr());
        assertEquals(AZ_1, actual.get(0).getAvailabilityZone());
        assertEquals(CIDR_3, actual.get(1).getPublicSubnetCidr());
        assertEquals(CIDR_4, actual.get(1).getPrivateSubnetCidr());
        assertEquals(AZ_2, actual.get(1).getAvailabilityZone());
        assertEquals(CIDR_5, actual.get(2).getPublicSubnetCidr());
        assertEquals(CIDR_6, actual.get(2).getPrivateSubnetCidr());
        assertEquals(AZ_3, actual.get(2).getAvailabilityZone());
    }

    @Test
    public void testProvideWhenOnlyTwoCidrProvided() {
        AmazonEC2Client ec2Client = createEc2Client(List.of(createAZ(AZ_1), createAZ(AZ_2), createAZ(AZ_3), createAZ(AZ_4)));
        List<String> subnetCidrs = List.of(CIDR_1, CIDR_2);

        List<SubnetRequest> actual = underTest.provide(ec2Client, subnetCidrs);

        assertEquals(CIDR_1, actual.get(0).getPublicSubnetCidr());
        assertEquals(CIDR_2, actual.get(0).getPrivateSubnetCidr());
        assertEquals(AZ_1, actual.get(0).getAvailabilityZone());
        assertTrue(actual.size() == 1);
    }

    private AmazonEC2Client createEc2Client(List<AvailabilityZone> availabilityZones) {
        AmazonEC2Client ec2Client = Mockito.mock(AmazonEC2Client.class);
        DescribeAvailabilityZonesResult result = new DescribeAvailabilityZonesResult();
        result.setAvailabilityZones(availabilityZones);
        Mockito.when(ec2Client.describeAvailabilityZones()).thenReturn(result);
        return ec2Client;
    }

    private AvailabilityZone createAZ(String name) {
        AvailabilityZone availabilityZone = new AvailabilityZone();
        availabilityZone.setZoneName(name);
        return availabilityZone;
    }

}
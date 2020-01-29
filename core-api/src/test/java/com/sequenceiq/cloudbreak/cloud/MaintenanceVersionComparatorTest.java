package com.sequenceiq.cloudbreak.cloud;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MaintenanceVersionComparatorTest {

    private MaintenanceVersionComparator underTest = new MaintenanceVersionComparator();

    @Test
    public void testIsGreaterVersionShouldReturnsTrueWhenTheNewVersionIsGreaterThanTheCurrent() {
        boolean actual = underTest.isGreaterVersion("7.0.2", "7.0.3");
        assertTrue(actual);
    }

    @Test
    public void testIsGreaterVersionShouldReturnsFalseWhenTheNewVersionIsLowerThanTheCurrent() {
        boolean actual = underTest.isGreaterVersion("7.0.3", "7.0.2");
        assertFalse(actual);
    }

    @Test
    public void testIsGreaterVersionShouldReturnsFalseWhenTheVersionsAreEqual() {
        boolean actual = underTest.isGreaterVersion("7.0.3", "7.0.3");
        assertFalse(actual);
    }

    @Test
    public void testIsGreaterVersionShouldReturnsFalseWhenTheNewVersionIsContainsOtherCharacter() {
        boolean actual = underTest.isGreaterVersion("7.0.3", "7.x.2");
        assertFalse(actual);
    }

    @Test
    public void testIsGreaterVersionShouldReturnsFalseWhenTheNewVersionIsNotValid() {
        boolean actual = underTest.isGreaterVersion("7.0.3", "version1");
        assertFalse(actual);
    }

    @Test
    public void testIsGreaterVersionShouldReturnsFalseWhenTheVersionsAreNull() {
        boolean actual = underTest.isGreaterVersion(null, null);
        assertFalse(actual);
    }
}
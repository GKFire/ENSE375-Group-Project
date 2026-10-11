package ense375.personalpasswordmanager.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HMACUtilTest {
	
	@Test
	void testHOTP() {
		assertEquals(439256, HMACUtil.HOTP_SHA_1("JBSWY3DPFQQHO33SNRSCC", 42, 6));
	}
}

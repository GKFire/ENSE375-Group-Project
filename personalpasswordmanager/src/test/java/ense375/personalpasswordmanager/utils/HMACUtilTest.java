package ense375.personalpasswordmanager.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class)
public class HMACUtilTest {
	
	@Test
	@Order(1)
	void testConvertStringKey() {
		
	}
	
	@Test
	@Order(1)
	void testConcatByteArrays() {
		
	}
	
	@Test
	@Order(2)
	void testHMAC_SHA_1() {
		
	}
	
	@Test
	@Order(3)
	void testHOTP_SHA_1() {
		assertEquals(439256, HMACUtil.HOTP_SHA_1("JBSWY3DPFQQHO33SNRSCC", 42, 6));
	}
}

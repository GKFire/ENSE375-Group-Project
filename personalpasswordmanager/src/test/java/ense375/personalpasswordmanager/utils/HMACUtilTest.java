package ense375.personalpasswordmanager.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class)
public class HMACUtilTest {
	
	private static boolean testByteArrays(byte[] expected, byte[] given) {
		if (expected.length != given.length) return false;
		for (int i = 0; i < expected.length; i++) if (expected[i] != given[i]) return false;
		return true;
	}
	
	
	@Test
	@Order(1)
	void testConvertStringKey() {
		
		byte[] hello_world = {'H', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd', '!' ,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,0,0, 0,0};
		byte[] the_quick = "The quick brown fox jumps over the lazy.".getBytes();
		
		assertTrue(testByteArrays(hello_world, HMACUtil.convertStringKey("JBSWY3DPFQQHO33SNRSCC")));
		assertTrue(testByteArrays(the_quick, HMACUtil.convertStringKey("KRUGKIDROVUWG2ZAMJZG653OEBTG66BANJ2W24DTEBXXMZLSEB2GQZJANRQXU6JO")));
	}
	
	@Test
	@Order(1)
	void testConcatByteArrays() {
		assertNull(HMACUtil.concatByteArrays(null, new byte[] {}));
		assertNull(HMACUtil.concatByteArrays(new byte[] {}, null));
		assertTrue(testByteArrays(new byte[] {}, HMACUtil.concatByteArrays(new byte[] {}, new byte[] {})));
		assertTrue(testByteArrays(new byte[] {1, 2, 3, 4, 5}, HMACUtil.concatByteArrays(new byte[] {}, new byte[] {1, 2, 3, 4, 5})));
		assertTrue(testByteArrays(new byte[] {1, 2, 3, 1, 2, 3, 4, 5}, HMACUtil.concatByteArrays(new byte[] {1, 2, 3}, new byte[] {1, 2, 3, 4, 5})));
	}
	
	@Test
	@Order(2)
	void testHOTP_SHA_1() {
		// https://en.wikipedia.org/wiki/Time-based_one-time_password#otpauth://_URI_scheme
		assertEquals(439256, HMACUtil.HOTP_SHA_1("JBSWY3DPFQQHO33SNRSCC", 42, 6));
		assertEquals(421334, HMACUtil.HOTP_SHA_1("JBSWY3DPFQQHO33SNRSCC", 59777074, 6));
		
		// https://totp.danhersam.com/
		assertEquals(757540, HMACUtil.HOTP_SHA_1("JBSWY3DPEHPK3PXP", 59777073, 6));
		assertEquals(802824, HMACUtil.HOTP_SHA_1("JBSWY3DPEHPK3PXP", 59777074, 6));
	}
}

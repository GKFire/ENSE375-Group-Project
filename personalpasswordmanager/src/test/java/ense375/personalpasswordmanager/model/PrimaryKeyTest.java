package ense375.personalpasswordmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class PrimaryKeyTest {
	
	PrimaryKey key_1 = PrimaryKey.derriveKey("password");
	PrimaryKey key_2 = PrimaryKey.derriveKey("other_password");
	PrimaryKey key_1_reentered = PrimaryKey.derriveKey("password");
	
	
	@Test
	void testDerriveNoKeyWithNoInput() {
		assertNull(PrimaryKey.derriveKey(null));
	}
	
	
	@Test
	void testPrimaryKey() {
		assertNull(key_1.encrypt(null));
		assertNull(key_1.decrypt(null));
		
		assertEquals("eD4uV8hOxDUKffyEoRHD6A==", key_1.encrypt("Hello, world!"));
		assertEquals("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI", key_1.encrypt("The quick brown fox jumps over the lazy."));
		
		assertNotEquals("JBSWY3DPFQQHO33SNRSCC", key_2.encrypt("Hello, world!"));
		assertNotEquals("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI", key_2.encrypt("The quick brown fox jumps over the lazy."));
		
		assertEquals("eD4uV8hOxDUKffyEoRHD6A==", key_1_reentered.encrypt("Hello, world!"));
		assertEquals("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI", key_1_reentered.encrypt("The quick brown fox jumps over the lazy."));
		
		assertEquals("Hello, world!", key_1.decrypt("eD4uV8hOxDUKffyEoRHD6A=="));
		assertEquals("The quick brown fox jumps over the lazy.", key_1.decrypt("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI"));
		
		assertNotEquals("Hello, world!", key_2.decrypt("eD4uV8hOxDUKffyEoRHD6A=="));
		assertNotEquals("The quick brown fox jumps over the lazy.", key_2.decrypt("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI"));
		
		assertEquals("Hello, world!", key_1_reentered.decrypt("eD4uV8hOxDUKffyEoRHD6A=="));
		assertEquals("The quick brown fox jumps over the lazy.", key_1_reentered.decrypt("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI"));
		
		
		assertEquals("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI", key_1.encrypt(key_1_reentered.decrypt("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI")));
		assertEquals("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI", key_1_reentered.encrypt(key_1.decrypt("FSitFEKpQ3fQIZ4YJQgCGW2PW/HRvhyOP7qK1VvEaQGKf9jlgZIhfAlNH25G2OqI")));
		
		assertEquals("Hello, world!", key_1.decrypt(key_1_reentered.encrypt("Hello, world!")));
		assertEquals("Hello, world!", key_1_reentered.decrypt(key_1.encrypt("Hello, world!")));
		
		assertNotEquals("Hello, world!", key_1.decrypt(key_2.encrypt("Hello, world!")));
	}
	
	
}

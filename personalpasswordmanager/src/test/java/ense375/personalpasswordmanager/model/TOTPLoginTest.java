package ense375.personalpasswordmanager.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TOTPLoginTest {
	
	TOTPLogin login = new TOTPLogin("username", "website", "secret_key", Arrays.asList("recovery_key_1", "recovery_key_2"));
	
	@Test
	void testEquivalence() {
		assertFalse(login.equals(null));
		assertFalse(login.equals(new Object()));
		assertTrue(login.equals(login));
		assertTrue(login.equals(new TOTPLogin("username", "website", "secret_key", Arrays.asList("recovery_key_1", "recovery_key_2"), login.getCreated(), login.getLastModified())));
		assertFalse(login.equals(new TOTPLogin("username", "website", "key", new ArrayList<String>(), login.getCreated(), login.getLastModified())));
		assertFalse(login.equals(new LoginEntry("username", "website") {}));
		assertTrue(login.equals((LoginEntry) new TOTPLogin("username", "website", "secret_key", Arrays.asList("recovery_key_1", "recovery_key_2"), login.getCreated(), login.getLastModified())));
		assertFalse(login.equals((LoginEntry) new TOTPLogin("username", "website", "other_secret_key", Arrays.asList("recovery_key_1", "recovery_key_2"), login.getCreated(), login.getLastModified())));
		assertFalse(login.equals((LoginEntry) new TOTPLogin("username", "website", "secret_key", Arrays.asList("recovery_key_1", "recovery_key_two"), login.getCreated(), login.getLastModified())));
		
		assertTrue(((LoginEntry) login).equals(new TOTPLogin("username", "website", "secret_key", Arrays.asList("recovery_key_1", "recovery_key_2"), login.getCreated(), login.getLastModified())));
		assertFalse(((LoginEntry) login).equals((LoginEntry) new TextLogin("username", "website", "secret_key", login.getCreated(), login.getLastModified())));
		assertFalse(((LoginEntry) login).equals((LoginEntry) new TOTPLogin("username", "website", "secret", new ArrayList<String>(), login.getCreated(), login.getLastModified())));
	}
}

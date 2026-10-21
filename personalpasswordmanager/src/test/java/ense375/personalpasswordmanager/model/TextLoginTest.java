package ense375.personalpasswordmanager.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TextLoginTest {
	
	TextLogin login = new TextLogin("username", "website", "password");
	
	@Test
	void testEquivalence() {
		assertTrue(login.equals(login));
		assertTrue(login.equals(new TextLogin("username", "website", "password", login.getCreated(), login.getLastModified())));
		assertFalse(login.equals(null));
		assertFalse(login.equals(new TOTPLogin("username", "website", "key", new ArrayList<String>(), login.getCreated(), login.getLastModified())));
		assertFalse(login.equals(new LoginEntry("username", "website") {}));
		assertTrue(login.equals((LoginEntry) new TextLogin("username", "website", "password", login.getCreated(), login.getLastModified())));
		assertFalse(login.equals((LoginEntry) new TextLogin("username", "website", "otherpassword", login.getCreated(), login.getLastModified())));
		
		assertTrue(((LoginEntry) login).equals(new TextLogin("username", "website", "password", login.getCreated(), login.getLastModified())));
		assertFalse(((LoginEntry) login).equals((LoginEntry) new TextLogin("username", "website", "otherpassword", login.getCreated(), login.getLastModified())));
		assertFalse(((LoginEntry) login).equals((LoginEntry) new TOTPLogin("username", "website", "secret", new ArrayList<String>(), login.getCreated(), login.getLastModified())));
	}
}

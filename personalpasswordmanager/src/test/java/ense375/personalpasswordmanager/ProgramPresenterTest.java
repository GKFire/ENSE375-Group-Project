package ense375.personalpasswordmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import ense375.personalpasswordmanager.model.DatabaseManager;
import ense375.personalpasswordmanager.model.LoginEntry;


@TestMethodOrder(OrderAnnotation.class)
public class ProgramPresenterTest {

	static Path data_file = Path.of(System.getProperty("user.dir"), "database_test.bin");
	
	private static class ProgramPresenterInstance extends ProgramPresenter {
		
		public ProgramPresenterInstance() {
			logins = new ArrayList<LoginEntry>();
			database = new DatabaseManager(data_file);
		}
	}
	
	static ProgramPresenterInstance presenter = new ProgramPresenterInstance();
	
	@BeforeAll
	static void setupTest() {
		if (data_file.toFile().exists()) data_file.toFile().delete();
	}
	
	@Test
	@Order(1)
	void testInitDatabase() {
		assertFalse(presenter.initDatabase(null));
		assertFalse(presenter.initDatabase(""));
		assertFalse(data_file.toFile().exists());
		assertTrue(presenter.initDatabase("mypassword"));
		assertTrue(data_file.toFile().exists());
		assertFalse(presenter.initDatabase("mypassword"));
		assertFalse(presenter.initDatabase("myotherpassword"));
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertFalse(presenter_other.initDatabase("myotherpassword"));
		assertTrue(presenter_other.initDatabase("mypassword"));
	}
	
	@Test
	@Order(1)
	void testValidations() {
		assertFalse(ProgramPresenter.validUsernameOrEmail(null));
		assertFalse(ProgramPresenter.validUsernameOrEmail(""));
		assertFalse(ProgramPresenter.validUsernameOrEmail("my-username"));
		assertTrue(ProgramPresenter.validUsernameOrEmail("myusername"));
		assertTrue(ProgramPresenter.validUsernameOrEmail("my_username"));
		assertTrue(ProgramPresenter.validUsernameOrEmail("my_username123"));
		
		assertFalse(ProgramPresenter.validUsernameOrEmail("email@"));
		assertFalse(ProgramPresenter.validUsernameOrEmail("@website"));
		assertFalse(ProgramPresenter.validUsernameOrEmail("email@website"));
		assertTrue(ProgramPresenter.validUsernameOrEmail("email.user@website.ca"));
		assertTrue(ProgramPresenter.validUsernameOrEmail("email-user@website.ca"));
		
		assertFalse(ProgramPresenter.validDomain("my_username123"));
		assertFalse(ProgramPresenter.validDomain("email@website"));
		assertFalse(ProgramPresenter.validDomain("email@website.ca"));
		assertFalse(ProgramPresenter.validDomain("website.c"));
		assertTrue(ProgramPresenter.validDomain("website.com"));
		assertTrue(ProgramPresenter.validDomain("website.ca"));
		
		assertFalse(ProgramPresenter.validPassword(null));
		assertFalse(ProgramPresenter.validPassword(""));
		assertFalse(ProgramPresenter.validPassword("short"));
		assertFalse(ProgramPresenter.validPassword("S%0rt"));
		assertFalse(ProgramPresenter.validPassword("lengthvalid"));
		assertFalse(ProgramPresenter.validPassword("lengthValid"));
		assertFalse(ProgramPresenter.validPassword("l3ngthValid"));
		assertTrue(ProgramPresenter.validPassword("l3ng$hValid"));
		
		
		assertFalse(ProgramPresenter.validBase64("not valid"));
		assertFalse(ProgramPresenter.validBase64("not_valid"));
		assertTrue(ProgramPresenter.validBase64("G"));
		assertTrue(ProgramPresenter.validBase64("EIGJROGIHIH30RH8FB0I04===="));
		assertTrue(ProgramPresenter.validBase64("EIGJRO/GIHIH30RH8F+B0I04"));
		assertFalse(ProgramPresenter.validBase64("EIGJRO/GIHIH30RH8F+B0I0=4"));
	}
	
	@Test
	@Order(2)
	void testCreateNewTextLogin() {
		assertEquals(-1, presenter.createNewTextLogin(null, "Password123$", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("username", null, "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("username", "Password123$", null));
		assertEquals(0, presenter.createNewTextLogin("username1", "Password123$", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("username", "Password123$", "5426"));
		assertEquals(1, presenter.createNewTextLogin("username@email.ca", "Password123$", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("username", "password", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("username", "Password", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("username", "Password123", "my.domain"));
		assertEquals(2, presenter.createNewTextLogin("username3", "Password123$", "my.domain"));
		
		assertEquals(-1, presenter.createNewTextLogin("email@", "Password123$", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("@email.com", "Password123$", "my.domain"));
		assertEquals(-1, presenter.createNewTextLogin("my%email@email.com", "Password123$", "my.domain"));
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertEquals(-1, presenter_other.createNewTextLogin("username3", "Password123$", "my.domain"));
		
	}
	
	
	@Test
	@Order(2)
	void testCreateNewTOTPLogin() {
		assertEquals(-1, presenter.createNewTOTPLogin("username", "Password123$", "my.domain", Arrays.asList("recoverykey1", "recoverykey2")));
		assertEquals(3, presenter.createNewTOTPLogin("email@email.com", "JBSWY3DPFQQHO33SNRSCC", "my.domain", Arrays.asList("recoverykey1", "recoverykey2")));
		assertEquals(-1, presenter.createNewTOTPLogin("username", "JBSWY3DPFQQHO33SNRSCC", "my.domain", null));
		assertEquals(-1, presenter.createNewTOTPLogin("username", "JBSWY3DPFQQHO33SNRSCC", "my.domain", Arrays.asList("invalid%recovery_key", "recoverykey2")));
		assertEquals(-1, presenter.createNewTOTPLogin("username", "JBSWY3DPFQQHO33SNRSCC", "my.domain", Arrays.asList("recoverykey1", "invalid%recovery_key")));
		assertEquals(4, presenter.createNewTOTPLogin("My.Email@Email.COM", "JBSWY3DPFQQHO33SNRSCC", "my.domain", Arrays.asList("recoverykey1")));
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertEquals(-1, presenter_other.createNewTOTPLogin("My.Email@Email.COM", "JBSWY3DPFQQHO33SNRSCC", "my.domain", Arrays.asList("recoverykey1")));
	}
	
	@Test
	@Order(3)
	void testGetOneTimePassword() {
		assertNull(presenter.getOneTimePassword(-1));
		assertNull(presenter.getOneTimePassword(0));
		assertNull(presenter.getOneTimePassword(1));
		assertNull(presenter.getOneTimePassword(2));
		assertNull(presenter.getOneTimePassword(10));
		assertTrue(Pattern.matches("\\d{3}-\\d{3}", presenter.getOneTimePassword(3)));
		assertTrue(Pattern.matches("\\d{3}-\\d{3}", presenter.getOneTimePassword(4)));
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertNull(presenter_other.getOneTimePassword(0));
	}
	
	@Test
	@Order(3)
	void testGetLogins() {
		assertEquals(Arrays.asList(
				"username: \"username1\"; domain: \"my.domain\"; type: \"Text Login\"",
				"username: \"username@email.ca\"; domain: \"my.domain\"; type: \"Text Login\"",
				"username: \"username3\"; domain: \"my.domain\"; type: \"Text Login\"",
				"username: \"email@email.com\"; domain: \"my.domain\"; type: \"TOTP Login\"",
				"username: \"my.email@email.com\"; domain: \"my.domain\"; type: \"TOTP Login\""
				), presenter.getLogins());
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertNull(presenter_other.getLogins());
	}
	
	@Test
	@Order(3)
	void testGetLogin() {
		assertNull(presenter.getLogin(-1));
		
		Map<String, String> map_0 = new HashMap<String, String>();
		map_0.put("username", "username1");
		map_0.put("domain", "my.domain");
		map_0.put("type", "text");
		map_0.put("password", "Password123$");
		
		assertTrue(matchesEntryMap(map_0, presenter.getLogin(0)));
		
		Map<String, String> map_2 = new HashMap<String, String>();
		map_2.put("username", "username3");
		map_2.put("domain", "my.domain");
		map_2.put("type", "text");
		map_2.put("password", "Password123$");
		
		assertTrue(matchesEntryMap(map_2, presenter.getLogin(2)));
		
		Map<String, String> map_3 = new HashMap<String, String>();
		map_3.put("username", "email@email.com");
		map_3.put("domain", "my.domain");
		map_3.put("type", "totp");
		map_3.put("totp", presenter.getOneTimePassword(3));
		map_3.put("recovery_key_0", "recoverykey1");
		map_3.put("recovery_key_1", "recoverykey2");
		
		assertTrue(matchesEntryMap(map_3, presenter.getLogin(3)));
		
		Map<String, String> map_4 = new HashMap<String, String>();
		map_4.put("username", "my.email@email.com");
		map_4.put("domain", "my.domain");
		map_4.put("type", "totp");
		map_4.put("totp", presenter.getOneTimePassword(4));
		map_4.put("recovery_key_0", "recoverykey1");
		
		assertTrue(matchesEntryMap(map_4, presenter.getLogin(4)));
		
		assertNull(presenter.getLogin(5));
		
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertNull(presenter_other.getOneTimePassword(0));
	}
	
	@Test
	@Order(3)
	void testSaveToDatabase() {
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertFalse(presenter_other.saveToDatabase());
		assertTrue(presenter.saveToDatabase());
	}
	
	@Test
	@Order(4)
	void testEditExistingLogin() {
		Map<String, String> changes = new HashMap<String, String>();
		changes.put("username", "new_username");
		assertFalse(presenter.editExistingLogin(-1, changes));
		assertFalse(presenter.editExistingLogin(5, changes));
		assertTrue(presenter.editExistingLogin(0, changes));
		assertEquals("new_username", presenter.getLogin(0).get("username"));
		
		changes.remove("username");
		changes.put("password", "my_New123_password$");
		assertTrue(presenter.editExistingLogin(2, changes));
		assertEquals("username3", presenter.getLogin(2).get("username"));
		assertEquals("my_New123_password$", presenter.getLogin(2).get("password"));
		
		changes.remove("password");
		changes.put("recovery_key_0", "new_recovery_key_0");
		changes.put("recovery_key_1", "new_recovery_key_1");
		changes.put("recovery_key_2", "new_recovery_key_2");
		
		assertTrue(presenter.editExistingLogin(3, changes));
		assertEquals("new_recovery_key_0", presenter.getLogin(3).get("recovery_key_0"));
		assertEquals("new_recovery_key_1", presenter.getLogin(3).get("recovery_key_1"));
		assertEquals("new_recovery_key_2", presenter.getLogin(3).get("recovery_key_2"));
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertFalse(presenter_other.editExistingLogin(3, changes));
	}
	
	@Test
	@Order(5)
	void testDeleteLogin() {
		assertFalse(presenter.deleteLogin(-1));
		assertFalse(presenter.deleteLogin(5));
		assertTrue(presenter.deleteLogin(1));
		assertFalse(presenter.deleteLogin(4));
		
		Map<String, String> map_1 = new HashMap<String, String>();
		map_1.put("username", "username3");
		map_1.put("domain", "my.domain");
		map_1.put("type", "text");
		map_1.put("password", "my_New123_password$");
		
		assertTrue(matchesEntryMap(map_1, presenter.getLogin(1)));
		
		
		
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertFalse(presenter_other.deleteLogin(3));
	}
	
	@Test
	@Order(6)
	void testReadDatabase() {
		ProgramPresenterInstance presenter_other = new ProgramPresenterInstance();
		assertFalse(presenter_other.readDatabase());
		assertFalse(presenter_other.initDatabase("myotherpassword"));
		assertTrue(presenter_other.initDatabase("mypassword"));
		assertTrue(presenter_other.readDatabase());
		
		Map<String, String> map_0 = new HashMap<String, String>();
		map_0.put("username", "new_username");
		map_0.put("domain", "my.domain");
		map_0.put("type", "text");
		map_0.put("password", "Password123$");
		
		assertTrue(matchesEntryMap(map_0, presenter_other.getLogin(0)));
		
		Map<String, String> map_1 = new HashMap<String, String>();
		map_1.put("username", "username3");
		map_1.put("domain", "my.domain");
		map_1.put("type", "text");
		map_1.put("password", "my_New123_password$");
		
		assertTrue(matchesEntryMap(map_1, presenter_other.getLogin(1)));
		
		Map<String, String> map_2 = new HashMap<String, String>();
		map_2.put("username", "email@email.com");
		map_2.put("domain", "my.domain");
		map_2.put("type", "totp");
		map_2.put("totp", presenter.getOneTimePassword(2));
		map_2.put("recovery_key_0", "new_recovery_key_0");
		map_2.put("recovery_key_1", "new_recovery_key_1");
		map_2.put("recovery_key_2", "new_recovery_key_2");
		
		assertTrue(matchesEntryMap(map_2, presenter_other.getLogin(2)));
		
		Map<String, String> map_3 = new HashMap<String, String>();
		map_3.put("username", "my.email@email.com");
		map_3.put("domain", "my.domain");
		map_3.put("type", "totp");
		map_3.put("totp", presenter.getOneTimePassword(3));
		map_3.put("recovery_key_0", "recoverykey1");
		
		assertTrue(matchesEntryMap(map_3, presenter_other.getLogin(3)));
		
	}
	
	private boolean matchesEntryMap(Map<String, String> expected, Map<String, String> test) {
		for (String key : expected.keySet()) if (!test.containsKey(key) || !test.get(key).equals(expected.get(key))) return false;
		return true;
	}
	
	
	
}

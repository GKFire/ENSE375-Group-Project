package ense375.personalpasswordmanager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
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
	
	private class ProgramPresenterInstance extends ProgramPresenter {
		
		public ProgramPresenterInstance() {
			logins = new ArrayList<LoginEntry>();
			database = new DatabaseManager(data_file);
		}
	}
	
	ProgramPresenterInstance presenter = new ProgramPresenterInstance();
	
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
	@Order(2)
	void testCreateNewTextLogin() {
		assertEquals(-1, presenter.createNewTextLogin(null, "domain.domain", "Password123$"));
		assertEquals(-1, presenter.createNewTextLogin("", "domain.domain", "Password123$"));
		assertEquals(0, presenter.createNewTextLogin("username", "domain.domain", "Password123$"));
		assertEquals(-1, presenter.createNewTextLogin("username", "5426", "Password123$"));
		assertEquals(1, presenter.createNewTextLogin("username", "my.domain", "Password123$"));
		assertEquals(-1, presenter.createNewTextLogin("username", "my.domain", "password"));
		assertEquals(-1, presenter.createNewTextLogin("username", "my.domain", "Password"));
		assertEquals(-1, presenter.createNewTextLogin("username", "my.domain", "Password123"));
		assertEquals(2, presenter.createNewTextLogin("username", "my.domain", "Password123$"));
	}
	
	
	@Test
	@Order(2)
	void testCreateNewTOTPLogin() {
		
	}
	
	@Test
	@Order(3)
	void testGetOneTimePassword() {
		
	}
	
	@Test
	@Order(3)
	void testGetLogins() {
		
	}
	
	@Test
	@Order(3)
	void testGetLogin() {
		
	}
	
	@Test
	@Order(4)
	void testEditExistingLogin() {
		
	}
	
	@Test
	@Order(4)
	void testDeleteLogin() {
		
	}
	
	@Test
	@Order(5)
	void testSaveToDatabase() {
		
	}
	
	@Test
	@Order(6)
	void testReadDatabase() {
		
	}
	
	@AfterAll
	static void cleanupTest() {
		if (data_file.toFile().exists()) data_file.toFile().delete();
	}
	
	
	
}

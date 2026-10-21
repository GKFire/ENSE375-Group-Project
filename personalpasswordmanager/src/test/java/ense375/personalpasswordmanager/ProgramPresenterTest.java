package ense375.personalpasswordmanager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@TestMethodOrder(OrderAnnotation.class)
public class ProgramPresenterTest {

	ProgramPresenter presenter = new ProgramPresenter();
	
	@BeforeAll
	static void setupTest() {
		
	}
	
	@Test
	@Order(1)
	void testInitDatabase() {
		
	}
	
	@Test
	@Order(2)
	void testCreateNewTextLogin() {
		
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
		
	}
	
	
	
}

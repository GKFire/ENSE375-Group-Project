package ense375.personalpasswordmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class DatabaseManagerTest {
	
	static Path date_file = Path.of(System.getProperty("user.dir"), "database_test.bin");
	
	DatabaseManager database = new DatabaseManager(date_file);
	
	static PrimaryKey valid_key = PrimaryKey.derriveKey("myvalidpassword");
	static PrimaryKey invalid_key = PrimaryKey.derriveKey("myinvalidpassword");
	
	static List<LoginEntry> entries = new ArrayList<LoginEntry>();
	
	@BeforeAll
	static void setupEntries() {
		
		File file = date_file.toFile();
		if (file.exists()) file.delete();
		
		entries.add(new TextLogin("myusername", "d0main.site.example", "thispasswordwouldbeencrypted"));
		entries.add(new TextLogin("otherusername", "other.site.example", "thisPasswordWouldAlsoBeEncrypted"));
		entries.add(new TOTPLogin("superusername", "domain.example", "mysupersecretkey", Arrays.asList("recoverkey1", "recoverykey2")));
		entries.add(new TOTPLogin("almightlyEmail", "domain.whereami", "myexposedsecretkey", Arrays.asList("recoverkey1")));
		entries.add(new TOTPLogin("uwu", "domain.site", "myOtherSuperSecretKey", new ArrayList<String>()));
	}
	
	@Test
	@Order(1)
	void testFormatEntryForWriting() {
		assertEquals(DatabaseManager.formatEntryForDatabaseWrite(entries.get(0)), String.format("{username:\"myusername\";domain:\"d0main.site.example\";password:\"thispasswordwouldbeencrypted\";created:%d;lastModified:%d}", entries.get(0).getCreated(), entries.get(0).getLastModified()));
		assertEquals(DatabaseManager.formatEntryForDatabaseWrite(entries.get(2)), String.format("{username:\"superusername\";domain:\"domain.example\";secret:\"mysupersecretkey\";recovery:[\"recoverkey1\",\"recoverykey2\"];created:%d;lastModified:%d}", entries.get(2).getCreated(), entries.get(2).getLastModified()));
		assertEquals(DatabaseManager.formatEntryForDatabaseWrite(entries.get(3)), String.format("{username:\"almightlyEmail\";domain:\"domain.whereami\";secret:\"myexposedsecretkey\";recovery:[\"recoverkey1\"];created:%d;lastModified:%d}", entries.get(3).getCreated(), entries.get(3).getLastModified()));
		assertEquals(DatabaseManager.formatEntryForDatabaseWrite(entries.get(4)), String.format("{username:\"uwu\";domain:\"domain.site\";secret:\"myOtherSuperSecretKey\";recovery:[];created:%d;lastModified:%d}", entries.get(4).getCreated(), entries.get(4).getLastModified()));
		assertNull(DatabaseManager.formatEntryForDatabaseWrite(null));
	}
	
	@Test
	@Order(2)
	void testExtractEntryFromDatabase() {
		assertNull(DatabaseManager.extractLoginEntryFromDatabaseEntry(null));
		assertEquals(DatabaseManager.extractLoginEntryFromDatabaseEntry(String.format("{username:\"myusername\";domain:\"d0main.site.example\";password:\"thispasswordwouldbeencrypted\";created:%d;lastModified:%d}", entries.get(0).getCreated(), entries.get(0).getLastModified())), entries.get(0));
		assertEquals(DatabaseManager.extractLoginEntryFromDatabaseEntry(String.format("{username:\"superusername\";domain:\"domain.example\";secret:\"mysupersecretkey\";recovery:[\"recoverkey1\",\"recoverykey2\"];created:%d;lastModified:%d}", entries.get(2).getCreated(), entries.get(2).getLastModified())), entries.get(2));
		assertEquals(DatabaseManager.extractLoginEntryFromDatabaseEntry(String.format("{username:\"almightlyEmail\";domain:\"domain.whereami\";secret:\"myexposedsecretkey\";recovery:[\"recoverkey1\"];created:%d;lastModified:%d}", entries.get(3).getCreated(), entries.get(3).getLastModified())), entries.get(3));
		assertEquals(DatabaseManager.extractLoginEntryFromDatabaseEntry(String.format("{username:\"uwu\";domain:\"domain.site\";secret:\"myOtherSuperSecretKey\";recovery:[];created:%d;lastModified:%d}", entries.get(4).getCreated(), entries.get(4).getLastModified())), entries.get(4));
	}
	
	
	@Test
	@Order(3)
	void testWriteDatabase() {
		assertFalse(database.writeDatabase(entries, null));
		assertTrue(database.writeDatabase(entries, valid_key));
		
	}
	
	@Test
	@Order(4)
	void testValidateKey() {
		assertFalse(database.validateKey(null));
		assertTrue(database.validateKey(valid_key));
		assertFalse(database.validateKey(invalid_key));
	}
	
	@Test
	@Order(5)
	void testReadDatabase() {
		assertNull(database.readDatabase(null));
		assertNull(database.readDatabase(invalid_key));
		List<LoginEntry> entries = database.readDatabase(valid_key);
		assertTrue(entries.equals(DatabaseManagerTest.entries));
	}

}

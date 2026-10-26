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
		
		entries.add(new TextLogin("myusername", "d0main.site.example", "Sm+K67fwgXuPEfykH1bAH7FFh3QWS2PUa47aGDoU0sM=", 0, 0));
		entries.add(new TOTPLogin("superusername", "domain.example", "MUnAlMN6vkMTNZSRph906mTb4WMCwjNCYid3Z6bn1K0=", Arrays.asList("qOb/fQ3tFCij/yFskM2WCw==", "RHMurqUClXNT0ChYB6XnQg=="), 0, 0));
		entries.add(new TOTPLogin("almightlyEmail", "domain.whereami", "QL1zYTwai4S6YOLh8HqSiUE7IWHZLAplOVegJyVQB5I=", Arrays.asList("qOb/fQ3tFCij/yFskM2WCw=="), 0, 0));
		entries.add(new TOTPLogin("uwu", "domain.site", "lJHGzdgWbIAR96yjCqUI0fjUhACaXo5yEtUBmjEsBkM=", new ArrayList<String>(), 0, 0));
	}
	
	@Test
	@Order(1)
	void testFormatEntryForWriting() {
		assertEquals("{username:\"myusername\";domain:\"d0main.site.example\";password:\"Sm+K67fwgXuPEfykH1bAH7FFh3QWS2PUa47aGDoU0sM=\";created:0;lastModified:0}", DatabaseManager.formatEntryForDatabaseWrite(entries.get(0)));
		assertEquals("{username:\"superusername\";domain:\"domain.example\";secret:\"MUnAlMN6vkMTNZSRph906mTb4WMCwjNCYid3Z6bn1K0=\";recovery:[\"qOb/fQ3tFCij/yFskM2WCw==\",\"RHMurqUClXNT0ChYB6XnQg==\"];created:0;lastModified:0}", DatabaseManager.formatEntryForDatabaseWrite(entries.get(1)));
		assertEquals("{username:\"almightlyEmail\";domain:\"domain.whereami\";secret:\"QL1zYTwai4S6YOLh8HqSiUE7IWHZLAplOVegJyVQB5I=\";recovery:[\"qOb/fQ3tFCij/yFskM2WCw==\"];created:0;lastModified:0}", DatabaseManager.formatEntryForDatabaseWrite(entries.get(2)));
		assertEquals("{username:\"uwu\";domain:\"domain.site\";secret:\"lJHGzdgWbIAR96yjCqUI0fjUhACaXo5yEtUBmjEsBkM=\";recovery:[];created:0;lastModified:0}", DatabaseManager.formatEntryForDatabaseWrite(entries.get(3)));
		assertNull(DatabaseManager.formatEntryForDatabaseWrite(null));
	}
	
	@Test
	@Order(1)
	void testExtractEntryFromDatabase() {
		assertNull(DatabaseManager.extractLoginEntryFromDatabaseEntry(null));
		assertEquals(entries.get(0), DatabaseManager.extractLoginEntryFromDatabaseEntry("{username:\"myusername\";domain:\"d0main.site.example\";password:\"Sm+K67fwgXuPEfykH1bAH7FFh3QWS2PUa47aGDoU0sM=\";created:0;lastModified:0}"));
		assertEquals(entries.get(1), DatabaseManager.extractLoginEntryFromDatabaseEntry("{username:\"superusername\";domain:\"domain.example\";secret:\"MUnAlMN6vkMTNZSRph906mTb4WMCwjNCYid3Z6bn1K0=\";recovery:[\"qOb/fQ3tFCij/yFskM2WCw==\",\"RHMurqUClXNT0ChYB6XnQg==\"];created:0;lastModified:0}"));
		assertEquals(entries.get(2), DatabaseManager.extractLoginEntryFromDatabaseEntry("{username:\"almightlyEmail\";domain:\"domain.whereami\";secret:\"QL1zYTwai4S6YOLh8HqSiUE7IWHZLAplOVegJyVQB5I=\";recovery:[\"qOb/fQ3tFCij/yFskM2WCw==\"];created:0;lastModified:0}"));
		assertEquals(entries.get(3), DatabaseManager.extractLoginEntryFromDatabaseEntry("{username:\"uwu\";domain:\"domain.site\";secret:\"lJHGzdgWbIAR96yjCqUI0fjUhACaXo5yEtUBmjEsBkM=\";recovery:[];created:0;lastModified:0}"));
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

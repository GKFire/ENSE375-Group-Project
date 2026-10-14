package ense375.personalpasswordmanager.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseManager {
	
	private File database;
	
	public static final String TEXT_ENTRY_REGEX = "\\{username:\\\"(?<username>\\w+)\\\";domain:\\\"(?<domain>[\\w.]+)\\\";password:\\\"(?<password>\\w+)\\\";created:(?<created>[0-9]+);lastModified:(?<modified>[0-9]+)\\}";
	public static final String TEXT_ENTRY_FORMAT = "{username:\"%s\";domain:\"%s\";password:\"%s\";created:%d;lastModified:%d}";
	
	public static final String TOTP_ENTRY_REGEX = "\\{username:\\\"(?<username>\\w+)\\\";domain:\\\"(?<domain>[\\w.]+)\\\";secret:\\\"(?<secret>\\w+)\\\";recovery:\\[(?<recovery>(?:\\\"\\w*\\\",)*(?:\\\"\\w*\\\"))?\\];created:(?<created>[0-9]+);lastModified:(?<modified>[0-9]+)\\}";
	public static final String TOTP_ENTRY_FORMAT = "{username:\"%s\";domain:\"%s\";secret:\"%s\";recovery:[%s];created:%d;lastModified:%d}";
	
	public DatabaseManager(Path datafile) {
		this.database = datafile.toAbsolutePath().toFile();
	}
	
	public boolean validateKey(PrimaryKey key) {
		if (key == null) return false;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(database));
			String entry = reader.readLine();
			reader.close();
			if (entry == null) return true;
			entry = key.decrypt(entry);
			if (entry == null) return false;
			if (Pattern.matches(TEXT_ENTRY_REGEX, entry) || Pattern.matches(TOTP_ENTRY_REGEX, entry)) return true;
			return false;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<LoginEntry> readDatabase(PrimaryKey key) {
		if (key == null) return null;
		ArrayList<String> database_buffer = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(database));
			String entry;
			while ((entry = reader.readLine()) != null) database_buffer.add(entry);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if (database_buffer.size() == 0) return new ArrayList<LoginEntry>();
		
		ArrayList<LoginEntry> entries = new ArrayList<LoginEntry>();
		
		for (String buffer : database_buffer) {
			LoginEntry entry = extractLoginEntryFromDatabaseEntry(key.decrypt(buffer));
			if (entry == null) return null;
			entries.add(entry);
		}
		
		return entries;
	}
	
	public boolean writeDatabase(List<LoginEntry> entries, PrimaryKey key) {
		if (key == null) return false;
		ArrayList<String> database_buffer = new ArrayList<String>();
		entries.forEach((entry) -> database_buffer.add(formatEntryForDatabaseWrite(entry)));
		String output_buffer = String.join("\n", database_buffer.stream().map((entry) -> key.encrypt(entry)).toList());
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(database, false));
			writer.write(output_buffer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static String formatEntryForDatabaseWrite(LoginEntry entry) {
		if (entry == null) return null;
		if (entry instanceof TOTPLogin) {
			TOTPLogin login = (TOTPLogin) entry;
			return String.format(TOTP_ENTRY_FORMAT, login.getUsername(), login.getWebsiteDomain(), login.getKey(), String.join(",", login.getRecoveryKeys().stream().map((recovery) -> '\"' + recovery + '\"').toList()), login.getCreated(), login.getLastModified());
		} else {
			TextLogin login = (TextLogin) entry;
			return String.format(TEXT_ENTRY_FORMAT, login.getUsername(), login.getWebsiteDomain(), login.getPassword(), login.getCreated(), login.getLastModified());
		}
	}
	
	public static LoginEntry extractLoginEntryFromDatabaseEntry(String buffer) {
		if (buffer == null) return null;
		if (Pattern.matches(TEXT_ENTRY_REGEX, buffer)) {
			Matcher text_login_matcher = Pattern.compile(TEXT_ENTRY_REGEX).matcher(buffer);
			text_login_matcher.find();
			
			return new TextLogin(text_login_matcher.group("username"), text_login_matcher.group("domain"), text_login_matcher.group("password"), Long.parseLong(text_login_matcher.group("created")), Long.parseLong(text_login_matcher.group("modified")));
			
		} else if (Pattern.matches(TOTP_ENTRY_REGEX, buffer)) {
			Matcher totp_login_matcher = Pattern.compile(TOTP_ENTRY_REGEX).matcher(buffer);
			totp_login_matcher.find();
			
			List<String> recovery_keys = (totp_login_matcher.group("recovery") != null ? Arrays.stream(totp_login_matcher.group("recovery").split(",")).map(recovery -> recovery.substring(1, recovery.length()-1)).toList() : new ArrayList<String>());
			return new TOTPLogin(totp_login_matcher.group("username"), totp_login_matcher.group("domain"), totp_login_matcher.group("secret"), recovery_keys, Long.parseLong(totp_login_matcher.group("created")), Long.parseLong(totp_login_matcher.group("modified")));
			
		} else return null;
		
	}
	
}

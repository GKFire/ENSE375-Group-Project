package ense375.personalpasswordmanager;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ense375.personalpasswordmanager.model.DatabaseManager;
import ense375.personalpasswordmanager.model.LoginEntry;
import ense375.personalpasswordmanager.model.PrimaryKey;
import ense375.personalpasswordmanager.model.TOTPLogin;
import ense375.personalpasswordmanager.model.TextLogin;
import ense375.personalpasswordmanager.utils.HMACUtil;

public class ProgramPresenter {
	
	public static final String USERNAME_REGEX = "[a-zA-Z0-9_]+";
	public static final String DOMAIN_REGEX = "[A-Za-z0-9]{2,}(?:\\.[A-Za-z0-9]{2,})+";
	public static final String EMAIL_REGEX = "[A-Za-z0-9._+\\-]+@".concat(DOMAIN_REGEX);
	public static final String BASE64_REGEX = "[0-9a-zA-Z+/]+=*";
	public static final String RECOVERY_KEY_REGEX = "[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*";
	
	
	
	public static final String USERNAME = "username";
	public static final String DOMAIN = "domain";
	public static final String PASSWORD = "password";
	public static final String SECRET = "secret_key";
	public static final String RECOVERY_KEY_PREFIX = "recovery_key_";
	public static final String CREATED = "created";
	public static final String LAST_MODIFIED = "last_modified";
	public static final String TYPE = "type";
	public static final String TOTP_TYPE = "totp";
	public static final String TEXT_TYPE = "text";
	
	
	
	protected List<LoginEntry> logins;
	protected DatabaseManager database;
	private PrimaryKey key;

	public ProgramPresenter() {
		logins = new ArrayList<LoginEntry>();
		
		Path config_dir = Path.of(System.getProperty("user.home"),
				(System.getProperty("os.name").startsWith("Windows") ?
						"Documents" : // If on windows, put into the user's documents directory
						".config" // Otherwise, the user is on UNIX, so put into their .config folder
				),
				"ENSE375",
				"PersonalManager",
				"database.bin");
		
		database = new DatabaseManager(config_dir);
		key = null;
	}
	
	private boolean databaseInitialized() {
		return key != null;
	}
	
	public boolean initDatabase(String user_password) {
		if (user_password == null) return false;
		if (user_password.equals("")) return false;
		if (key != null) return false;
		PrimaryKey candidate_key = PrimaryKey.derriveKey(user_password);
		if (!database.validateKey(candidate_key)) return false;
		this.key = candidate_key;
		return true;
	}
	
	public boolean saveToDatabase() {
		if (!databaseInitialized()) return false;
		return database.writeDatabase(logins, key);
	}
	
	public boolean readDatabase() {
		if (!databaseInitialized()) return false;
		List<LoginEntry> logins = database.readDatabase(key);
		if (logins == null) return false;
		this.logins = logins;
		return true;
	}
	
	public List<String> getLogins() {
		if (!databaseInitialized()) return null;
		List<String> list = new ArrayList<String>();
		logins.forEach(entry -> list.add(String.format("%s: \"%s\"; %s: \"%s\"; %s: \"%s\"", USERNAME, entry.getUsername(), DOMAIN, entry.getWebsiteDomain(), TYPE, (entry instanceof TOTPLogin ? "TOTP Login" : "Text Login"))));
		return list;
	}
	
	public Map<String, String> getLogin(int entry_id) {
		if (!databaseInitialized()) return null;
		if (entry_id < 0 || entry_id >= logins.size()) return null;
		
		HashMap<String, String> output = new HashMap<String, String>();
		LoginEntry entry = logins.get(entry_id);
		
		output.put(USERNAME, entry.getUsername());
		output.put(DOMAIN, entry.getWebsiteDomain());
		output.put(CREATED, entry.getFormattedCreated());
		output.put(LAST_MODIFIED, entry.getFormattedLastModified());
		
		if (entry instanceof TOTPLogin) {
			TOTPLogin totpentry = (TOTPLogin) entry;
			output.put(TYPE, TOTP_TYPE);
			output.put(TOTP_TYPE, getOneTimePassword(entry_id));
			List<String> recovery_keys = totpentry.getRecoveryKeys();
			for (int i = 0; i < recovery_keys.size(); i++)
				output.put(RECOVERY_KEY_PREFIX.concat(String.valueOf(i)), key.decrypt(recovery_keys.get(i)));
			
		} else {
			TextLogin textentry = (TextLogin) entry;
			output.put(TYPE, TEXT_TYPE);
			output.put(PASSWORD, key.decrypt(textentry.getPassword()));
		}
		
		return output;
	}
	
	public int createNewTextLogin(String username, String password, String website) {
		if (!databaseInitialized()) return -1;
		if (!validUsernameOrEmail(username)) return -1;
		if (!validDomain(website)) return -1;
		if (!validPassword(password)) return -1;
		if (logins.add(new TextLogin(username.toLowerCase(), website, key.encrypt(password)))) return logins.size() - 1;
		return -1;
	}
	
	public int createNewTOTPLogin(String username, String secret_key, String website, List<String> recovery_keys) {
		if (!databaseInitialized()) return -1;
		if (!validUsernameOrEmail(username)) return -1;
		if (!validDomain(website)) return -1;
		if (!validBase64(secret_key)) return -1;
		if (recovery_keys == null) return -1;
		for (String recovery_key : recovery_keys) if (!validRecoveryKey(recovery_key)) return -1;
		if (logins.add(new TOTPLogin(username.toLowerCase(), website, key.encrypt(secret_key), recovery_keys.stream().map(recovery_key -> key.encrypt(recovery_key)).toList()))) return logins.size() - 1;
		return -1;
	}
	
	public boolean deleteLogin(int entry_id) {
		if (!databaseInitialized()) return false;
		if (entry_id < 0 || entry_id >= logins.size()) return false;
		logins.remove(entry_id);
		return saveToDatabase();
	}
	
	public boolean editExistingLogin(int entry_id, Map<String, String> changes) {
		if (!databaseInitialized()) return false;
		if (entry_id < 0 || entry_id >= logins.size()) return false;
		
		LoginEntry entry = logins.get(entry_id);
		if (changes.containsKey(USERNAME)) entry.updateUsername(changes.get(USERNAME));
		if (entry instanceof TextLogin)
			if (changes.containsKey(PASSWORD)) ((TextLogin) entry).updatePassword(key.encrypt(changes.get(PASSWORD)));
		if (entry instanceof TOTPLogin) {
			List<String> recovery_keys = new ArrayList<String>();
			int i = 0;
			while (changes.containsKey(RECOVERY_KEY_PREFIX.concat(String.valueOf(i)))) {
				recovery_keys.add(key.encrypt(changes.get(RECOVERY_KEY_PREFIX.concat(String.valueOf(i++)))));
			}
			((TOTPLogin) entry).setRecoveryKeys(recovery_keys);
		}
		
		return saveToDatabase();
	}
	
	
	public String getOneTimePassword(int entry_id) {
		if (!databaseInitialized()) return null;
		if (entry_id < 0 || entry_id >= logins.size()) return null;
		
		LoginEntry entry = logins.get(entry_id);
		if (entry instanceof TOTPLogin) {
			TOTPLogin totp = (TOTPLogin) entry;
			
			int code = HMACUtil.HOTP_SHA_1(key.decrypt(totp.getKey()), Instant.now().getEpochSecond() / 30, 6);
			return String.format("%03d-%03d", code / 1000, code % 1000);
		} else return null;
	}
	
	public static boolean validPassword(String password) {
		if (password == null) return false;
		if (password.length() <= 7) return false;
		if (!Pattern.compile("[0-9]").matcher(password).find()) return false;
		if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;
		if (!Pattern.compile("[+#$%@!%^&*{}.,<>;:/()=]").matcher(password).find()) return false;
		return true;
	}
	
	public static boolean validDomain(String domain) {
		if (domain == null) return false;
		return Pattern.matches(DOMAIN_REGEX, domain);
	}
	
	public static boolean validUsernameOrEmail(String input) {
		if (input == null) return false;
		return Pattern.matches(USERNAME_REGEX, input) || Pattern.matches(EMAIL_REGEX, input);
	}
	
	public static boolean validBase64(String input) {
		if (input == null) return false;
		return Pattern.matches(BASE64_REGEX, input);
	}
	
	public static boolean validRecoveryKey(String input) {
		if (input == null) return false;
		return Pattern.matches(RECOVERY_KEY_REGEX, input);
	}
	
}

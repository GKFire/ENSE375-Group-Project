package ense375.personalpasswordmanager;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ense375.personalpasswordmanager.model.DatabaseManager;
import ense375.personalpasswordmanager.model.LoginEntry;
import ense375.personalpasswordmanager.model.PrimaryKey;
import ense375.personalpasswordmanager.model.TOTPLogin;
import ense375.personalpasswordmanager.model.TextLogin;
import ense375.personalpasswordmanager.utils.HMACUtil;

public class ProgramPresenter {
	
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
	
	
	
	private List<LoginEntry> logins;
	private DatabaseManager database;
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
		if (logins == null) return database.writeDatabase(new ArrayList<LoginEntry>(), key);
		return database.writeDatabase(logins, key);
	}
	
	public boolean readDatabase() {
		logins = database.readDatabase(key);
		if (logins == null) return false;
		return true;
	}
	
	public Map<String, String> getLogins() {
		if (logins == null) return null;
		TreeMap<String, String> map = new TreeMap<String, String>();
		logins.forEach(entry -> map.put(entry.getWebsiteDomain(), String.format("%s:\"%s\";%s:\"%s\"", USERNAME, entry.getUsername(), TYPE, (entry instanceof TOTPLogin ? "TOTP Login" : "Text Login"))));
		return map;
	}
	
	public Map<String, String> getLogin(int entry_id) {
		if (logins == null) return null;
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
	
	public Map<String, String> createNewTextLogin(String username, String password, String website) {
		logins.add(new TextLogin(username, website, key.encrypt(password)));
		return getLogin(logins.size() - 1);
	}
	
	public Map<String, String> createNewTOTPLogin(String username, String secret_key, String website, List<String> recovery_keys) {
		logins.add(new TOTPLogin(username, website, key.encrypt(secret_key), recovery_keys.stream().map(recovery_key -> key.encrypt(recovery_key)).toList()));
		return getLogin(logins.size() - 1);
	}
	
	public boolean deleteLogin(int entry_id) {
		if (entry_id < 0 || entry_id >= logins.size()) return false;
		logins.remove(entry_id);
		return saveToDatabase();
	}
	
	public boolean editExistingLogin(int entry_id, Map<String, String> changes) {
		if (entry_id < 0 || entry_id >= logins.size()) return false;
		
		LoginEntry entry = logins.get(entry_id);
		if (changes.containsKey(USERNAME)) entry.updateUsername(changes.get(USERNAME));
		if (changes.containsKey(PASSWORD)) ((TextLogin) entry).updatePassword(key.encrypt(changes.get(PASSWORD)));
		else {
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
		LoginEntry entry = logins.get(entry_id);
		if (entry instanceof TOTPLogin) {
			TOTPLogin totp = (TOTPLogin) entry;
			
			int code = HMACUtil.HOTP_SHA_1(key.decrypt(totp.getKey()), Instant.now().getEpochSecond() / 30, 6);
			return String.format("$03%d-$03%d", code / 1000, code % 1000);
		} else return null;
	}
	
}

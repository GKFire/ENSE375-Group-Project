package ense375.personalpasswordmanager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ense375.personalpasswordmanager.model.DatabaseManager;
import ense375.personalpasswordmanager.model.LoginEntry;
import ense375.personalpasswordmanager.model.PrimaryKey;
import ense375.personalpasswordmanager.model.TOTPLogin;
import ense375.personalpasswordmanager.utils.HMACUtil;

public class ProgramPresenter {
	
	public List<LoginEntry> logins;
	private DatabaseManager database;
	private PrimaryKey key;

	public ProgramPresenter() {
		logins = new ArrayList<LoginEntry>();
		database = new DatabaseManager();
		key = null;
	}
	
	public boolean initDatabase() {
		return false;
	}
	
	public boolean saveToDatabase() {
		return false;
	}
	
	public boolean readDatabase() {
		return false;
	}
	
	public Map<String, String> getLogins() {
		return null;
	}
	
	public Map<String, String> getLogin(int entry_id) {
		return null;
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

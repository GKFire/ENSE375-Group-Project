package ense375.personalpasswordmanager.model;

import java.util.ArrayList;
import java.util.List;

public class TOTPLogin extends LoginEntry {

	private String key;
	private List<String> recovery_keys;
	
	public TOTPLogin(String username, String website, String key, List<String> recovery_keys) {
		super(username, website);
		this.key = key;
		this.recovery_keys = new ArrayList<String>(recovery_keys);
	}
	
	public List<String> getRecoveryKeys() {
		return this.recovery_keys;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public boolean addRecoveryKey(String recovery_key) {
		this.recovery_keys.add(recovery_key);
		return true;
	}
	
	public boolean removeRecoveryKey(String recovery_key) {
		return this.recovery_keys.remove(recovery_key);
	}
}

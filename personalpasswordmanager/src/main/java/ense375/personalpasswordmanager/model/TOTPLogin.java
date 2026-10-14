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
	
	public TOTPLogin(String username, String website, String key, List<String> recovery_keys, long created, long lastModified) {
		this(username, website, key, recovery_keys);
		this.created.setTimeInMillis(created);
		this.lastModified.setTimeInMillis(lastModified);
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
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TOTPLogin)) return false;
		
		TOTPLogin o_entry = (TOTPLogin) o;
		if (!this.username.equals(o_entry.getUsername())) return false;
		if (!this.domain.equals(o_entry.getWebsiteDomain())) return false;
		if (!(this.created.getTimeInMillis() == o_entry.getCreated())) return false;
		if (!(this.lastModified.getTimeInMillis() == o_entry.getLastModified())) return false;
		if (!this.key.equals(o_entry.getKey())) return false;
		if (!this.recovery_keys.equals(o_entry.getRecoveryKeys())) return false;
		return true;
	}
}

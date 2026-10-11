package ense375.personalpasswordmanager.model;

public class TextLogin extends LoginEntry {

	private String password;
	
	public TextLogin(String username, String website, String password) {
		super(username, website);
		this.password = password;
	}
	
	public TextLogin(String username, String website, String password, long created, long lastModified) {
		this(username, website, password);
		this.created.setTimeInMillis(created);
		this.lastModified.setTimeInMillis(lastModified);
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public boolean setPassword(String password) {
		this.password = password;
		return true;
	}
}

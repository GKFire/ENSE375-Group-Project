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
	
	public boolean updatePassword(String password) {
		this.password = password;
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) return true;
		if (!(o instanceof TextLogin)) return false;
		
		TextLogin o_entry = (TextLogin) o;
		if (!this.username.equals(o_entry.getUsername())) return false;
		if (!this.domain.equals(o_entry.getWebsiteDomain())) return false;
		if (!(this.created.getTimeInMillis() == o_entry.getCreated())) return false;
		if (!(this.lastModified.getTimeInMillis() == o_entry.getLastModified())) return false;
		if (!this.password.equals(o_entry.getPassword())) return false;
		return true;
	}
}

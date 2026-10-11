package ense375.personalpasswordmanager.model;

import java.text.DateFormat;
import java.util.Calendar;

public abstract class LoginEntry {

	protected String username;
	protected String domain;
	protected Calendar lastModified;
	protected Calendar created;
	
	public LoginEntry(String username, String domain) {
		this.username = username;
		this.domain = domain;
		this.created = Calendar.getInstance();
		this.lastModified = (Calendar) this.created.clone();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void updateUsername(String username) {
		this.username = username;
	}
	
	public String getWebsiteDomain() {
		return this.domain;
	}
	
	protected void updateLastModified() {
		this.lastModified.setTimeInMillis(System.currentTimeMillis());
	}
	
	public String getLastModified() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(this.lastModified.getTime());
	}
	
	public String getCreated() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(this.created.getTime());
	}
	
}

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
	
	public long getLastModified() {
		return this.lastModified.getTimeInMillis();
	}
	
	public long getCreated() {
		return this.created.getTimeInMillis();
	}
	
	public String getFormattedLastModified() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(this.lastModified.getTime());
	}
	
	public String getFormattedCreated() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(this.created.getTime());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof LoginEntry)) return false;
		
		LoginEntry o_entry = (LoginEntry) o;
		if (!this.username.equals(o_entry.getUsername())) return false;
		if (!this.domain.equals(o_entry.getWebsiteDomain())) return false;
		if (!(this.created.getTimeInMillis() == o_entry.getCreated())) return false;
		if (!(this.lastModified.getTimeInMillis() == o_entry.getLastModified())) return false;
		return true;
	}
	
}

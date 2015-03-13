package hqlplus;

import java.util.Locale;

public class HqlParameter {
	private Locale locale;

	public HqlParameter(Locale locale){
		this.locale = locale;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}

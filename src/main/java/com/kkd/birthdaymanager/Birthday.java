package com.kkd.birthdaymanager;

import lombok.Data;

@Data
public class Birthday {
	private int year;
	private int month;
	private int day;

	@Override
	public String toString() {
		return "Birthday{" +
			"year='" + year + '\'' +
			", month='" + month + '\'' +
			", day='" + day + '\'' +
			'}';
	}
	
	public String getDateString() {
		return String.format("%d-%02d-%02d", year, month, day);
	}
	
	public String getDateTimeString() {
		// consider only KST for now.
		return getDateString() + "T00:00:00+09:00";
	}
}

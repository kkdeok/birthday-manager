package com.kkd.birthdaymanager;

import lombok.Data;

@Data
public class User {
	private String name;
	private Birthday birthday;
	private Boolean useLunar;
}

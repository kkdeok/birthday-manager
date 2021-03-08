package com.kkd.birthdaymanager;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class Config {
	private static final String CONFIG_FILE_PATH = "application.yaml";

	private List<User> userList = new ArrayList<>();
	private String lunarCalendarApiUrl = null;
	private String lunarCalendarApiToken = null;
	private String calendarId; 
	

	public Config() {
		Yaml yaml = new Yaml();
		InputStream inputStream = this.getClass()
			.getClassLoader()
			.getResourceAsStream(CONFIG_FILE_PATH);
		Map<String, Object> map = yaml.load(inputStream);
		
		setUserList((List<Map<String, String>>) map.get("target_users"));
		
		lunarCalendarApiUrl = String.valueOf(((Map) map.get("lunar_calendar_api")).get("url"));
		lunarCalendarApiToken = String.valueOf(((Map)map.get("lunar_calendar_api")).get("token"));
		calendarId = String.valueOf(map.get("calendar_id"));
	}
	
	private void setUserList(List<Map<String, String>> targetUserList) {
		for (Map<String, String> map : targetUserList) {
			User user = new User();
			user.setName(map.get("name"));
			
			Birthday birthday = new Birthday();
			birthday.setYear(Integer.parseInt(String.valueOf(map.get("birthday_year"))));
			birthday.setMonth(Integer.parseInt(String.valueOf(map.get("birthday_mon"))));
			birthday.setDay(Integer.parseInt(String.valueOf(map.get("birthday_day"))));
			
			user.setBirthday(birthday);
			user.setUseLunar(map.getOrDefault("is_lunar", "N").equals("Y"));
			userList.add(user);
		}
	}
}

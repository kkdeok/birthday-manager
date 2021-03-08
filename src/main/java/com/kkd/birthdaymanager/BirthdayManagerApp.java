package com.kkd.birthdaymanager;

import java.util.List;

public class BirthdayManagerApp {
	private Config config;
	private FutureBirthdayCalculator futureBirthdayCalculator;

	public BirthdayManagerApp() {
		this.config = new Config();
		this.futureBirthdayCalculator = new FutureBirthdayCalculator(
			config.getLunarCalendarApiUrl(), config.getLunarCalendarApiToken());
	}

	public void start() throws Exception {
		for (User user : config.getUserList()) {
			List<Birthday> birthdayList = futureBirthdayCalculator.calculate(user);

			System.out.println(user);
			for (Birthday birthday : birthdayList) {
				System.out.println(birthday);
			}
			CalendarManager.addToCalendar(user, birthdayList, config.getCalendarId());
		}
	}
	

	public static void main(String... args) throws Exception {
		BirthdayManagerApp birthdayManager = new BirthdayManagerApp();
		birthdayManager.start();
	}
}
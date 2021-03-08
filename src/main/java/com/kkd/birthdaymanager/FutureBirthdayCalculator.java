package com.kkd.birthdaymanager;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FutureBirthdayCalculator {
	private static final String TO_YEAR = "2050";
	private final String url;
	private final String token;

	public FutureBirthdayCalculator(String url, String token) {
		this.url = url;
		this.token = token;
	}

	/**
	 * calculate future birthday from current year to 2050.
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<Birthday> calculate(User user) throws Exception {
		if (user.getUseLunar()) {
			return calculateBasedOnLunarBirthday(user);
		} else {
			return calculateBasedOnSolrbirthday(user);
		}
	} 
	
	private List<Birthday> calculateBasedOnSolrbirthday(User user) {
		int fromYear = Calendar.getInstance().getWeekYear();
		int toYear = Integer.parseInt(TO_YEAR);
		
		List<Birthday> birthdayList = new ArrayList<>();
		for (int year = fromYear ; year <= toYear ; year++) {
			Birthday birthday = new Birthday();
			birthday.setYear(year);
			birthday.setMonth(user.getBirthday().getMonth());
			birthday.setDay(user.getBirthday().getDay());
			birthdayList.add(birthday);
		}
		return birthdayList;
	}
	
	private List<Birthday> calculateBasedOnLunarBirthday(User user) throws Exception {
		String fromYear = String.valueOf(Calendar.getInstance().getWeekYear());
		String month = String.format("%02d", user.getBirthday().getMonth());
		String day = String.format("%02d", user.getBirthday().getDay());
		
		StringBuilder urlBuilder = new StringBuilder(url)
			.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + token)
			.append("&" + URLEncoder.encode("fromSolYear", "UTF-8") + "=" + URLEncoder.encode(fromYear, "UTF-8"))
			.append("&" + URLEncoder.encode("toSolYear", "UTF-8") + "=" + URLEncoder.encode(TO_YEAR, "UTF-8"))
			.append("&" + URLEncoder.encode("lunMonth", "UTF-8") + "=" + URLEncoder.encode(month, "UTF-8"))
			.append("&" + URLEncoder.encode("lunDay", "UTF-8") + "=" + URLEncoder.encode(day, "UTF-8"))
			.append("&" + URLEncoder.encode("leapMonth", "UTF-8") + "=" + URLEncoder.encode("평", "UTF-8"))
			.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8"));

		// https://www.data.go.kr/data/15012679/openapi.do
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		InputStream is;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			is = conn.getInputStream();
		} else {
			is = conn.getErrorStream();
		}

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		BirthdayHandler birthdayHandler = new BirthdayHandler();
		parser.parse(is, birthdayHandler);

		conn.disconnect();
		return  birthdayHandler.getBirthdayList();
	}
}

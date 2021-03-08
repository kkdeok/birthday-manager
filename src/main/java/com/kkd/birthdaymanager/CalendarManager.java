package com.kkd.birthdaymanager;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class CalendarManager {
	private static final String APPLICATION_NAME = "BirthdayManagerApp";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	/**
	 * Global instance of the scopes required by this quickstart.
	 * If modifying these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Lists.newArrayList(CalendarScopes.all());
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	
	private static final int TWO_WEEKS = 24 * 60 * 7 * 2;
	
	private static final Event.Creator creator; 
	private static final Calendar service;
	
	static {
		creator = new Event.Creator();
		creator.setDisplayName("Kideok Kim");
		creator.setEmail("doubleknd26@gmail.com");

		try {
			// Build a new authorized API client service.
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					.setApplicationName(APPLICATION_NAME)
					.build();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * Creates an authorized Credential object.
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
		throws IOException {
		// Load client secrets.
		InputStream in = BirthdayManagerApp.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets =
			GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
			.setAccessType("offline")
			.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	// https://developers.google.com/calendar/create-events
	public static void addToCalendar(
		User user, List<Birthday> birthdayList, String calendarId) throws Exception {
		for (Birthday birthday : birthdayList) {
			Event event = new Event()
				.setCreator(creator)
				.setSummary(user.getName() + " \uD83C\uDF89")
				.setDescription(user.getName());
			
			DateTime dateTime = new DateTime(birthday.getDateString());
			EventDateTime start = new EventDateTime().setDate(dateTime);
			event.setStart(start);

			EventDateTime end = new EventDateTime().setDate(dateTime);
			event.setEnd(end);

			EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("popup").setMinutes(TWO_WEEKS),

			};

			Event.Reminders reminders = new Event.Reminders()
				.setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
			event.setReminders(reminders);

			event = service.events().insert(calendarId, event).execute();
			System.out.printf("Event created: %s\n", event.toString());
		}
	}
}

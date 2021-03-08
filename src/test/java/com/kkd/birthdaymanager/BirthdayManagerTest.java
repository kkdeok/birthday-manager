package com.kkd.birthdaymanager;

import junit.framework.TestCase;

public class BirthdayManagerTest extends TestCase {

	public void testStart() throws Exception {
		BirthdayManagerApp birthdayManager = new BirthdayManagerApp();
		birthdayManager.start();
	}
}
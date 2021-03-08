package com.kkd.birthdaymanager;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BirthdayHandler extends DefaultHandler {
	private List<Birthday> birthdayList = new ArrayList<>();
	private Birthday birthday = null;
	private String str = null;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException {
		
		if ("item".equals(qName)) {
			birthday = new Birthday();
			birthdayList.add(birthday);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("solDay".equals(qName)) {
			birthday.setDay(Integer.parseInt(str));
		} else if ("solMonth".equals(qName)) {
			birthday.setMonth(Integer.parseInt(str));
		} else if ("solYear".equals(qName)) {
			birthday.setYear(Integer.parseInt(str));
		} 
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		//태그와 태그 사이의 내용을 처리
		str = new String(ch,start,length);
	}
	
	public void init() {
		birthdayList = new ArrayList<>();
		birthday = null;
	}
}

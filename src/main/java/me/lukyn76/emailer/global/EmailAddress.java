package me.lukyn76.emailer.global;

import javax.mail.internet.InternetAddress;

@SuppressWarnings("serial")
public class EmailAddress extends InternetAddress {

	public EmailAddress(String address) {
		super.setAddress(address);
	}

}

package com.polishchuk;

public class ParseException extends RuntimeException {
	private String message;
	private Exception e;

	public ParseException(String string) {
		this.message = string;
	}

	public ParseException(String string, Exception e) {
		this.e = e;
		this.message = string;
	}

	public ParseException(Exception e) {
		this.e = e;
	}
	
}

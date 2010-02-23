package com.ecommerce.utils;

/**
 * typesafe enumeration class to handle the possible types of request acknowledgements
 * @author djdaugherty
 */
public class DasientRequest {

	private final String name;

	private DasientRequest(String name) { this.name = name; }

	@Override
	public String toString() { return name; }

	public static final DasientRequest REQUESTACK = new DasientRequest("REQUEST_ACK");
	public static final DasientRequest REQUESTIGNORED = new DasientRequest("REQUEST_IGNORED");
}

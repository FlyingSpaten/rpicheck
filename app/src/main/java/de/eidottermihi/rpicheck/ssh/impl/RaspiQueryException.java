package de.eidottermihi.rpicheck.ssh.impl;

import java.io.Serializable;

/**
 * Exception class for RaspiQuery.
 * 
 * @author Michael
 * @version 0.04
 * 
 */
public class RaspiQueryException extends Exception implements Serializable {
	/** Reasoncodes */
	public static final int REASON_CONNECTION_FAILED = 1;
	public static final int REASON_AUTHENTIFICATION_FAILED = 2;
	public static final int REASON_TRANSPORT_EXCEPTION = 3;
	public static final int REASON_VCGENCMD_NOT_FOUND = 6;
	public static final int REASON_IO_EXCEPTION = 7;

	private static final long serialVersionUID = -4385455511235194189L;
	private int reasonCode;

	/**
	 * Private constructor. Exceptions are generated by static method call.
	 * 
	 * @param reasonCode
	 *            a unique reason code
	 * @param message
	 *            a exception message
	 * @param cause
	 *            cause of exception
	 */
	private RaspiQueryException(final int reasonCode, final String message,
			final Throwable cause) {
		super(message, cause);
		this.setReasonCode(reasonCode);
	}

	private RaspiQueryException(int reasonCode, final String message) {
		super(message);
		this.setReasonCode(reasonCode);
	}

	/**
	 * Creates a exception with message
	 * "Could not establish a connection to host '%s'. Check your connection and settings."
	 * 
	 * @param host
	 *            the hostname
	 * @param port
	 *            the port
	 * @param cause
	 *            cause
	 * @return the created exception
	 */
	public static RaspiQueryException createConnectionFailure(
			final String host, final int port, final Throwable cause) {
		return new RaspiQueryException(
				REASON_CONNECTION_FAILED,
				String.format(
						"Could not establish a connection to host '%s:%d'. Check your connection and settings.",
						host, port), cause);
	}

	/**
	 * Creates a exception with message
	 * "Could not authenticate on host '%s' and login '%s'. Check your login/password."
	 * 
	 * @param hostname
	 *            the hostname
	 * @param username
	 *            the username/login
	 * @param cause
	 *            cause
	 * @return the created exception
	 */
	public static RaspiQueryException createAuthenticationFailure(
			final String hostname, final String username, final Throwable cause) {
		return new RaspiQueryException(
				REASON_AUTHENTIFICATION_FAILED,
				String.format(
						"Could not authenticate on host '%s' and login '%s'. Check your login/password.",
						hostname, username), cause);
	}

	/**
	 * Create a exception with message
	 * "A transport exception occured. Check your connection."
	 * 
	 * @param hostname
	 *            the hostname
	 * @param cause
	 *            cause
	 * @return the created exception
	 */
	public static RaspiQueryException createTransportFailure(
			final String hostname, final Throwable cause) {
		return createTransportFailure(cause);
	}

	public int getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}

	public static RaspiQueryException createVcgencmdNotFound() {
		return new RaspiQueryException(REASON_VCGENCMD_NOT_FOUND,
				"vcgencmd was not found on the device.");
	}

	/**
	 * Create a exception with message "An unexpected IO exception occured."
	 * 
	 * @param cause
	 *            the cause
	 * @return the created exception
	 */
	public static RaspiQueryException createIOException(Throwable cause) {
		return new RaspiQueryException(REASON_IO_EXCEPTION,
				"An unexpected IO exception occured.", cause);
	}

	/**
	 * Create a exception with message
	 * "A transport exception occured. Check your connection."
	 * 
	 * @param cause
	 *            cause
	 * @return the created exception
	 */
	public static RaspiQueryException createTransportFailure(Throwable cause) {
		return new RaspiQueryException(REASON_TRANSPORT_EXCEPTION,
				"A transport exception occured. Check your connection.", cause);
	}
}

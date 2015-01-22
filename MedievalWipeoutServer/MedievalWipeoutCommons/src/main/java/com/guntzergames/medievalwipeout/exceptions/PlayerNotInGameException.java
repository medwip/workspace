package com.guntzergames.medievalwipeout.exceptions;

public class PlayerNotInGameException extends GameException {

	private static final long serialVersionUID = -4152882521812480846L;

	public PlayerNotInGameException() {
		super();
	}

	public PlayerNotInGameException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlayerNotInGameException(String message) {
		super(message);
	}

	public PlayerNotInGameException(Throwable cause) {
		super(cause);
	}

}

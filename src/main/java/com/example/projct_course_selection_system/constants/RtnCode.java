package com.example.projct_course_selection_system.constants;

public enum RtnCode {

	SUCCESS ("200", "Success!"),
	SUCCESSFUL ("204", "Successful!"),
	CANNOT_EMPTY("400","Input is empty!"),
	INCORRECT("401"," Incorrect requests!"),
	NOT_FOUND("404","Not found!"),
	ALREADY_EXISTED("409","Input has already existed!"),
	BEEN_SELECTED("409","Course has been selected!"),
	FULLY_SELECTED("409","Course selection is full!"),
	PATTERNISNOTMATCH("422", "Pattern is not match!");
	
	private String code;
	private String message;
	
	private RtnCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}

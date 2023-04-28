package com.example.projct_course_selection_system.vo;

import java.time.LocalTime;

public class StudentCourseTable {

	// 共同屬性
	private String courseNumber;

	// Course屬性
	private String courseTitle;
	private String schedule;
	private LocalTime startTime;
	private LocalTime endTime;
	private int credits;
	private int personlimit;

	// Student屬性
	private String studentID;
	private String name;
	private int creditsLimit;

	public StudentCourseTable() {
		super();
	}

	public StudentCourseTable(String courseNumber) {
		super();
		this.courseNumber = courseNumber;
	}

	public StudentCourseTable(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits, int personlimit, String studentID, String name, int creditsLimit) {
		super();
		this.courseNumber = courseNumber;
		this.courseTitle = courseTitle;
		this.schedule = schedule;
		this.startTime = startTime;
		this.endTime = endTime;
		this.credits = credits;
		this.personlimit = personlimit;
		this.studentID = studentID;
		this.name = name;
		this.creditsLimit = creditsLimit;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public int getPersonlimit() {
		return personlimit;
	}

	public void setPersonlimit(int personlimit) {
		this.personlimit = personlimit;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCreditsLimit() {
		return creditsLimit;
	}

	public void setCreditsLimit(int creditsLimit) {
		this.creditsLimit = creditsLimit;
	}
	
}

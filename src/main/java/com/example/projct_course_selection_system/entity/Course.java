package com.example.projct_course_selection_system.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "course")
@JsonInclude(JsonInclude.Include.NON_NULL) // 不顯示值為null的key 和 value
public class Course {

	@Id
	@Column(name = "course_number")
	private String courseNumber; // 課程編碼

	@Column(name = "course_title")
	private String courseTitle; // 課程名稱

	@Column(name = "schedule")
	private String schedule; // 上課星期

	@Column(name = "start_time")
	private LocalTime startTime; // 上課開始時間

	@Column(name = "end_time")
	private LocalTime endTime; // 上課結束時間
	
	@Column(name = "credits")
	private int credits; // 學分
	
	@Column(name = "person_limit")
	private int personLimit = 3; // 修習人數上限

	public Course() {
		super();
	}

	public Course(String courseNumber, String courseTitle, String schedule, LocalTime startTime, LocalTime endTime,
			int credits) {
		super();
		this.courseNumber = courseNumber;
		this.courseTitle = courseTitle;
		this.schedule = schedule;
		this.startTime = startTime;
		this.endTime = endTime;
		this.credits = credits;
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

	public int getPersonLimit() {
		return personLimit;
	}

	public void setPersonLimit(int personLimit) {
		this.personLimit = personLimit;
	}
		
}

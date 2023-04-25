package com.example.projct_course_selection_system.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "student")
@JsonInclude(JsonInclude.Include.NON_NULL) // 不顯示值為null的key 和 value
public class Student {

	@Id
	@Column(name = "student_ID")
	private String studentID; // 學號

	@Column(name = "name")
	private String name; // 學生姓名

	@Column(name = "course_number")
	private String courseNumber; // 課程代碼
	
	@Column(name = "credits_limit")
	private int creditsLimit = 10; // 修習學分上限

	public Student() {
		super();
	}

	public Student(String studentID, String name) {
		super();
		this.studentID = studentID;
		this.name = name;
	}

	public Student(String studentID, String name, String courseNumber) {
		super();
		this.studentID = studentID;
		this.name = name;
		this.courseNumber = courseNumber;
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

	public String getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}

	public int getCreditsLimit() {
		return creditsLimit;
	}

	public void setCreditsLimit(int creditsLimit) {
		this.creditsLimit = creditsLimit;
	}
	
}

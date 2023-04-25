package com.example.projct_course_selection_system.vo;

import java.time.LocalTime;
import java.util.List;

import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {

	private Course course;
	
	// 課程
	@JsonProperty("course_number")
	private String courseNumber;
	
	@JsonProperty("course_title")
	private String courseTitle;
	
	private String schedule;
	
	@JsonProperty("start_time")
	private LocalTime startTime; 
	
	@JsonProperty("end_time")
	private LocalTime endTime;
	
	private int credits; 
	
	@JsonProperty("course_list")
	private List<String> courseList;
	
	// 學員
	private Student student;
	
	@JsonProperty("course_ID")
	private String studentID;
	
	@JsonProperty("course_name")
	private String studentName;
	
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
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
	public List<String> getCourseList() {
		return courseList;
	}
	public void setCourseList(List<String> courseList) {
		this.courseList = courseList;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public String getStudentID() {
		return studentID;
	}
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
}

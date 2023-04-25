package com.example.projct_course_selection_system.vo;

import java.util.List;

import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;

public class Response {

	private String message;
	private Course course;
	private Student student;
	private List<Course> courseList;

	public Response() {
		super();
	}

	public Response(String message) {
		super();
		this.message = message;
	}

	public Response(Course course, String message) {
		super();
		this.course = course;
		this.message = message;
	}

	public Response(Student student, String message) {
		super();
		this.student = student;
		this.message = message;
	}

	public Response(List<Course> courseList, String message) {
		super();
		this.courseList = courseList;
		this.message = message;
	}

	public Response(Student student, List<Course> courseList, String message) {
		super();
		this.student = student;
		this.courseList = courseList;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public List<Course> getCourseList() {
		return courseList;
	}

	public void setCourseList(List<Course> courseList) {
		this.courseList = courseList;
	}

}

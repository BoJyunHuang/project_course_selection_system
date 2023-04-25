package com.example.projct_course_selection_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;

@RestController
public class Controller {

	@Autowired
	private CourseSelection courseSelection;

	@PostMapping(value = "add_course")
	public Response addCourse(@RequestBody Request request) {
		return courseSelection.addCourse(request.getCourseNumber(), request.getCourseTitle(), request.getSchedule(),
				request.getStartTime(), request.getEndTime(), request.getCredits());
	}

	@GetMapping(value = "revise_course_by_title")
	public Response reviseCourseByTitle(@RequestBody Request request) {
		return courseSelection.reviseCourseByTitle(request.getCourseNumber(), request.getCourseTitle());
	}

	@GetMapping(value = "revise_course_by_schedule")
	public Response reviseCourseBySchedule(@RequestBody Request request) {
		return courseSelection.reviseCourseBySchedule(request.getCourseNumber(), request.getSchedule(),
				request.getStartTime(), request.getEndTime());
	}

	@GetMapping(value = "revise_course_by_all")
	public Response reviseCourseByAll(@RequestBody Request request) {
		return courseSelection.reviseCourseByAll(request.getCourseNumber(), request.getCourseTitle(),
				request.getSchedule(), request.getStartTime(), request.getEndTime(), request.getCredits());
	}
	
	@DeleteMapping(value = "delete_course")
	public Response deleteCourse(@RequestBody Request request) {
		return courseSelection.deleteCourse(request.getCourseNumber());
	}

	@PostMapping(value = "add_student")
	public Response addStudent(@RequestBody Request request) {
		return courseSelection.addStudent(request.getStudentID(), request.getStudentName());
	}
	
	@GetMapping(value = "select_course")
	public Response selectCourse(@RequestBody Request request) {
		return courseSelection.selectCourse(request.getStudentID(), request.getCourseList());
	}
	
	@GetMapping(value = "withdraw_course")
	public Response withdrawCourse(@RequestBody Request request) {
		return courseSelection.withdrawCourse(request.getStudentID(), request.getCourseNumber());
	}
	
	@DeleteMapping(value = "delete_student")
	public Response deleteStudent(@RequestBody Request request) {
		return courseSelection.deleteStudent(request.getStudentID());
	}
	
	@GetMapping(value = "course_schedule")
	public Response courseSchedule(@RequestBody Request request) {
		return courseSelection.courseSchedule(request.getStudentID());
	}
	
	@GetMapping(value = "find_course_info_by_number")
	public Response findCourseInfoByNumber(@RequestBody Request request) {
		return courseSelection.findCourseInfoByNumber(request.getCourseNumber());
	}
	
	@GetMapping(value = "find_course_info_by_title")
	public Response findCourseInfoByTitle(@RequestBody Request request) {
		return courseSelection.findCourseInfoByTitle(request.getCourseTitle());
	}
	
	
}

package com.example.projct_course_selection_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.service.ifs.CourseService;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;

@RestController
public class Controller {

	@Autowired
	private CourseService courseService;
	@Autowired
	private StudentService studentService;
	@Autowired
	private CourseSelection courseSelection;

	@PostMapping(value = "add_course")
	public Response addCourse(@RequestBody Request request) {
		return courseService.addCourse(request.getCourseNumber(), request.getCourseTitle(), request.getSchedule(),
				request.getStartTime(), request.getEndTime(), request.getCredits());
	}
	
	@GetMapping(value = "revise_course")
	public Response reviseCourse(@RequestBody Request request) {
		return courseService.reviseCourse(request);
	}
	
	@DeleteMapping(value = "delete_course")
	public Response deleteCourse(@RequestBody Request request) {
		return courseService.deleteCourse(request.getCourseNumber());
	}

	@GetMapping(value = "find_course_info")
	public Response findCourseInfoByNumber(@RequestBody Request request) {
		return courseService.findCourseInfo(request);
	}

	@PostMapping(value = "add_student")
	public Response addStudent(@RequestBody Request request) {
		return studentService.addStudent(request.getStudentID(), request.getStudentName());
	}
	
	@GetMapping(value = "delete_student")
	public Response deleteStudent(@RequestBody Request request) {
		return studentService.deleteStudent(request.getStudentID());
	}
		
	@GetMapping(value = "select_course")
	public Response selectCourse(@RequestBody Request request) {
		return courseSelection.selectCourse(request.getStudentID(), request.getCourseList());
	}
	
	@GetMapping(value = "drop_course")
	public Response dropCourse(@RequestBody Request request) {
		return courseSelection.dropCourse(request.getStudentID(), request.getCourseNumber());
	}
	
	@GetMapping(value = "course_schedule")
	public Response courseSchedule(@RequestBody Request request) {
		return courseSelection.courseSchedule(request.getStudentID());
	}
	
}

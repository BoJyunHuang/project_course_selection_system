package com.example.projct_course_selection_system.service.ifs;

import java.time.LocalTime;
import java.util.List;

import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.vo.Response;

public interface CourseSelection {

	// 1.選課
	public Response selectCourse(String studentID, List<String> courseList);

	// 2.退選
	public Response withdrawCourse(String studentID, String courseNumber);

}

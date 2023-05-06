package com.example.projct_course_selection_system.service.ifs;

import java.util.List;
import com.example.projct_course_selection_system.vo.Response;

public interface CourseSelection {

	// 1.選課(可選多堂)
	public Response selectCourse(String studentID, List<String> courseList);

	// 2.退選
	public Response dropCourse(String studentID, String courseNumber);

	// 3.查詢學生的所有課程
	public Response courseSchedule(String studentID);

}

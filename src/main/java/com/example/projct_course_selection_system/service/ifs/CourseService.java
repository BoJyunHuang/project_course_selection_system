package com.example.projct_course_selection_system.service.ifs;

import java.time.LocalTime;

import com.example.projct_course_selection_system.vo.Response;

public interface CourseService {

	// 1.新增課程
	public Response addCourse(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits);

	// 2.修改課程名稱
	public Response reviseCourseTitle(String courseNumber, String courseTitle);

	// 3.修改課程時間
	public Response reviseCourseSchedule(String courseNumber, String schedule, LocalTime startTime,
			LocalTime endTime);

	// 4.修改整個課程
	public Response reviseCourseAll(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits);

	// 5.刪除課程
	public Response deleteCourse(String courseNumber);
}

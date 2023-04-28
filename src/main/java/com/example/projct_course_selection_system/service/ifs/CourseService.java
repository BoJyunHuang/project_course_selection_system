package com.example.projct_course_selection_system.service.ifs;

import java.time.LocalTime;

import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;

public interface CourseService {

	// 1.新增課程(課號、課名、上課日期、時間、學分數)
	public Response addCourse(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits);

	// 2.修改課程資訊
	public Response reviseCourse(Request request);

	// 3.刪除課程 (必須無人選修)
	public Response deleteCourse(String courseNumber);
}

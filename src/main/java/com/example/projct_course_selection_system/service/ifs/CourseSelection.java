package com.example.projct_course_selection_system.service.ifs;

import java.time.LocalTime;
import java.util.List;

import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.vo.Response;

public interface CourseSelection {

	// 1.新增課程
	public Response addCourse(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits);

	// 1-1.修改課程名稱
	public Response reviseCourseByTitle(String courseNumber, String courseTitle);

	// 1-2.修改課程時間
	public Response reviseCourseBySchedule(String courseNumber, String schedule, LocalTime startTime,
			LocalTime endTime);

	// 1-3.修改整個課程
	public Response reviseCourseByAll(String courseNumber, String courseTitle, String schedule,
			LocalTime startTime, LocalTime endTime, int credits);

	// 1-4.刪除課程
	public Response deleteCourse(String courseNumber);

	// 2.新增學員
	public Response addStudent(String studentID, String studentName);

	// 2-1.選課
	public Response selectCourse(String studentID, List<Course> courseList);

	// 2-2.退選
	public Response withdrawCourse(String studentID, String courseNumber);

	// 2-3.退學
	public Response deleteStudent(String studentID);

	// 3.學生課程表
	public Response courseSchedule(String studentID);

	// 4-1.搜尋課程(課程編號)
	public Response findCourseInfoByNumber(String courseNumber);

	// 4-2.搜尋課程(課程名稱)
	public Response findCourseInfoByTitle(String courseTitle);

}

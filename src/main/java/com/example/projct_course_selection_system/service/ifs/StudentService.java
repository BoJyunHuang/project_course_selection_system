package com.example.projct_course_selection_system.service.ifs;

import java.util.List;

import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.vo.Response;

public interface StudentService {

	// 1.新增學員
	public Response addStudent(String studentID, String studentName);

	// 2.退學
	public Response deleteStudent(String studentID);

	// 3.學生課程表
	public Response courseSchedule(String studentID);

	// 4.搜尋課程(課程編號)
	public Response findCourseInfoByNumber(String courseNumber);

	// 5.搜尋課程(課程名稱)
	public Response findCourseInfoByTitle(String courseTitle);
}

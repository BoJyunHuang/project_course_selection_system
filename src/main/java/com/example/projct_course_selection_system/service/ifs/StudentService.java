package com.example.projct_course_selection_system.service.ifs;

import java.util.List;

import com.example.projct_course_selection_system.vo.Response;

public interface StudentService {

	// 1.新增學員(學號+姓名)
	public Response addStudent(String studentID, String studentName);

	// 2.刪除學員 (必須先全部退選)
	public Response deleteStudent(String studentID);

	// 3.選課(可選多堂)
	public Response selectCourse(String studentID, List<String> courseList);

	// 4.退選
	public Response dropCourse(String studentID, String courseNumber);

	// 5.查詢學生的所有課程
	public Response courseSchedule(String studentID);

}

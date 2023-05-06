package com.example.projct_course_selection_system.service.ifs;

import com.example.projct_course_selection_system.vo.Response;

public interface StudentService {

	// 1.新增學員(學號+姓名)
	public Response addStudent(String studentID, String studentName);

	// 2.刪除學員 (必須先全部退選)
	public Response deleteStudent(String studentID);
	
}

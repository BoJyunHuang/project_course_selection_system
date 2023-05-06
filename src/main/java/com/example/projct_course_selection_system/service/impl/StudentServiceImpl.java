package com.example.projct_course_selection_system.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.projct_course_selection_system.constants.RtnCode;
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Response;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private CourseSelection courseSelection;

	@Override
	public Response addStudent(String studentID, String studentName) {
		// 0.防呆:輸入參數空、白
		if (!StringUtils.hasText(studentID) || !StringUtils.hasText(studentName)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 1.新增學生，並檢查有無重複
		return studentDao.insertStudent(studentID, studentName) == 0
				? new Response(RtnCode.ALREADY_EXISTED.getMessage())
				: new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response deleteStudent(String studentID) {
		// 0.防呆:輸入參數空、白
		if (!StringUtils.hasText(studentID)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 1.確認學員是否存在
		Optional<Student> res = studentDao.findById(studentID);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 2.確認有無課程，有則進行退選
		if (StringUtils.hasText(res.get().getCourseNumbers())) {
			String[] listOfSelectedCourse = res.get().getCourseNumbers().split(", ");
			for (String c : listOfSelectedCourse) {
				if (!courseSelection.dropCourse(studentID, c).getMessage()
						.equals(RtnCode.SUCCESSFUL.getMessage())) {
					return courseSelection.dropCourse(studentID, c);
				}
			}
		}
		// 3.刪除學生
		studentDao.deleteById(studentID);
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}
}

package com.example.projct_course_selection_system.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.projct_course_selection_system.constants.RtnCode;
import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.repository.CourseDao;
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Response;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentDao studentDao;

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private CourseSelection courseSelection;

	@Override
	public Response addStudent(String studentID, String studentName) {
		// 防空、白
		if (!StringUtils.hasText(studentID) || !StringUtils.hasText(studentName)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 重複資料確認
		Optional<Student> res = studentDao.findById(studentID);
		if (res.isPresent()) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 新增資料
		Student student = new Student(studentID, studentName);
		return new Response(studentDao.save(student), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response deleteStudent(String studentID) {
		// 防空、白
		if (!StringUtils.hasText(studentID)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢學生
		Optional<Student> res = studentDao.findById(studentID);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 確認有無課程
			if (StringUtils.hasText(res.get().getCourseNumber())) {
				String tempStr1 = res.get().getCourseNumber();
				String tempStr2 = tempStr1.substring(0, tempStr1.length() - 1);
				String[] listOfSelectedCourse = tempStr2.split(", ");
				// 退選
				if (listOfSelectedCourse.length != 0) {
					for (String c : listOfSelectedCourse) {
						courseSelection.withdrawCourse(studentID, c);
					}
				}
			}
		// 刪除學生
		studentDao.deleteById(studentID);
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response courseSchedule(String studentID) {
		// 防空、白
		if (!StringUtils.hasText(studentID)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢學生
		Optional<Student> res = studentDao.findById(studentID);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 取得學生課表(有可能空白)
		if (!StringUtils.hasText(res.get().getCourseNumber())){
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		String selectedStr = res.get().getCourseNumber();
		List<Course> allCourse = courseDao.findAll();
		List<Course> selectedCourses = null;
		for (Course c : allCourse) {
			if (selectedStr.contains(c.getCourseNumber())) {
				selectedCourses.add(c);
			}
		}
		return new Response(res.get(), selectedCourses, RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response findCourseInfoByNumber(String courseNumber) {
		// 防空、
		if (!StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 尋找資料
		Optional<Course> res = courseDao.findById(courseNumber);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 印出資料
		return new Response(res.get(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response findCourseInfoByTitle(String courseTitle) {
		// 防空、
		if (!StringUtils.hasText(courseTitle)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 尋找資料
		List<Course> res = courseDao.findByCourseTitle(courseTitle);
		if (CollectionUtils.isEmpty(res)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 印出資料
		return new Response(res, RtnCode.SUCCESS.getMessage());
	}

}

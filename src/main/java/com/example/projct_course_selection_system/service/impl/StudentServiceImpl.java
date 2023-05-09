package com.example.projct_course_selection_system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.projct_course_selection_system.constants.RtnCode;
import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.repository.CourseDao;
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Response;
import com.example.projct_course_selection_system.vo.StudentCourseTable;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private StudentDao studentDao;

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
				if (!dropCourse(studentID, c).getMessage().equals(RtnCode.SUCCESSFUL.getMessage())) {
					return dropCourse(studentID, c);
				}
			}
		}
		// 3.刪除學生
		studentDao.deleteById(studentID);
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response selectCourse(String studentID, List<String> courseList) {
		// 1-1.查詢欲查學生是否存在
		Optional<Student> thisStudent = studentDao.findById(StringUtils.hasText(studentID) ? studentID : "");
		if (!thisStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1-2.確認欲選課程存在，並去除重複選課
		List<Course> selectCourses = courseDao
				.findAllById(CollectionUtils.isEmpty(courseList) ? new ArrayList<>(Arrays.asList("")) : courseList);
		if (CollectionUtils.isEmpty(selectCourses) || selectCourses.size() != new HashSet<>(courseList).size()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 2-1.找出學生的修課表
		Response studentCourseSchedule = courseSchedule(studentID);
		// 2-2.若已存在課程，取出課表，並合併預選課表(也可能不存在課程)
		List<Course> alreadyHaveCoures = new ArrayList<>();
		if (studentCourseSchedule.getMessage().equals(RtnCode.SUCCESS.getMessage())) {
			alreadyHaveCoures = studentCourseSchedule.getCourseList();
		}
		// 不論如何，預選課程必定要做比較，如果有已選課程，便同時做比較
		alreadyHaveCoures.addAll(0, selectCourses);
		// 2-3.計算欲選課程總學分
		int expectCredits = 0;
		for (int i = 0; i < selectCourses.size(); i++) {
			// 2-3-1確認修課人數，排除:選課人數已滿
			if (selectCourses.get(i).getPersonLimit() == 0) {
				return new Response(RtnCode.FULLY_SELECTED.getMessage());
			}
			// 2-3-2選課名單中不能撞名與互相衝堂
			for (int j = i + 1; j < alreadyHaveCoures.size(); j++) {
				// 排除:相同課號或相同課名
				if (selectCourses.get(i).getCourseNumber().equals(alreadyHaveCoures.get(j).getCourseNumber())
						|| selectCourses.get(i).getCourseTitle().equals(alreadyHaveCoures.get(j).getCourseTitle())) {
					return new Response(RtnCode.ALREADY_EXISTED.getMessage());
					// 排除:上課日期相同且時間相同或衝堂
				} else if (selectCourses.get(i).getSchedule().equals(alreadyHaveCoures.get(j).getSchedule())) {
					if (selectCourses.get(i).getStartTime().isBefore(alreadyHaveCoures.get(j).getEndTime())
							|| selectCourses.get(i).getEndTime().isAfter(alreadyHaveCoures.get(j).getStartTime())
							|| selectCourses.get(i).getStartTime().equals(alreadyHaveCoures.get(j).getStartTime())
							|| selectCourses.get(i).getEndTime().equals(alreadyHaveCoures.get(j).getEndTime())) {
						return new Response(RtnCode.CONFLICT.getMessage());
					}
				}
			}
			expectCredits += selectCourses.get(i).getCredits();
			// 2-3-3.若無問題，便修改修課人數
			selectCourses.get(i).setPersonLimit(selectCourses.get(i).getPersonLimit() - 1);
		}
		// 2-3-4排除:學分超過上限
		if (expectCredits > thisStudent.get().getCreditsLimit()) {
			return new Response(RtnCode.OUT_OF_LIMIT.getMessage());
		}

		// 3.修改學生修課列表
		List<String> getNewCourseNumber = new ArrayList<>();
		for (Course a : alreadyHaveCoures) {
			getNewCourseNumber.add(a.getCourseNumber());
		}
		// 4.選課結束，儲存資訊
		return (studentDao.updateStudentCredits(thisStudent.get().getStudentID(), String.join(", ", getNewCourseNumber),
				thisStudent.get().getCreditsLimit() - expectCredits) == 1
				&& !courseDao.saveAll(selectCourses).isEmpty()) ? new Response(RtnCode.SUCCESSFUL.getMessage())
						: new Response(RtnCode.INCORRECT.getMessage());
	}

	@Override
	public Response dropCourse(String studentID, String courseNumber) {
		// 1.確認學生是否包含此課程
		StudentCourseTable res = studentDao.findStudentCourse(studentID, courseNumber);
		if (res == null) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 2.取出課程並刪除
		String[] courseEle = res.getCourseNumbers().split(", ");
		List<String> courseList = Arrays.stream(courseEle).collect(Collectors.toList());
		courseList.remove(courseNumber);

		// 3.修改課程及學生狀態，恢復課程修課人數及學分
		return (courseDao.reviseCoursePerson(courseNumber, res.getPersonlimit() + 1) == 1
				&& studentDao.updateStudentCredits(studentID, String.join(", ", courseList),
						res.getCreditsLimit() + res.getCredits()) == 1) ? new Response(RtnCode.SUCCESSFUL.getMessage())
								: new Response(RtnCode.INCORRECT.getMessage());
	}

	@Override
	public Response courseSchedule(String studentID) {
		// 1.執行搜尋
		List<StudentCourseTable> studentCourseList = studentDao.findStudentCourseList(studentID);
		if (CollectionUtils.isEmpty(studentCourseList)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 2-1.整理學生個人資訊
		Student student = new Student(studentCourseList.get(0).getStudentID(), studentCourseList.get(0).getName());
		student.setCreditsLimit(studentCourseList.get(0).getCreditsLimit());
		// 2-2.整理課程資料
		List<Course> selectedCourses = new ArrayList<>();
		for (StudentCourseTable aD : studentCourseList) {
			// 建立Course並存到陣列中
			Course courseElement = new Course(aD.getCourseNumber(), aD.getCourseTitle(), aD.getSchedule(),
					aD.getStartTime(), aD.getEndTime(), aD.getCredits());
			courseElement.setPersonLimit(aD.getPersonlimit());
			selectedCourses.add(courseElement);
		}
		return new Response(student, selectedCourses, RtnCode.SUCCESS.getMessage());
	}

}

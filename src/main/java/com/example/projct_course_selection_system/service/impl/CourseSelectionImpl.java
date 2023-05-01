package com.example.projct_course_selection_system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.vo.Response;
import com.example.projct_course_selection_system.vo.StudentCourseTable;

@Service
public class CourseSelectionImpl implements CourseSelection {

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private StudentDao studentDao;

	@Override
	public Response selectCourse(String studentID, List<String> courseList) {
		// 0.防空、白
		if (!StringUtils.hasText(studentID) || CollectionUtils.isEmpty(courseList)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}

		// 1-1.查詢欲查學生是否存在
		Optional<Student> thisStudent = studentDao.findById(studentID);
		if (!thisStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1-2.確認欲選課程存在
		List<Course> selectCourses = courseDao.findAllById(courseList);
		if (CollectionUtils.isEmpty(selectCourses) || selectCourses.size() != courseList.size()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1.3檢查欲選課程可選、衝突與否
		Response conflictCheck = selectCourseCheck(selectCourses);
		if (!conflictCheck.getMessage().equals(RtnCode.SUCCESS.getMessage())) {
			return conflictCheck;
		}

		// 2-1.找出學生的修課表
		Response studentCourseTable = courseSchedule(studentID);
		// 2-2.若已存在課程，進行進一步檢查
		if (studentCourseTable.getMessage().equals(RtnCode.SUCCESS.getMessage())) {
			// 2-3.檢查欲選課程與學生已選課程是否學分數超過、重複、衝堂
			Response checkResult = CoursesCheck(conflictCheck.getExpectCredits(), selectCourses, studentCourseTable);
			if (!checkResult.getMessage().equals(RtnCode.SUCCESSFUL.getMessage())) {
				return checkResult;
			}
		}

		// 3-1.紀錄欲選課程，並修改修課人數
		List<String> getNewCourseNumber = new ArrayList<>();
		List<String> oldCourseNumber = new ArrayList<>();
		for (Course rC : selectCourses) {
			getNewCourseNumber.add(rC.getCourseNumber());
			rC.setPersonlimit(rC.getPersonlimit() - 1);
		}
		// 3-2.修改已選課學生修課列表
		if (!CollectionUtils.isEmpty(studentCourseTable.getCourseList())) {
			for (Course e : studentCourseTable.getCourseList()) {
				oldCourseNumber.add(e.getCourseNumber());
			}
			getNewCourseNumber.addAll(oldCourseNumber);
		}
		String newCouresList = String.join(", ", getNewCourseNumber);

		// 3-3.更新學生學分狀態與修課列表
		thisStudent.get().setCreditsLimit(thisStudent.get().getCreditsLimit() - conflictCheck.getExpectCredits());
		thisStudent.get().setCourseNumber(newCouresList);

		// 4.選課結束，儲存
		studentDao.save(thisStudent.get());
		courseDao.saveAll(selectCourses);
		return new Response(thisStudent.get(), selectCourses, RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response withdrawCourse(String studentID, String courseNumber) {
		// 0.防呆
		if (!StringUtils.hasText(studentID) || !StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}

		// 1-1.查詢學生
		Optional<Student> resStudent = studentDao.findById(studentID);
		if (!resStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1-2.確認課程存在與否
		Optional<Course> resCourse = courseDao.findById(courseNumber);
		if (!resCourse.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1-3.確認學生是否包含此課程
		if (!resStudent.get().getCourseNumber().contains(courseNumber)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 2-1.取出課程
		String[] courseEle = resStudent.get().getCourseNumber().split(", ");
		List<String> courseList = Arrays.stream(courseEle).collect(Collectors.toList());
		// 2-2.刪除課程
		courseList.remove(courseNumber);
		String newCouresList = String.join(", ", courseList);
		// 2-3.修改課程狀態，恢復課程修課人數
		resCourse.get().setPersonlimit(resCourse.get().getPersonlimit() + 1);
		// 2-4.修改學生狀態，恢復學生學分上限 與 修習課程
		resStudent.get().setCreditsLimit(resStudent.get().getCreditsLimit() + resCourse.get().getCredits());
		resStudent.get().setCourseNumber(newCouresList);

		// 3.退選，存回資料庫
		courseDao.save(resCourse.get());
		studentDao.save(resStudent.get());
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response courseSchedule(String studentID) {
		// 0.防呆:輸入參數空、白
		if (!StringUtils.hasText(studentID)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}

		// 1.確認學員是否存在
		boolean res = studentDao.existsById(studentID);
		if (!res) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 2.取得課表
		List<StudentCourseTable> studentCourseList = studentDao.findStudentCourseList(studentID);
		if (CollectionUtils.isEmpty(studentCourseList)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 3.整理資料
		Response courseTable = organizeData(studentCourseList);
		return new Response(courseTable.getStudent(), courseTable.getCourseList(), RtnCode.SUCCESS.getMessage());
	}

	// 檢查欲選課程是否可選、重複、學分超過總上限
	private Response selectCourseCheck(List<Course> resCourse) {
		// 計算欲選課程總學分
		int expectCredits = 0;
		for (Course rC : resCourse) {
			// 確認修課人數，排除:選課人數已滿
			if (rC.getPersonlimit() == 0) {
				return new Response(RtnCode.FULLY_SELECTED.getMessage());
			}
			// 選課名單中不能撞名與互相衝堂
			for (Course rCclone : resCourse) {
				// 因比較相同list，固唯一屬性課號相同時，便跳過
				if (!rC.getCourseNumber().equals(rCclone.getCourseNumber())) {
					// 排除:不同課號但相同課名
					if (rC.getCourseTitle().equals(rCclone.getCourseTitle())) {
						return new Response(RtnCode.ALREADY_EXISTED.getMessage());
					}
					// 排除:上課日期相同且時間相同或衝堂
					if (rC.getSchedule().equals(rCclone.getSchedule())) {
						if (rC.getStartTime().isBefore(rCclone.getEndTime())
								|| rCclone.getEndTime().isAfter(rCclone.getStartTime())) {
							return new Response(RtnCode.CONFLICT.getMessage());
						}
					}
				}
			}
			expectCredits += rC.getCredits();
		}
		// 排除:學分超過上限
		if (expectCredits > 10) {
			return new Response(RtnCode.OUT_OF_LIMIT.getMessage());
		}
		return new Response(expectCredits, RtnCode.SUCCESS.getMessage());
	}

	// 檢查欲選課程與學生已選課程是否學分數超過、重複、衝堂
	private Response CoursesCheck(int expectCredits, List<Course> selectCourses, Response studentCourseTable) {
		// 取出學生與課程資料
		Student thisStudent = studentCourseTable.getStudent();
		List<Course> alreadyHaveCoures = studentCourseTable.getCourseList();
		// 排除:選修分數超過學生剩餘學分
		if (expectCredits > thisStudent.getCreditsLimit()) {
			return new Response(RtnCode.OUT_OF_LIMIT.getMessage());
		}
		// 檢查:課程是否已選、相同課名、衝堂
		for (Course alHC : alreadyHaveCoures) {
			for (Course sC : selectCourses) {
				// 排除:相同課號與課名
				if (alHC.getCourseNumber().equals(sC.getCourseNumber())
						|| alHC.getCourseTitle().equals(sC.getCourseTitle())) {
					return new Response(RtnCode.ALREADY_EXISTED.getMessage());
				}
				// 衝堂檢查
				if (sC.getSchedule().equals(alHC.getSchedule())) {
					if (sC.getStartTime().equals(alHC.getEndTime()) || sC.getEndTime().equals(alHC.getStartTime())
							|| sC.getStartTime().isBefore(alHC.getEndTime())
							|| sC.getEndTime().isAfter(alHC.getStartTime())) {
						return new Response(RtnCode.INCORRECT.getMessage());
					}

				}
			}
		}
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	// 將findStudentCourseList()方法中資訊轉換成List<Course>
	private Response organizeData(List<StudentCourseTable> allData) {
		// 宣告輸出陣列，與學生
		List<Course> selectedCourses = new ArrayList<>();
		// 儲存學生個人資訊
		Student student = new Student(allData.get(0).getStudentID(), allData.get(0).getName());
		student.setCreditsLimit(allData.get(0).getCreditsLimit());
		// 梳理資料
		for (StudentCourseTable aD : allData) {
			// 建立Course並存到陣列中
			Course courseElement = new Course(aD.getCourseNumber(), aD.getCourseTitle(), aD.getSchedule(),
					aD.getStartTime(), aD.getEndTime(), aD.getCredits());
			courseElement.setPersonlimit(aD.getPersonlimit());
			selectedCourses.add(courseElement);
		}
		// 回傳整理後的資料
		return new Response(student, selectedCourses);
	}

}

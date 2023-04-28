package com.example.projct_course_selection_system.service.impl;

import java.time.Duration;
import java.time.LocalTime;
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
import com.example.projct_course_selection_system.service.ifs.StudentService;
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

		// 1-1.查詢學生是否存在
		Optional<Student> resStudent = studentDao.findById(studentID);
		if (!resStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1-2.確認課程存在
		List<Course> resCourse = courseDao.findAllById(courseList);
		if (CollectionUtils.isEmpty(resCourse)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		// 2.1檢查欲選課程可選與衝突偶與否
		Response courseCheck = selectCourseCheck(resCourse);
		if (!courseCheck.getMessage().equals(RtnCode.SUCCESSFUL.getMessage())) {
			return new Response(RtnCode.CONFLICT.getMessage());
		}

		// 2-2.計算欲選課程總學分
		// 找出學生的修課表
		List<Course> allCourse = courseDao.findAll();
		List<Course> selectedCourses = new ArrayList<>();
		int expectCredits = 0;
		for (Course rC : resCourse) {
			expectCredits += rC.getCredits();
		}
		if (StringUtils.hasText(resStudent.get().getCourseNumber())) {
			// 確認學生學分數狀態
			if (resStudent.get().getCreditsLimit() < expectCredits) {
				return new Response(RtnCode.INCORRECT.getMessage());
			}
			// 紀錄已選取學分
			for (Course c : allCourse) {
				if (resStudent.get().getCourseNumber().contains(c.getCourseNumber())) {
					selectedCourses.add(c);
				}
			}
			// 2-2.避免重複學程(包含課名)，比對新課程是否衝堂
			for (Course sC : selectedCourses) {
				for (Course rC : resCourse) {
					if (sC.getCourseNumber().equals(rC.getCourseNumber())
							|| sC.getCourseTitle().equals(rC.getCourseTitle())) {
						return new Response(RtnCode.ALREADY_EXISTED.getMessage());
					}
					if (rC.getSchedule().equals(sC.getSchedule())) {
						if (rC.getStartTime().isBefore(sC.getEndTime()) || rC.getEndTime().isAfter(sC.getStartTime())) {
							return new Response(RtnCode.INCORRECT.getMessage());
						}
					}
				}
			}
		}
		// 3-2.設定學生狀態
		List<String> getNewCourseNumber = new ArrayList<>();
		if (!CollectionUtils.isEmpty(selectedCourses)) {
			for (Course sC : selectedCourses) {
				getNewCourseNumber.add(sC.getCourseNumber());
			}
		}
		// 紀錄課程狀態
		for (Course rC : resCourse) {
			getNewCourseNumber.add(rC.getCourseNumber());
			rC.setPersonlimit(rC.getPersonlimit() - 1);
		}
		String newCouresList = String.join(", ", getNewCourseNumber);
		newCouresList = "{" + newCouresList + "}";
		resStudent.get().setCreditsLimit(resStudent.get().getCreditsLimit() - expectCredits);
		resStudent.get().setCourseNumber(newCouresList);
		// 4.選課結束，儲存
		studentDao.save(resStudent.get());
		courseDao.saveAll(resCourse);
		return new Response(RtnCode.SUCCESSFUL.getMessage());
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
		String[] courseEle = resStudent.get().getCourseNumber().replaceAll("[{}\"]", "").split(", ");
		List<String> courseList = Arrays.stream(courseEle).collect(Collectors.toList());
		// 2-2.刪除課程
		courseList.remove(courseNumber);
		String newCouresList = String.join(", ", courseList);
		newCouresList = "{" + newCouresList + "}";
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
		Optional<Student> res = studentDao.findById(studentID);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 2.判斷學生課表是否存在
		if (!StringUtils.hasText(res.get().getCourseNumber())) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 3.取得課表
		List<StudentCourseTable> studentCourseList = studentDao.findStudentCourseList(studentID);
		List<Course> selectedCourses = new ArrayList<>();
		for (StudentCourseTable sCL : studentCourseList) {
			Course courseElement = new Course(sCL.getCourseNumber(), sCL.getCourseTitle(), sCL.getSchedule(),
					sCL.getStartTime(), sCL.getEndTime(), sCL.getCredits());
			courseElement.setPersonlimit(sCL.getPersonlimit());
			selectedCourses.add(courseElement);
			Student student = new Student(sCL.getStudentID(), sCL.getName(),sCL.getCourseNumber());
			student.setCreditsLimit(sCL.getCreditsLimit());
		}
		return new Response(res.get(), selectedCourses, RtnCode.SUCCESS.getMessage());
	}

	// 檢查課程是否可選或重複
	private Response selectCourseCheck(List<Course> resCourse) {
		for (Course rC : resCourse) {
			// 1.確認修課人數，排除:選課人數已滿
			if (rC.getPersonlimit() == 0) {
				return new Response(RtnCode.FULLY_SELECTED.getMessage());
			}
			// 2.選課名單中不能撞名與互相衝堂
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
		}
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}
	
	//將findStudentCourseList()方法中資訊轉換成List<Course>
	private List<Course> organizeData(List<StudentCourseTable> allData) {
		// 宣告輸出陣列
		List<Course> selectedCourses = new ArrayList<>();
		// 梳理資料
		for (StudentCourseTable aD : allData) {
			// 建立Course並存到陣列中
			Course courseElement = new Course(aD.getCourseNumber(), aD.getCourseTitle(), aD.getSchedule(),
					aD.getStartTime(), aD.getEndTime(), aD.getCredits());
			courseElement.setPersonlimit(aD.getPersonlimit());
			selectedCourses.add(courseElement);
			Student student = new Student(aD.getStudentID(), aD.getName(),aD.getCourseNumber());
			student.setCreditsLimit(aD.getCreditsLimit());
		}
		return null;
	}

}

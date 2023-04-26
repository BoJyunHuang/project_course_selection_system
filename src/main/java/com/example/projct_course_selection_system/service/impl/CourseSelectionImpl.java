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
		for (Course rC : resCourse) {
			// 1-2-1.確認修課人數
			if (rC.getPersonlimit() == 0) {
				return new Response(RtnCode.FULLY_SELECTED.getMessage());
			}
			// 1-2-2.選課名單中不能撞名與互相衝堂
			for (Course rCclone : resCourse) {
				if (!rC.getCourseNumber().equals(rCclone.getCourseNumber())) {
					if (rC.getCourseTitle().equals(rCclone.getCourseTitle())) {
						return new Response(RtnCode.ALREADY_EXISTED.getMessage());
					}
					if (rC.getSchedule().equals(rCclone.getSchedule())) {
						if (rC.getStartTime().isBefore(rCclone.getEndTime())
								|| rCclone.getEndTime().isAfter(rCclone.getStartTime())) {
							return new Response(RtnCode.INCORRECT.getMessage());
						}
					}
				}
			}
		}
		// 2-1.確認學分數是否足夠
		int expectCredits = 0;
		for (Course rC : resCourse) {
			expectCredits += rC.getCredits();
		}
		List<Course> allCourse = courseDao.findAll();
		List<Course> selectedCourses = new ArrayList<>();
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

}

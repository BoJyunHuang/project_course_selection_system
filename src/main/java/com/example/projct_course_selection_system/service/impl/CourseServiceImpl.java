package com.example.projct_course_selection_system.service.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.projct_course_selection_system.constants.RtnCode;
import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.repository.CourseDao;
import com.example.projct_course_selection_system.service.ifs.CourseService;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseDao courseDao;

	// pattern 工作天
	private List<String> weekday = new ArrayList<>(
			Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
	// pattern 早上最早上課時間
	private LocalTime Morning = LocalTime.of(8, 0);
	// pattern 晚上最晚下課時間
	private LocalTime Evening = LocalTime.of(19, 0);

	@Override
	public Response addCourse(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits) {
		// 0.防呆:輸入參數空、白
		if (!StringUtils.hasText(courseNumber) || !StringUtils.hasText(courseTitle) || !StringUtils.hasText(schedule)
				|| startTime == null || endTime == null || startTime.toString().isBlank()
				|| endTime.toString().isBlank()) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}

		// 1-1.符合型式:日期(星期)要在工作天範圍內
		if (!weekday.contains(schedule)) {
			return new Response(RtnCode.PATTERN_IS_NOT_MATCH.getMessage());
		}
		// 1-2.符合型式:時間要在正常上班時間且開始時間要比結束時間早
		if (startTime.isBefore(Morning) || endTime.isAfter(Evening) || startTime.isAfter(endTime)) {
			return new Response(RtnCode.PATTERN_IS_NOT_MATCH.getMessage());
		}
		// 1-3.符合型式:學分要在1~3之間
		if (credits < 1 || credits > 3) {
			return new Response(RtnCode.PATTERN_IS_NOT_MATCH.getMessage());
		}

		// 2.儲存資料並檢查重複
		return courseDao.insertCourse(courseNumber, courseTitle, schedule, startTime, endTime, credits) == 0 ?
				new Response(RtnCode.ALREADY_EXISTED.getMessage()):new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response reviseCourse(Course course) {
		// 0.防呆:輸入參數key空、白
		if (course == null || !StringUtils.hasText(course.getCourseNumber())) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}

		// 1.查詢資料是否存在
		Optional<Course> res = courseDao.findById(course.getCourseNumber());
		// 排除:資料不存在
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}

		boolean isRevise = false;
		// 2-1.修改課名
		if (StringUtils.hasText(course.getCourseTitle())) {
			// 排除:修改資訊與原資訊相同
			if (res.get().getCourseTitle().equals(course.getCourseTitle())) {
				return new Response(RtnCode.REPEAT.getMessage());
			}
			res.get().setCourseTitle(course.getCourseTitle());
			isRevise = true;
		}
		// 2-2.修改日期
		if (StringUtils.hasText(course.getSchedule())) {
			// 排除:不符合工作天
			if (!weekday.contains(course.getSchedule())) {
				return new Response(RtnCode.PATTERN_IS_NOT_MATCH.getMessage());
			}
			// 排除:修改資訊與原資訊相同
			if (res.get().getSchedule().equals(course.getSchedule())) {
				return new Response(RtnCode.REPEAT.getMessage());
			}
			res.get().setSchedule(course.getSchedule());
			isRevise = true;
		}
		// 2-3.修改時間
		if (course.getStartTime() != null || course.getEndTime() != null) {
			// 排除:時間不在正常上班時間且開始時間要比結束時間晚
			if (course.getStartTime().isBefore(Morning) || course.getEndTime().isAfter(Evening)
					|| course.getStartTime().isAfter(course.getEndTime())) {
				return new Response(RtnCode.PATTERN_IS_NOT_MATCH.getMessage());
			}
			res.get().setStartTime(course.getStartTime());
			res.get().setEndTime(course.getEndTime());
			isRevise = true;
		}
		// 2-4.修改學分
		if (course.getCredits() > 0 && course.getCredits() < 4) {
			// 排除:修改資訊與原資訊相同
			if (res.get().getCredits() == course.getCredits()) {
				return new Response(RtnCode.REPEAT.getMessage());
			}
			res.get().setCredits(course.getCredits());
			isRevise = true;
		}

		// 3.儲存並判斷是否有修改內容
		return isRevise ? new Response(courseDao.save(res.get()), RtnCode.SUCCESS.getMessage())
				: new Response(RtnCode.INCORRECT.getMessage());
	}

	@Override
	public Response deleteCourse(String courseNumber) {
		// 刪除課程
		return courseDao.deleteCourse(courseNumber) == 0 ? new Response(RtnCode.INCORRECT.getMessage())
				: new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response findCourseInfo(Course course) {
		// 存在字串判斷，空或無轉成空字串；執行自定義方法，尋找資料
		List<Course> courseList = courseDao.searchByNumberOrTitle(
				StringUtils.hasText(course.getCourseNumber()) ? course.getCourseNumber() : "",
				StringUtils.hasText(course.getCourseTitle()) ? course.getCourseTitle() : "");
		return courseList.size() > 0 ? new Response(courseList, RtnCode.SUCCESS.getMessage())
				: new Response(RtnCode.NOT_FOUND.getMessage());
	}
}

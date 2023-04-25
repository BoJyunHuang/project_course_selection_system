package com.example.projct_course_selection_system.service.impl;

import java.time.Duration;
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
import com.example.projct_course_selection_system.vo.Response;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseDao courseDao;

	private List<String> weekday = new ArrayList<>(
			Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));

	private LocalTime Morning = LocalTime.of(8, 0);

	private LocalTime Evening = LocalTime.of(19, 0);

	@Override
	public Response addCourse(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber) || !StringUtils.hasText(courseTitle) || !StringUtils.hasText(schedule)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 時間防空、白
		if (!StringUtils.hasText(startTime.toString()) || !StringUtils.hasText(endTime.toString())) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 星期要在工作天
		if (!weekday.contains(schedule)) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 時間要在正常上班時間且開始時間要比結束時間少
		if (startTime.isBefore(Morning) || endTime.isAfter(Evening) || startTime.isAfter(endTime)) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 學分要在1~3之間
		if (credits < 1 || credits > 3) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 學分要與課堂時間"要"相差一小時內，不然學分與學習時數不相符
		Duration duration = Duration.between(startTime, endTime);
		if (LocalTime.of((credits), 0).getHour() < duration.toHours()
				|| LocalTime.of((credits + 1), 0).getHour() > duration.toHours()) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 確認有無重複課程
		Optional<Course> res = courseDao.findById(courseNumber);
		if (res.isPresent()) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 儲存課程
		Course course = new Course(courseNumber, courseTitle, schedule, startTime, endTime, credits);
		return new Response(courseDao.save(course), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response reviseCourseTitle(String courseNumber, String courseTitle) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber) || !StringUtils.hasText(courseTitle)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢資料
		Optional<Course> res = courseDao.findById(courseNumber);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 比對資料
		if (res.get().getCourseTitle().equals(courseTitle)) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 修正資料
		res.get().setCourseTitle(courseTitle);
		return new Response(courseDao.save(res.get()), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response reviseCourseSchedule(String courseNumber, String schedule, LocalTime startTime, LocalTime endTime) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber) || !StringUtils.hasText(schedule)
				|| !StringUtils.hasText(startTime.toString()) || !StringUtils.hasText(endTime.toString())) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 星期要在工作天
		if (!weekday.contains(schedule)) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 時間要在正常上班時間且開始時間要比結束時間少
		if (startTime.isBefore(Morning) || endTime.isAfter(Evening) || startTime.isAfter(endTime)) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 查詢資料
		Optional<Course> res = courseDao.findById(courseNumber);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 更新資料不得重複
		if (res.get().getSchedule().equals(schedule) && res.get().getStartTime().equals(startTime)
				&& res.get().getEndTime().equals(endTime)) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 學分要與課堂時間"要"相差一小時內，不然學分與學習時數不相符
		Duration duration = Duration.between(startTime, endTime);
		if (LocalTime.of((res.get().getCredits()), 0).getHour() < duration.toHours()
				|| LocalTime.of((res.get().getCredits() + 1), 0).getHour() > duration.toHours()) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 修正資料
		res.get().setSchedule(schedule);
		res.get().setStartTime(startTime);
		res.get().setEndTime(endTime);
		return new Response(courseDao.save(res.get()), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response reviseCourseAll(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber) || !StringUtils.hasText(courseTitle) || !StringUtils.hasText(schedule)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 時間防空、白
		if (!StringUtils.hasText(startTime.toString()) || !StringUtils.hasText(endTime.toString())) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 星期要在工作天
		if (!weekday.contains(schedule)) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 時間要在正常上班時間且開始時間要比結束時間少
		if (startTime.isBefore(Morning) || endTime.isAfter(Evening) || startTime.isAfter(endTime)) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 學分要在1~3之間
		if (credits < 1 || credits > 3) {
			return new Response(RtnCode.PATTERNISNOTMATCH.getMessage());
		}
		// 學分要與課堂時間"要"相差一小時內，不然學分與學習時數不相符
		Duration duration = Duration.between(startTime, endTime);
		if (LocalTime.of((credits), 0).getHour() < duration.toHours()
				|| LocalTime.of((credits + 1), 0).getHour() > duration.toHours()) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 查詢資料
		Optional<Course> res = courseDao.findById(courseNumber);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 比對資料
		if (res.get().getCourseTitle().equals(courseTitle) && res.get().getSchedule().equals(schedule)
				&& res.get().getStartTime().equals(startTime) && res.get().getEndTime().equals(endTime)
				&& res.get().getCredits() == credits) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 儲存資料
		res.get().setCourseTitle(courseTitle);
		res.get().setSchedule(schedule);
		res.get().setStartTime(startTime);
		res.get().setEndTime(endTime);
		res.get().setCredits(credits);
		return new Response(courseDao.save(res.get()), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response deleteCourse(String courseNumber) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢資料
		Optional<Course> res = courseDao.findById(courseNumber);
		if (!res.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 確認修課人數
		if (res.get().getPersonlimit() != 3) {
			return new Response(RtnCode.BEEN_SELECTED.getMessage());
		}
		// 刪除課程
		courseDao.delete(res.get());
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

}

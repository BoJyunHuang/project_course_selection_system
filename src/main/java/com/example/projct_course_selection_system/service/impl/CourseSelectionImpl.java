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
import com.example.projct_course_selection_system.vo.Response;

@Service
public class CourseSelectionImpl implements CourseSelection {

	@Autowired
	private CourseDao courseDao;

	@Autowired
	private StudentDao studentDao;

	private List<String> weekday = new ArrayList<>(
			Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));

	private LocalTime Morning = LocalTime.of(8, 0);

	private LocalTime Evening = LocalTime.of(19, 0);

	@Override
	public Response addCourse(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
			LocalTime endTime, int credits) {
		// 防呆
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
		Course res = courseDao.findByCourseNumber(courseNumber);
		if (res != null) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 儲存課程
		Course course = new Course(courseNumber, courseTitle, schedule, startTime, endTime, credits);
		return new Response(courseDao.save(course), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response reviseCourseByTitle(String courseNumber, String courseTitle) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢資料
		Course res = courseDao.findByCourseNumber(courseNumber);
		if (res == null) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 比對資料
		if (res.getCourseTitle().equals(courseTitle)) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 修正資料
		res.setCourseTitle(courseTitle);
		return new Response(courseDao.save(res), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response reviseCourseBySchedule(String courseNumber, String schedule, LocalTime startTime,
			LocalTime endTime) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber)) {
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
		Course res = courseDao.findByCourseNumber(courseNumber);
		if (res == null) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 比對資料
		if (res.getSchedule().equals(schedule) && res.getStartTime().equals(startTime)
				&& res.getEndTime().equals(endTime)) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 學分要與課堂時間"要"相差一小時內，不然學分與學習時數不相符
		Duration duration = Duration.between(startTime, endTime);
		if (LocalTime.of((res.getCredits()), 0).getHour() < duration.toHours()
				|| LocalTime.of((res.getCredits() + 1), 0).getHour() > duration.toHours()) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 修正資料
		res.setSchedule(schedule);
		res.setStartTime(startTime);
		res.setEndTime(endTime);
		return new Response(courseDao.save(res), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response reviseCourseByAll(String courseNumber, String courseTitle, String schedule, LocalTime startTime,
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
		// 查詢資料
		Course res = courseDao.findByCourseNumber(courseNumber);
		if (res == null) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 比對資料
		if (res.getCourseTitle().equals(courseTitle) && res.getSchedule().equals(schedule)
				&& res.getStartTime().equals(startTime) && res.getEndTime().equals(endTime)
				&& res.getCredits() == credits) {
			return new Response(RtnCode.ALREADY_EXISTED.getMessage());
		}
		// 學分要與課堂時間"要"相差一小時內，不然學分與學習時數不相符
		Duration duration = Duration.between(startTime, endTime);
		if (LocalTime.of((credits), 0).getHour() < duration.toHours()
				|| LocalTime.of((credits + 1), 0).getHour() > duration.toHours()) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 儲存資料
		res.setCourseTitle(courseTitle);
		res.setSchedule(schedule);
		res.setStartTime(startTime);
		res.setEndTime(endTime);
		res.setCredits(credits);
		return new Response(courseDao.save(res), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response deleteCourse(String courseNumber) {
		// 防空、白
		if (!StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢資料
		Course res = courseDao.findByCourseNumber(courseNumber);
		if (res == null) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 確認修課人數
		if (res.getPersonlimit() != 3) {
			return new Response(RtnCode.BEEN_SELECTED.getMessage());
		}
		// 刪除課程
		courseDao.delete(res);
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response addStudent(String studentID, String studentName) {
		// 防空、白
		if (!StringUtils.hasText(studentID) || !StringUtils.hasText(studentName)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 新增資料
		Student student = new Student(studentID, studentName);
		return new Response(studentDao.save(student), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public Response selectCourse(String studentID, List<Course> courseList) {
		// 0.防空、白
		if (!StringUtils.hasText(studentID) || !CollectionUtils.isEmpty(courseList)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 1-1.查詢學生是否存在
		Optional<Student> resStudent = studentDao.findById(studentID);
		if (!resStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 1-2.確認課程存在，及確認修課人數
		List<Course> resCourse = courseDao.findAllById(courseList);
		if (CollectionUtils.isEmpty(resCourse)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		for (Course rC :resCourse) {
			if (rC.getPersonlimit() == 0) {
				return new Response(RtnCode.INCORRECT.getMessage());
			}
		}
		// 2-1.避免重複學程
		for (Course rC : resCourse) {
			if (resStudent.get().getCourseNumber().contains(rC.getCourseNumber())) {
				return new Response(RtnCode.ALREADY_EXISTED.getMessage());
			}
		}
		// 2-2.確認學分數是否足夠
		if (resStudent.get().getCreditsLimit() < 0) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 2-3找出學員的課程
		String[] ListOfSelectedCourse = null;
		if (resStudent.get().getCourseNumber().length() != 0) {
			String tempList1 = resStudent.get().getCourseNumber();
			String tempList2 = tempList1.substring(0, tempList1.length() - 1);
			ListOfSelectedCourse = tempList2.split(", ");
		}
		List<String> listOfCourseSelect = Arrays.stream(ListOfSelectedCourse).collect(Collectors.toList());
		List<Course> selectedCourse = courseDao.findAllById(listOfCourseSelect);
		// 比對新選課程學分數是否超規
		int selectCoursesTotalCredits =0;
		for (Course rC:resCourse) {
			selectCoursesTotalCredits += rC.getCredits();
		}
		if (selectCoursesTotalCredits>resStudent.get().getCreditsLimit()) {
			return new Response(RtnCode.INCORRECT.getMessage());
		}
		// 比對新課程名稱是否重複或是否衝堂
		for (Course rC :resCourse) {
			for (Course sC:selectedCourse) {
				if (rC.getCourseTitle().equals(sC.getCourseTitle())) {
					return new Response(RtnCode.INCORRECT.getMessage());
				}
				if (rC.getSchedule().equals(sC.getSchedule())) {
					if (rC.getStartTime().isBefore(sC.getEndTime()) || rC.getEndTime().isAfter(sC.getStartTime())) {
						return new Response(RtnCode.INCORRECT.getMessage());
					}
				}
			}
		}
		// 選課 並設定課程狀態
		int newCredits = 0;
		for (Course rC :resCourse) {
			listOfCourseSelect.add(rC.getCourseNumber());
			newCredits += rC.getCredits();
			rC.setPersonlimit(rC.getPersonlimit()-1);
		}
		// 設定學生狀態
		String newCouresList = String.join(", ", listOfCourseSelect);
		newCouresList = "{" + newCouresList + "}";
		resStudent.get().setCreditsLimit(resStudent.get().getCreditsLimit() - newCredits);
		resStudent.get().setCourseNumber(newCouresList);
		// 儲存
		studentDao.save(resStudent.get());
		courseDao.saveAll(resCourse);
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response withdrawCourse(String studentID, String courseNumber) {
		// 防呆
		if (StringUtils.hasText(studentID) || StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢學生
		Optional<Student> resStudent = studentDao.findById(studentID);
		if (!resStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 確認課程存在與否
		Course resCourse = courseDao.findByCourseNumber(courseNumber);
		if (resCourse == null) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 確認學生是否包含此課程
		if (!resStudent.get().getCourseNumber().contains(courseNumber)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 取出課程
		String[] courseEle = resStudent.get().getCourseNumber().replaceAll("[{}\"]", "").split(", ");
		List<String> listOfCourseElement = Arrays.stream(courseEle).collect(Collectors.toList());
		// 刪除課程
		listOfCourseElement.remove(courseNumber);
		String newCouresList = String.join(", ", listOfCourseElement);
		newCouresList = "{" + newCouresList + "}";
		// 恢復課程修課人數
		resCourse.setPersonlimit(resCourse.getPersonlimit() + 1);
		// 恢復學生學分上限 與 修習課程
		resStudent.get().setCreditsLimit(resStudent.get().getCreditsLimit() + resCourse.getCredits());
		resStudent.get().setCourseNumber(newCouresList);
		// 放回資料庫
		courseDao.save(resCourse);
		studentDao.save(resStudent.get());
		return new Response(RtnCode.SUCCESSFUL.getMessage());
	}

	@Override
	public Response deleteStudent(String studentID) {
		// 防空、白
		if (!StringUtils.hasText(studentID)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 查詢學生
		Optional<Student> resStudent = studentDao.findById(studentID);
		if (!resStudent.isPresent()) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 確認有無課程
		if (resStudent.get().getCourseNumber().length() != 0) {
			String tempList1 = resStudent.get().getCourseNumber();
			String tempList2 = tempList1.substring(0, tempList1.length() - 1);
			String[] listOfSelectedCourse = tempList2.split(", ");
			// 退選
			for (String c : listOfSelectedCourse) {
				withdrawCourse(studentID, c);
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

		return null;
	}

	@Override
	public Response findCourseInfoByNumber(String courseNumber) {
		// 防空、
		if (!StringUtils.hasText(courseNumber)) {
			return new Response(RtnCode.CANNOT_EMPTY.getMessage());
		}
		// 尋找資料
		List<Course> res = courseDao.findByCourseTitle(courseNumber);
		if (CollectionUtils.isEmpty(res)) {
			return new Response(RtnCode.NOT_FOUND.getMessage());
		}
		// 印出資料
		return new Response(res, RtnCode.SUCCESS.getMessage());
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

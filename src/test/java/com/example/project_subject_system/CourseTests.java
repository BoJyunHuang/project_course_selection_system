package com.example.project_subject_system;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.projct_course_selection_system.ProjectCourseSelectionApplication;
import com.example.projct_course_selection_system.constants.RtnCode;
import com.example.projct_course_selection_system.entity.Course;
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.repository.CourseDao;
import com.example.projct_course_selection_system.service.ifs.CourseService;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 為了可以使用@BeforeAll和@AfterAll
public class CourseTests {

	@Autowired
	private CourseService cSer;

	@Autowired
	private CourseDao cDao;

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		Course c = newCourseData();
		cDao.deleteById(c.getCourseNumber());
	}

	@BeforeEach
	public void beforeEach() {
		// 各單元執行前執行，預設資料內容
		Course c = newCourseData();
		cDao.save(c);
	}

	@Test
	public void updateCourseTitleByIdTset() {
		// 帶入生成資料
		Course c = newCourseData();
		// 更改課名
		c.setCourseTitle("TestDao");
		int res1 = cDao.updateCourseTitleById(c.getCourseNumber(), c.getCourseTitle());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void updateCourseScheduleByIdTset() {
		// 帶入生成資料
		Course c = newCourseData();
		// 更改日期
		c.setSchedule("Tuesday");
		int res1 = cDao.updateCourseScheduleById(c.getCourseNumber(), c.getSchedule());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void updateCourseTimeByIdTset() {
		// 帶入生成資料
		Course c = newCourseData();
		// 更改時間
		c.setStartTime(LocalTime.of(15, 0));
		c.setEndTime(LocalTime.of(17, 0));
		int res1 = cDao.updateCourseTimeById(c.getCourseNumber(), c.getStartTime(), c.getEndTime());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void updateCourseCreditsByIdTset() {
		// 帶入生成資料
		Course c = newCourseData();
		// 更改學分
		c.setCredits(3);
		int res1 = cDao.updateCourseCreditsById(c.getCourseNumber(), c.getCredits());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void addCourseEmptyTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:課程編號為空
		c.setCourseNumber(null);
		Response res1 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		c.setCourseNumber("TXXX");
		// 狀況:開始時間為null
		c.setStartTime(null);
		Response res2 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res2.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addCoursePatternNotMatchTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:日期在假日
		c.setSchedule("Sunday");
		Response res1 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res1.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		c.setSchedule("Monday");
		// 狀況:時間過早
		c.setStartTime(LocalTime.of(7, 0));
		Response res2 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res2.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		c.setStartTime(LocalTime.of(9, 0));
		// 狀況:學分為0
		c.setCredits(0);
		Response res3 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res3.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		c.setCredits(2);
		// 狀況:時數過多差(比學分超出一小時以上)
		c.setEndTime(LocalTime.of(13, 0));
		Response res4 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res4.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void addCourseSaveAndExistedTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:儲存成功
		c.setCourseNumber("TXX1");
		Response res1 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:已存在課程
		Response res2 = cSer.addCourse(c.getCourseNumber(), c.getCourseTitle(), c.getSchedule(), c.getStartTime(),
				c.getEndTime(), c.getCredits());
		Assert.isTrue(res2.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		cSer.deleteCourse("TXX1");
	}

	@Test
	public void reviseCourseEmptyOrNotFoundTest() {
		// 帶入生成資料
		Request r = new Request();
		// 狀況:課程編號為空
		r.setCourseNumber(null);
		Response res1 = cSer.reviseCourse(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課程編號不存在
		r.setCourseNumber("TXX2");
		Response res2 = cSer.reviseCourse(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void reviseCourseRepeatTest() {
		// 帶入生成資料
		Course c = newCourseData();
		Request r = new Request();
		r.setCourseNumber(c.getCourseNumber());
		// 狀況:課名相同
		r.setCourseTitle(c.getCourseTitle());
		Response res1 = cSer.reviseCourse(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		r.setCourseTitle("");
		// 狀況:日期相同
		r.setSchedule(c.getSchedule());
		Response res2 = cSer.reviseCourse(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		r.setSchedule("");
		// 狀況:時間相同
		r.setStartTime(c.getStartTime());
		r.setEndTime(c.getEndTime());
		Response res3 = cSer.reviseCourse(r);
		Assert.isTrue(res3.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		r.setStartTime(null);
		r.setEndTime(null);
		// 狀況:學分相同
		r.setCredits(c.getCredits());
		Response res4 = cSer.reviseCourse(r);
		Assert.isTrue(res4.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void reviseCoursePatternNotMatchTest() {
		// 帶入生成資料
		Course c = newCourseData();
		Request r = new Request();
		r.setCourseNumber(c.getCourseNumber());
		// 狀況:日期為周末
		r.setSchedule("Sunday");
		Response res1 = cSer.reviseCourse(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		r.setSchedule(null);
		// 狀況:時間為半夜
		r.setStartTime(LocalTime.of(20, 0));
		r.setEndTime(LocalTime.of(21, 0));
		Response res2 = cSer.reviseCourse(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void reviseCourseIsTrueTest() {
		// 帶入生成資料
		Course c = newCourseData();
		Request r = new Request();
		r.setCourseNumber(c.getCourseNumber());
		// 狀況:空資料
		Response res1 = cSer.reviseCourse(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.INCORRECT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:改課名
		r.setCourseTitle("TestRevise");
		Response res2 = cSer.reviseCourse(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void deleteCourseEmpytTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:輸入為空白
		c.setCourseNumber("");
		Response res1 = cSer.deleteCourse(c.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteCourseNotFoundTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:找不到資料
		c.setCourseNumber("TXX2");
		Response res1 = cSer.deleteCourse(c.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteCourseBeenSelectedTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:有一人選課
		Optional<Course> res = cDao.findById(c.getCourseNumber());
		res.get().setPersonlimit(2);
		cDao.save(res.get());
		Response res1 = cSer.deleteCourse(c.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.BEEN_SELECTED.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteCourseSuccessTest() {
		// 帶入生成資料
		Course c = newCourseData();
		// 狀況:刪除成功
		Response res1 = cSer.deleteCourse(c.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
	
	@Test
	public void findCourseInfoEmptyTest() {
		// 帶入生成資料
		Request r = new Request();
		// 狀況:輸入為空
		Response res1 = cSer.findCourseInfo(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
	
	@Test
	public void findCourseInfoRepeatTest() {
		// 帶入生成資料
		Course c = newCourseData();
		Request r = new Request();
		// 狀況:雙重輸入
		r.setCourseNumber(c.getCourseNumber());
		r.setCourseTitle(c.getCourseTitle());
		Response res1 = cSer.findCourseInfo(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
	
	@Test
	public void findCourseInfoNotFoundTest() {
		// 帶入生成資料
		Request r = new Request();
		// 狀況:輸入不存在課程編號
		r.setCourseNumber("TXX1");
		Response res1 = cSer.findCourseInfo(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		r.setCourseNumber(null);
		// 狀況:輸入不存在課程名稱
		r.setCourseTitle("Test2");
		Response res2 = cSer.findCourseInfo(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}
	
	@Test
	public void findCourseInfoSuccessTest() {
		// 帶入生成資料
		Course c = newCourseData();
		Request r = new Request();
		// 狀況:存在課程編號
		r.setCourseNumber(c.getCourseNumber());
		Response res1 = cSer.findCourseInfo(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		r.setCourseNumber(null);
		// 狀況:存在課程名稱
		r.setCourseTitle(c.getCourseTitle());
		Response res2 = cSer.findCourseInfo(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	private Course newCourseData() {
		// 生成測試資料
		Course course = new Course("TXXX", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		return course;
	}
}

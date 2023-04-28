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

	@BeforeAll
	public void beforeAll() {
		// 最初執行，建立假資料、存檔進行測試
		Course c = resetData();
		cDao.save(c);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		Request r = newCourseData();
		cDao.deleteById(r.getCourseNumber());
	}

	@AfterEach
	public void afterEach() {
		// 各單元執行後執行，恢復預設資料內容
		Course c = resetData();
		cDao.save(c);
	}

	@Test
	public void updateCourseTitleByIdTset() {
		// 帶入生成資料
		Request r = newCourseData();
		// 更改課名
		r.setCourseTitle("TestDao");
		int res1 = cDao.updateCourseTitleById(r.getCourseNumber(), r.getCourseTitle());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void updateCourseScheduleByIdTset() {
		// 帶入生成資料
		Request r = newCourseData();
		// 更改日期
		r.setSchedule("Tuesday");
		int res1 = cDao.updateCourseScheduleById(r.getCourseNumber(), r.getSchedule());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void updateCourseTimeByIdTset() {
		// 帶入生成資料
		Request r = newCourseData();
		// 更改時間
		r.setStartTime(LocalTime.of(15, 0));
		r.setEndTime(LocalTime.of(17, 0));
		int res1 = cDao.updateCourseTimeById(r.getCourseNumber(), r.getStartTime(), r.getEndTime());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void updateCourseCreditsByIdTset() {
		// 帶入生成資料
		Request r = newCourseData();
		// 更改時間
		r.setCredits(3);
		int res1 = cDao.updateCourseCreditsById(r.getCourseNumber(), r.getCredits());
		Assert.isTrue(res1 == 1, RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void addCourseEmptyTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:課程編號為空
		r.setCourseNumber(null);
		Response res1 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		r.setCourseNumber("TXXX");
		// 狀況:開始時間為null
		r.setStartTime(null);
		Response res2 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res2.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addCoursePatternNotMatchTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:日期在假日
		r.setSchedule("Sunday");
		Response res1 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res1.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		r.setSchedule("Monday");
		// 狀況:時間過早
		r.setStartTime(LocalTime.of(7, 0));
		Response res2 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res2.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		r.setStartTime(LocalTime.of(9, 0));
		// 狀況:學分為0
		r.setCredits(0);
		Response res3 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res3.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		r.setCredits(2);
		// 狀況:時數過多差(比學分超出一小時以上)
		r.setEndTime(LocalTime.of(13, 0));
		Response res4 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res4.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void addCourseSaveAndExistedTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:儲存成功
		r.setCourseNumber("TXX1");
		Response res1 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:已存在課程
		Response res2 = cSer.addCourse(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		Assert.isTrue(res2.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		cSer.deleteCourse("TXX1");
	}

	@Test
	public void reviseCourseEmptyOrNotFoundTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:課程編號為空
		r.setCourseNumber(null);
		Response res1 = cSer.reviseCourse(r);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課程編號為空
		r.setCourseNumber("TXX2");
		Response res2 = cSer.reviseCourse(r);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void reviseCourseRepeatTest() {
		// 帶入生成資料
		Request r = newCourseData();
		Request rTest = new Request();
		rTest.setCourseNumber(r.getCourseNumber());
		// 狀況:課名相同
		rTest.setCourseTitle(r.getCourseTitle());
		Response res1 = cSer.reviseCourse(rTest);
		Assert.isTrue(res1.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		rTest.setCourseTitle("");
		// 狀況:日期相同
		rTest.setSchedule(r.getSchedule());
		Response res2 = cSer.reviseCourse(rTest);
		Assert.isTrue(res2.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		rTest.setSchedule("");
		// 狀況:時間相同
		rTest.setStartTime(r.getStartTime());
		rTest.setEndTime(r.getEndTime());
		Response res3 = cSer.reviseCourse(rTest);
		Assert.isTrue(res3.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		rTest.setStartTime(null);
		rTest.setEndTime(null);
		// 狀況:學分相同
		rTest.setCredits(r.getCredits());
		Response res4 = cSer.reviseCourse(rTest);
		Assert.isTrue(res4.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void reviseCoursePatternNotMatchTest() {
		// 帶入生成資料
		Request r = newCourseData();
		Request rTest = new Request();
		rTest.setCourseNumber(r.getCourseNumber());
		// 狀況:日期為周末
		rTest.setSchedule("Sunday");
		Response res1 = cSer.reviseCourse(rTest);
		Assert.isTrue(res1.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		rTest.setSchedule(null);
		// 狀況:時間為半夜
		rTest.setStartTime(LocalTime.of(20, 0));
		rTest.setEndTime(LocalTime.of(21, 0));
		Response res2 = cSer.reviseCourse(rTest);
		Assert.isTrue(res2.getMessage().equals(RtnCode.PATTERNISNOTMATCH.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void reviseCourseIsTrueTest() {
		// 帶入生成資料
		Request r = newCourseData();
		Request rTest = new Request();
		rTest.setCourseNumber(r.getCourseNumber());
		// 狀況:空資料
		Response res1 = cSer.reviseCourse(rTest);
		Assert.isTrue(res1.getMessage().equals(RtnCode.INCORRECT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:改課名
		rTest.setCourseTitle("TestRevise");
		Response res2 = cSer.reviseCourse(rTest);
		Assert.isTrue(res2.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void deleteCourseEmpytTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:輸入為空白
		r.setCourseNumber("");
		Response res1 = cSer.deleteCourse(r.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteCourseNotFoundTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:找不到資料
		r.setCourseNumber("TXX2");
		Response res1 = cSer.deleteCourse(r.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteCourseBeenSelectedTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:有一人選課
		Optional<Course> res = cDao.findById(r.getCourseNumber());
		res.get().setPersonlimit(2);
		cDao.save(res.get());
		Response res1 = cSer.deleteCourse(r.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.BEEN_SELECTED.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteCourseSuccessTest() {
		// 帶入生成資料
		Request r = newCourseData();
		// 狀況:刪除成功
		Response res1 = cSer.deleteCourse(r.getCourseNumber());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	private Request newCourseData() {
		// 生成測試資料
		Request request = new Request();
		request.setCourseNumber("TXXX");
		request.setCourseTitle("TestCourse");
		request.setSchedule("Monday");
		request.setStartTime(LocalTime.of(9, 0));
		request.setEndTime(LocalTime.of(11, 0));
		request.setCredits(2);
		return request;
	}

	private Course resetData() {
		// 建立預設課程資料
		Request r = newCourseData();
		Course c = new Course(r.getCourseNumber(), r.getCourseTitle(), r.getSchedule(), r.getStartTime(),
				r.getEndTime(), r.getCredits());
		return c;
	}

//	@BeforeEach
//	public void beforeEach() { // 各單元執行前執行
//		System.out.println("===== before_each ====");
//	}

}

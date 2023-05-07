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

	@BeforeEach
	public void beforeEach() {
		// 各單元執行前執行，預設資料內容
		Course c = new Course("TXXX", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		cDao.save(c);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		cDao.deleteById("TXXX");
	}
	
	@Test
	public void insertCourseTest() {
		// 狀況:課程編號已存在
		int res1 = cDao.insertCourse("TXXX", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res1 == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:新增成功
		int res2 = cDao.insertCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res2 == 1, RtnCode.TEST2_ERROR.getMessage());
		cDao.deleteById("TXX1");
	}

	@Test
	public void deleteCourseTest() {
		// 狀況:無此課程
		Course c = new Course("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		cDao.save(c);
		int res1 = cDao.deleteCourse(null);
		Assert.isTrue(res1 == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:有人修課
		c.setPersonLimit(2);
		cDao.save(c);
		int res2 = cDao.deleteCourse("TXX1");
		Assert.isTrue(res2 == 0, RtnCode.TEST2_ERROR.getMessage());
		// 況況:刪除成功
		c.setPersonLimit(3);
		cDao.save(c);
		int res3 = cDao.deleteCourse("TXX1");
		Assert.isTrue(res3 == 1, RtnCode.TEST3_ERROR.getMessage());
	}
	
	@Test
	public void searchByNumberOrTitleTest() {
		// 狀況:空查詢
		List<Course> res1 = cDao.searchByNumberOrTitle("", null);
		Assert.isTrue(res1.size() == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:查詢成功
		List<Course> res2 = cDao.searchByNumberOrTitle("TXXX", "TestCourse");
		Assert.isTrue(res2.size() == 1, RtnCode.TEST2_ERROR.getMessage());
	}
	
	@Test
	public void reviseCoursePersonTest() {
		// 狀況:無此課程
		int res1 = cDao.reviseCoursePerson("TXX1", 2);
		Assert.isTrue(res1 == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:修改成功
		int res2 = cDao.reviseCoursePerson("TXXX", 2);
		Assert.isTrue(res2 == 1, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addCourseTest() {
		// 狀況:課程編號為空
		Response res1 = cSer.addCourse(null, "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:結束時間為null
		Response res2 = cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), null, 2);
		Assert.isTrue(res2.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:日期在假日
		Response res3 = cSer.addCourse("TXX1", "TestCourse", "Sunday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res3.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		// 狀況:時間過早
		Response res4 = cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(7, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res4.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
		// 狀況:學分為0
		Response res5 = cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 0);
		Assert.isTrue(res5.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()),
				RtnCode.TEST5_ERROR.getMessage());
		// 狀況:儲存成功
		Response res6 = cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res6.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST6_ERROR.getMessage());
		// 狀況:已存在課程
		Response res7 = cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		Assert.isTrue(res7.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST7_ERROR.getMessage());
		cDao.deleteById("TXX1");
	}

	@Test
	public void reviseCourse1Test() {
		// 狀況:輸入為空
		Course c = new Course();
		Response res1 = cSer.reviseCourse(c);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課程編號不存在
		c.setCourseNumber("TXX2");
		Response res2 = cSer.reviseCourse(c);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		c.setCourseNumber("TXXX");
		// 狀況:課名相同
		c.setCourseTitle("TestCourse");
		Response res3 = cSer.reviseCourse(c);
		Assert.isTrue(res3.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		c.setCourseTitle("");
		// 狀況:日期相同
		c.setSchedule("Monday");
		Response res4 = cSer.reviseCourse(c);
		Assert.isTrue(res4.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST4_ERROR.getMessage());
		c.setSchedule("");
		// 狀況:時間相同
		c.setStartTime(LocalTime.of(9, 0));
		c.setEndTime(LocalTime.of(11, 0));
		Response res5 = cSer.reviseCourse(c);
		Assert.isTrue(res5.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST5_ERROR.getMessage());
		c.setStartTime(null);
		c.setEndTime(null);
		// 狀況:學分相同
		c.setCredits(2);
		Response res6 = cSer.reviseCourse(c);
		Assert.isTrue(res6.getMessage().equals(RtnCode.REPEAT.getMessage()), RtnCode.TEST6_ERROR.getMessage());
	}

	@Test
	public void reviseCourse2Test() {
		// 狀況:日期為周末
		Course c = new Course();
		c.setCourseNumber("TXXX");
		c.setSchedule("Sunday");
		Response res1 = cSer.reviseCourse(c);
		Assert.isTrue(res1.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()),	RtnCode.TEST1_ERROR.getMessage());
		c.setSchedule(null);
		// 狀況:時間為半夜
		c.setStartTime(LocalTime.of(20, 0));
		c.setEndTime(LocalTime.of(21, 0));
		Response res2 = cSer.reviseCourse(c);
		Assert.isTrue(res2.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		c.setStartTime(null);
		c.setEndTime(null);
		// 狀況:空資料
		Response res3 = cSer.reviseCourse(c);
		Assert.isTrue(res3.getMessage().equals(RtnCode.INCORRECT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		// 狀況:改課名
		c.setCourseTitle("TestRevise");
		Response res4 = cSer.reviseCourse(c);
		Assert.isTrue(res4.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void deleteCourseEmpytTest() {
		// 狀況:找不到資料
		Response res1 = cSer.deleteCourse("TXX1");
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:刪除成功
		Course cTest = new Course("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2);
		cDao.save(cTest);
		Response res2 = cSer.deleteCourse("TXX1");
		Assert.isTrue(res2.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void findCourseInfoTest() {
		// 帶入生成資料
		Course c = new Course();
		// 狀況:輸入不存在課程編號
		c.setCourseNumber("TXX1");
		Response res1 = cSer.findCourseInfo(c);
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:輸入不存在課程名稱
		c.setCourseTitle("Test2");
		Response res2 = cSer.findCourseInfo(c);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:存在課程編號
		c.setCourseNumber("TXXX");
		Response res3 = cSer.findCourseInfo(c);
		Assert.isTrue(res3.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		c.setCourseNumber(null);
		// 狀況:存在課程名稱
		c.setCourseTitle("TestCourse");
		Response res4 = cSer.findCourseInfo(c);
		Assert.isTrue(res4.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST4_ERROR.getMessage());
	}

}

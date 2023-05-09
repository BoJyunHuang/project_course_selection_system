package com.example.project_subject_system;

import java.time.LocalTime;

import org.junit.jupiter.api.AfterAll;
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

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 為了可以使用@BeforeAll和@AfterAll
public class CourseServiceTests {

	@Autowired
	private CourseService cSer;

	@Autowired
	private CourseDao cDao;

	@BeforeEach
	public void beforeEach() {
		// 各單元執行前執行，預設資料內容
		cDao.save(new Course("TXXX", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2));
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		cDao.deleteById("TXXX");
	}

	@Test
	public void insertCourseTest() {
		// 狀況:課程編號已存在
		Assert.isTrue(
				cDao.insertCourse("TXXX", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2) == 0,
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:新增成功
		Assert.isTrue(
				cDao.insertCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2) == 1,
				RtnCode.TEST2_ERROR.getMessage());
		cDao.deleteById("TXX1");
	}

	@Test
	public void deleteCourseTest() {
		// 狀況:無此課程
		cDao.save(new Course("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2));
		Assert.isTrue(cDao.deleteCourse(null) == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:有人修課
		cDao.save(new Course("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2, 2));
		Assert.isTrue(cDao.deleteCourse("TXX1") == 0, RtnCode.TEST2_ERROR.getMessage());
		// 況況:刪除成功
		cDao.save(new Course("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2, 3));
		Assert.isTrue(cDao.deleteCourse("TXX1") == 1, RtnCode.TEST3_ERROR.getMessage());
	}

	@Test
	public void searchByNumberOrTitleTest() {
		// 狀況:空查詢
		Assert.isTrue(cDao.searchByNumberOrTitle("", null).size() == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:查詢成功
		Assert.isTrue(cDao.searchByNumberOrTitle("TXXX", "TestCourse").size() == 1, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void reviseCoursePersonTest() {
		// 狀況:無此課程
		Assert.isTrue(cDao.reviseCoursePerson("TXX1", 2) == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:修改成功
		Assert.isTrue(cDao.reviseCoursePerson("TXXX", 2) == 1, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addCourseTest() {
		// 狀況:課程編號為空
		Assert.isTrue(cSer.addCourse(null, "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2)
				.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:結束時間為null
		Assert.isTrue(cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), null, 2).getMessage()
				.equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:日期在假日
		Assert.isTrue(cSer.addCourse("TXX1", "TestCourse", "Sunday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2)
				.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		// 狀況:時間過早
		Assert.isTrue(cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(7, 0), LocalTime.of(11, 0), 2)
				.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()), RtnCode.TEST4_ERROR.getMessage());
		// 狀況:學分為0
		Assert.isTrue(cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 0)
				.getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()), RtnCode.TEST5_ERROR.getMessage());
		// 狀況:儲存成功
		Assert.isTrue(cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2)
				.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST6_ERROR.getMessage());
		// 狀況:已存在課程
		Assert.isTrue(cSer.addCourse("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2)
				.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST7_ERROR.getMessage());
		cDao.deleteById("TXX1");
	}

	@Test
	public void reviseCourse1Test() {
		// 狀況:輸入為空
		Course c = new Course();
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課程編號不存在
		c.setCourseNumber("TXX2");
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		c.setCourseNumber("TXXX");
		// 狀況:課名相同
		c.setCourseTitle("TestCourse");
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.REPEAT.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		c.setCourseTitle("");
		// 狀況:日期相同
		c.setSchedule("Monday");
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.REPEAT.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
		c.setSchedule("");
		// 狀況:時間相同
		c.setStartTime(LocalTime.of(9, 0));
		c.setEndTime(LocalTime.of(11, 0));
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.REPEAT.getMessage()),
				RtnCode.TEST5_ERROR.getMessage());
		c.setStartTime(null);
		c.setEndTime(null);
		// 狀況:學分相同
		c.setCredits(2);
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.REPEAT.getMessage()),
				RtnCode.TEST6_ERROR.getMessage());
	}

	@Test
	public void reviseCourse2Test() {
		// 狀況:日期為周末
		Course c = new Course();
		c.setCourseNumber("TXXX");
		c.setSchedule("Sunday");
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		c.setSchedule(null);
		// 狀況:時間為半夜
		c.setStartTime(LocalTime.of(20, 0));
		c.setEndTime(LocalTime.of(21, 0));
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.PATTERN_IS_NOT_MATCH.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		c.setStartTime(null);
		c.setEndTime(null);
		// 狀況:空資料
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.INCORRECT.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		// 狀況:改課名
		c.setCourseTitle("TestRevise");
		Assert.isTrue(cSer.reviseCourse(c).getMessage().equals(RtnCode.SUCCESS.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void deleteCourseEmpytTest() {
		// 狀況:找不到資料
		Assert.isTrue(cSer.deleteCourse("TXX1").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:刪除成功
		cDao.save(new Course("TXX1", "TestCourse", "Monday", LocalTime.of(9, 0), LocalTime.of(11, 0), 2));
		Assert.isTrue(cSer.deleteCourse("TXX1").getMessage().equals(RtnCode.SUCCESSFUL.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void findCourseInfoTest() {
		// 帶入生成資料
		Course c = new Course();
		// 狀況:輸入不存在課程編號
		c.setCourseNumber("TXX1");
		Assert.isTrue(cSer.findCourseInfo(c).getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:輸入不存在課程名稱
		c.setCourseTitle("Test2");
		Assert.isTrue(cSer.findCourseInfo(c).getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		// 狀況:存在課程編號
		c.setCourseNumber("TXXX");
		Assert.isTrue(cSer.findCourseInfo(c).getMessage().equals(RtnCode.SUCCESS.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		c.setCourseNumber(null);
		// 狀況:存在課程名稱
		c.setCourseTitle("TestCourse");
		Assert.isTrue(cSer.findCourseInfo(c).getMessage().equals(RtnCode.SUCCESS.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
	}

}

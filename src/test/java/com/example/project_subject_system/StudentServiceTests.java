package com.example.project_subject_system;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

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
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.repository.CourseDao;
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.StudentService;

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 為了可以使用@BeforeAll和@AfterAll
public class StudentServiceTests {

	@Autowired
	private CourseDao cDao;

	@Autowired
	private StudentService sSer;

	@Autowired
	private StudentDao sDao;

	@BeforeEach
	public void beforeEach() {
		// 最初執行，建立測試資料 學生兩筆測試資料，一筆有選課，一筆沒選課 資訊如下
		sDao.saveAll(new ArrayList<>(Arrays.asList(new Student("TXXX0", "Test"),
				new Student("TXXX1", "TXXX1", "CXX1, CXX2", 4), new Student("TXXX2", "TXXX2"))));
		// 課程六筆測試資料，課程皆佔三學分 資訊如下
		cDao.saveAll(new ArrayList<>(
				Arrays.asList(new Course("CXX1", "CXX1", "Monday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3, 0),
						new Course("CXX2", "CXX2", "Tuesday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3, 1),
						new Course("CXX3", "CXX3", "Monday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3),
						new Course("CXX4", "CXX2", "Wednesday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3),
						new Course("CXX5", "CXX5", "Wednesday", LocalTime.of(11, 0), LocalTime.of(14, 0), 3),
						new Course("CXX6", "CXX6", "Thursday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3))));
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除測試資料
		sDao.deleteAllById(new ArrayList<>(Arrays.asList("TXXX0", "TXXX1", "TXXX2")));
		cDao.deleteAllById(new ArrayList<>(Arrays.asList("CXX1", "CXX2", "CXX3", "CXX4", "CXX5", "CXX6")));
	}

	@Test
	public void insertStudentTest() {
		// 狀況:資料已存在
		Assert.isTrue(sDao.insertStudent("TXXX0", "Test") == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:儲存成功
		Assert.isTrue(sDao.insertStudent("TXXXX", "Test") == 1, RtnCode.TEST2_ERROR.getMessage());
		sDao.deleteById("TXXXX");
	}
	
	@Test
	public void findStudentCourseListTest() {
		// 取用資料庫已存在資料
		Assert.isTrue(!sDao.findStudentCourseList("TXXX1").isEmpty(), RtnCode.TEST1_ERROR.getMessage());
		// 無課程狀況
		Assert.isTrue(sDao.findStudentCourseList("TXXX2").isEmpty(), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void findStudentCourseTest() {
		// 取用資料庫已存在資料
		Assert.isTrue(sDao.findStudentCourse("TXXX1", "CXX1").getCourseNumbers().contains("CXX1"),
				RtnCode.TEST1_ERROR.getMessage());
		// 無課程狀況
		Assert.isTrue(sDao.findStudentCourse("TXXX2", "CXX1") == null, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void updateStudentCreditsTest() {
		// 狀況:資料不存在
		Assert.isTrue(sDao.updateStudentCredits("TXXXX", "TestClass", 6) == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:更新成功
		Assert.isTrue(sDao.updateStudentCredits("TXXX0", "TestClass", 6) == 1, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addStudentTest() {
		// 狀況:學生Id為空
		Assert.isTrue(sSer.addStudent(null, "Test").getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:學生姓名為空
		Assert.isTrue(sSer.addStudent("TXXXX", "").getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		// 狀況:已存在學生
		Assert.isTrue(sSer.addStudent("TXXX0", "Test").getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		// 狀況:新增資料
		Assert.isTrue(sSer.addStudent("TXXXX", "Test").getMessage().equals(RtnCode.SUCCESSFUL.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
		sDao.deleteById("TXXXX");
	}

	@Test
	public void deleteStudentTest() {
		// 狀況:輸入為空
		Assert.isTrue(sSer.deleteStudent(null).getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:不存在學生
		Assert.isTrue(sSer.deleteStudent("TXXXX").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		// 狀況:刪除成功
		sDao.save(new Student("TXXXX", "Test"));
		Assert.isTrue(sSer.deleteStudent("TXXXx").getMessage().equals(RtnCode.SUCCESSFUL.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
	}
	
	@Test
	public void selectCourse1Test() {
		// 狀況:輸入為空
		Assert.isTrue(sSer.selectCourse(null, null).getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:學生不存在
		Assert.isTrue(sSer.selectCourse("TXXX3", new ArrayList<>(Arrays.asList("CXX1", "CXXX"))).getMessage()
				.equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:部分課程不存在
		Assert.isTrue(sSer.selectCourse("TXXX1", new ArrayList<>(Arrays.asList("CXX1", "CXXX"))).getMessage()
				.equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		// 狀況:選課人數已滿
		Assert.isTrue(sSer.selectCourse("TXXX2", new ArrayList<>(Arrays.asList("CXX1", "CXX2"))).getMessage()
				.equals(RtnCode.FULLY_SELECTED.getMessage()), RtnCode.TEST4_ERROR.getMessage());
		// 狀況:課名相同
		Assert.isTrue(sSer.selectCourse("TXXX2", new ArrayList<>(Arrays.asList("CXX2", "CXX4"))).getMessage()
				.equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST5_ERROR.getMessage());
		// 狀況:衝堂
		Assert.isTrue(sSer.selectCourse("TXXX2", new ArrayList<>(Arrays.asList("CXX4", "CXX5"))).getMessage()
				.equals(RtnCode.CONFLICT.getMessage()), RtnCode.TEST6_ERROR.getMessage());
		// 超過學分上限
		Assert.isTrue(sSer.selectCourse("TXXX2", new ArrayList<>(Arrays.asList("CXX2", "CXX3", "CXX5", "CXX6")))
				.getMessage().equals(RtnCode.OUT_OF_LIMIT.getMessage()), RtnCode.TEST7_ERROR.getMessage());
	}

	@Test
	public void selectCourses2Test() {
		// 狀況:超選學分
		Assert.isTrue(sSer.selectCourse("TXXX1", new ArrayList<>(Arrays.asList("CXX5", "CXX6"))).getMessage()
				.equals(RtnCode.OUT_OF_LIMIT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課名相同
		Assert.isTrue(sSer.selectCourse("TXXX1", new ArrayList<>(Arrays.asList("CXX4"))).getMessage()
				.equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:衝堂
		Assert.isTrue(sSer.selectCourse("TXXX1", new ArrayList<>(Arrays.asList("CXX3"))).getMessage()
				.equals(RtnCode.CONFLICT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		// 狀況:已存在課程
		Assert.isTrue(sSer.selectCourse("TXXX1", new ArrayList<>(Arrays.asList("CXX5"))).getMessage()
				.equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST4_ERROR.getMessage());
		// 狀況:未存在課程
		Assert.isTrue(sSer.selectCourse("TXXX2", new ArrayList<>(Arrays.asList("CXX3", "CXX4"))).getMessage()
				.equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST5_ERROR.getMessage());
	}

	@Test
	public void withdrawCourseTest() {
		// 狀況:輸入為空
		Assert.isTrue(sSer.dropCourse(null, null).getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:無此學生
		Assert.isTrue(sSer.dropCourse("TXXXX", "CXX1").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		// 狀況:無此課程
		Assert.isTrue(sSer.dropCourse("TXXX1", "CXXX").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		// 狀況:學生無此課程
		Assert.isTrue(sSer.dropCourse("TXXX1", "CXX3").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
		// 狀況:成功退課
		Assert.isTrue(sSer.dropCourse("TXXX1", "CXX1").getMessage().equals(RtnCode.SUCCESSFUL.getMessage()),
				RtnCode.TEST5_ERROR.getMessage());
	}

	@Test
	public void courseScheduleTest() {
		// 狀況:輸入為空
		Assert.isTrue(sSer.courseSchedule(null).getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST1_ERROR.getMessage());
		// 狀況:不存在學生資料
		Assert.isTrue(sSer.courseSchedule("TXXXX").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST2_ERROR.getMessage());
		// 狀況:不存在學生課表
		Assert.isTrue(sSer.courseSchedule("TXXX2").getMessage().equals(RtnCode.NOT_FOUND.getMessage()),
				RtnCode.TEST3_ERROR.getMessage());
		// 狀況:成功
		Assert.isTrue(sSer.courseSchedule("TXXX1").getMessage().equals(RtnCode.SUCCESS.getMessage()),
				RtnCode.TEST4_ERROR.getMessage());
	}
}

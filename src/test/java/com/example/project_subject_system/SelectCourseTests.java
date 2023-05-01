package com.example.project_subject_system;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.service.ifs.CourseService;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;
import com.example.projct_course_selection_system.vo.StudentCourseTable;

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 為了可以使用@BeforeAll和@AfterAll
public class SelectCourseTests {

	@Autowired
	private StudentDao sDao;

	@Autowired
	private CourseDao cDao;

	@Autowired
	private CourseSelection cSele;

	@BeforeEach
	public void beforeEach() {
		// 最初執行，建立測試資料 學生兩筆測試資料，一筆(s1)有選課，一筆(s2)沒選課 資訊如下
		Student s1 = new Student("TXXX1", "TXXX1");
		s1.setCourseNumber("CXX1, CXX2");
		s1.setCreditsLimit(4);
		Student s2 = new Student("TXXX2", "TXXX2");
		List<Student> sList = new ArrayList<>(Arrays.asList(s1, s2));

		// 課程五筆測試資料，課程皆佔三學分 資訊如下
		Course c1 = new Course("CXX1", "CXX1", "Monday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		c1.setPersonlimit(0);
		Course c2 = new Course("CXX2", "CXX2", "Tuesday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		c2.setPersonlimit(1);
		Course c3 = new Course("CXX3", "CXX3", "Monday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		Course c4 = new Course("CXX4", "CXX2", "Wednesday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		Course c5 = new Course("CXX5", "CXX5", "Wednesday", LocalTime.of(11, 0), LocalTime.of(14, 0), 3);
		Course c6 = new Course("CXX6", "CXX6", "Thursday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		List<Course> cList = new ArrayList<>(Arrays.asList(c1, c2, c3, c4, c5, c6));
		// 存進資料庫
		sDao.saveAll(sList);
		cDao.saveAll(cList);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除測試資料
		List<String> sList = new ArrayList<>(Arrays.asList("TXXX1", "TXXX2"));
		List<String> cList = new ArrayList<>(Arrays.asList("CXX1", "CXX2", "CXX3", "CXX4", "CXX5", "CXX6"));
		sDao.deleteAllById(sList);
		cDao.deleteAllById(cList);
	}

	@Test
	public void findStudentCourseListTest() {
		// 取用資料庫已存在資料
		List<StudentCourseTable> res1 = sDao.findStudentCourseList("TXXX1");
		List<String> courseNumber = new ArrayList<>();
		for (StudentCourseTable sCT : res1) {
			courseNumber.add(sCT.getCourseNumber());
		}
		Assert.isTrue(courseNumber.contains("CXX1"), RtnCode.TEST1_ERROR.getMessage());
		// 無課程狀況
		List<StudentCourseTable> res2 = sDao.findStudentCourseList("TXXX2");
		Assert.isTrue(res2.isEmpty(), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void selectCourseEmptyTest() {
		// 狀況:輸入為空
		Response res1 = cSele.selectCourse(null, null);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void selectCourseNotFoundTest() {
		// 狀況:學生不存在
		List<String> cList = new ArrayList<>(Arrays.asList("CXX1", "CXXX"));
		Response res1 = cSele.selectCourse("TXXX3", cList);
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:部分課程不存在
		Response res2 = cSele.selectCourse("TXXX1", cList);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void selectCourseCheckTest() {
		// 狀況:選課人數已滿
		List<String> cList1 = new ArrayList<>(Arrays.asList("CXX1", "CXX2"));
		Response res1 = cSele.selectCourse("TXXX2", cList1);
		Assert.isTrue(res1.getMessage().equals(RtnCode.FULLY_SELECTED.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課名相同
		List<String> cList2 = new ArrayList<>(Arrays.asList("CXX2", "CXX4"));
		Response res2 = cSele.selectCourse("TXXX2", cList2);
		Assert.isTrue(res2.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:衝堂
		List<String> cList3 = new ArrayList<>(Arrays.asList("CXX4", "CXX5"));
		Response res3 = cSele.selectCourse("TXXX2", cList3);
		Assert.isTrue(res3.getMessage().equals(RtnCode.CONFLICT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		// 超過學分上限
		List<String> cList4 = new ArrayList<>(Arrays.asList("CXX2", "CXX3", "CXX5", "CXX6"));
		Response res4 = cSele.selectCourse("TXXX2", cList4);
		Assert.isTrue(res4.getMessage().equals(RtnCode.OUT_OF_LIMIT.getMessage()), RtnCode.TEST4_ERROR.getMessage());
	}

	@Test
	public void selectCoursesCheckTest() {
		// 狀況:超選學分
		List<String> cList1 = new ArrayList<>(Arrays.asList("CXX5", "CXX6"));
		Response res1 = cSele.selectCourse("TXXX1", cList1);
		Assert.isTrue(res1.getMessage().equals(RtnCode.OUT_OF_LIMIT.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:課名相同
		List<String> cList2 = new ArrayList<>(Arrays.asList("CXX4"));
		Response res2 = cSele.selectCourse("TXXX1", cList2);
		Assert.isTrue(res2.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:衝堂
		List<String> cList3 = new ArrayList<>(Arrays.asList("CXX3"));
		Response res3 = cSele.selectCourse("TXXX1", cList3);
		Assert.isTrue(res3.getMessage().equals(RtnCode.INCORRECT.getMessage()), RtnCode.TEST3_ERROR.getMessage());
	}

	@Test
	public void selectCourseSuccessTest() {
		// 狀況:已存在課程
		List<String> cList1 = new ArrayList<>(Arrays.asList("CXX5"));
		Response res1 = cSele.selectCourse("TXXX1", cList1);
		Assert.isTrue(res1.getStudent().getCourseNumber().contains("CXX5"), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:未存在課程
		List<String> cList2 = new ArrayList<>(Arrays.asList("CXX3", "CXX4"));
		Response res2 = cSele.selectCourse("TXXX2", cList2);
		Assert.isTrue(res2.getStudent().getCreditsLimit() == 4, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void withdrawCourseEmptyTest() {
		// 狀況:輸入為空
		Response res1 = cSele.withdrawCourse(null, null);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void withdrawCourseNotFoundTest() {
		// 狀況:無此學生
		Response res1 = cSele.withdrawCourse("TXXXX", "CXX1");
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:無此課程
		Response res2 = cSele.withdrawCourse("TXXX1", "CXXX");
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:學生無此課程
		Response res3 = cSele.withdrawCourse("TXXX1", "CXX3");
		Assert.isTrue(res3.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST3_ERROR.getMessage());
	}
	
	@Test
	public void withdrawCourseSuccessTest() {
		// 狀況:成功退課
		Response res1 = cSele.withdrawCourse("TXXX1", "CXX1");
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}


	@Test
	public void courseScheduleEmptyTest() {
		// 狀況:輸入為空
		Response res1 = cSele.courseSchedule(null);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void courseScheduleNotFoundTest() {
		// 狀況:不存在學生資料
		String studentID = "TXXXX";
		Response res1 = cSele.courseSchedule(studentID);
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:不存在學生課表
		studentID = "TXXX2";
		Response res2 = cSele.courseSchedule(studentID);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void courseScheduleSuccessTest() {
		// 狀況:成功
		String studentID = "TXXX1";
		Response res1 = cSele.courseSchedule(studentID);
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
}

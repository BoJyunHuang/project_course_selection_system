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

	@BeforeAll
	public void beforeAll() {
		// 最初執行，建立測試資料 學生兩筆測試資料，一筆(s1)有選課，一筆(s2)沒選課 資訊如下
		Student s1 = new Student("TXXX1", "TXXX1");
		s1.setCourseNumber("[CXX1,CXX2]");
		s1.setCreditsLimit(4);
		Student s2 = new Student("TXXX2", "TXXX2");
		List<Student> sList = new ArrayList<>(Arrays.asList(s1, s2));

		// 課程四筆測試資料，課程皆佔三學分 資訊如下
		Course c1 = new Course("CXX1", "CXX1", "Monday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		c1.setPersonlimit(0);
		Course c2 = new Course("CXX2", "CXX2", "Tuesday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		c1.setPersonlimit(1);
		Course c3 = new Course("CXX3", "CXX3", "Monday", LocalTime.of(11, 0), LocalTime.of(14, 0), 3);
		Course c4 = new Course("CXX4", "CXX1", "Wednesday", LocalTime.of(9, 0), LocalTime.of(12, 0), 3);
		List<Course> cList = new ArrayList<>(Arrays.asList(c1, c2, c3, c4));
		// 存進資料庫
		sDao.saveAll(sList);
		cDao.saveAll(cList);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除測試資料
		List<String> sList = new ArrayList<>(Arrays.asList("TXXX1", "TXXX2"));
		List<String> cList = new ArrayList<>(Arrays.asList("CXX1", "CXX2", "CXX3", "CXX4"));
		sDao.deleteAllById(sList);
		cDao.deleteAllById(cList);
	}

	@AfterEach
	public void afterEach() {
		// 各單元執行後執行
	}

	@Test
	public void findStudentCourseListTest() {
		// 取用資料庫已存在資料
		List<StudentCourseTable> res = sDao.findStudentCourseList("TXXX1");
		List<String> courseNumber = new ArrayList<>();
		for (StudentCourseTable sCT : res) {
			courseNumber.add(sCT.getCourseNumber());
		}
		Assert.isTrue(courseNumber.contains("CXX1"), RtnCode.TEST1_ERROR.getMessage());
		List<StudentCourseTable> res2 = sDao.findStudentCourseList("TXXX2");
	}

	@Test
	public void findStudentCourseListEmptyTest() {
		// 課程為null。
		List<StudentCourseTable> res = sDao.findStudentCourseList("S0004");
		for (StudentCourseTable r : res) {
			System.out.println(r.getCourseNumber());
		}
	}

	@Test
	public void courseScheduleEmptyTest() {
		String studentID = null;
		Response res1 = cSele.courseSchedule(studentID);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void courseScheduleNotFoundTest() {
		String studentID = "TXXXX";
		Response res1 = cSele.courseSchedule(studentID);
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		studentID = "S0004";
		Response res2 = cSele.courseSchedule(studentID);
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void courseScheduleSuccessTest() {
		String studentID = "S0001";
		Response res1 = cSele.courseSchedule(studentID);
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
}

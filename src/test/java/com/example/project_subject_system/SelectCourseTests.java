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
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.service.ifs.CourseService;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;
import com.example.projct_course_selection_system.vo.StudentCourseTable;

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 為了可以使用@BeforeAll和@AfterAll
class SelectCourseTests {

	@BeforeAll
	public void beforeAll() { // 最初執行
		System.out.println("===== before_all ====");
	}

	@AfterAll
	public void afterAll() { // 最後執行
		System.out.println("===== after_all ====");
	}

	@BeforeEach
	public void beforeEach() { // 各單元執行前執行
		System.out.println("===== before_each ====");
	}

	@AfterEach
	public void afterEach() { // 各單元執行後執行
		System.out.println("===== after_each ====");
	}

	@Autowired
	private StudentDao sDao;

	@Autowired
	private StudentService sSer;

	@Autowired
	private CourseSelection cSele;

	@Test
	public void findStudentCourseListTest() {
		/*
		 * 取用資料庫已存在資料，學號"S0001"。 此筆資料做為測試用，不做更改與刪除。 其課程為A001、W003。
		 */
		List<StudentCourseTable> res = sDao.findStudentCourseList("S0001");
		for (StudentCourseTable r : res) {
			System.out.println(r.getCourseNumber());
		}

	}
	
	@Test
	public void findStudentCourseListEmptyTest() {
		/*
		 * 取用資料庫已存在資料，學號"S0004"。 此筆資料做為測試用，不做更改與刪除。 其課程為A001、W003。
		 */
		List<StudentCourseTable> res = sDao.findStudentCourseList("S0004");
		for (StudentCourseTable r : res) {
			System.out.println(r.getCourseNumber());
		}

	}

}

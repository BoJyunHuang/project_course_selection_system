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
import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.repository.StudentDao;
import com.example.projct_course_selection_system.service.ifs.CourseSelection;
import com.example.projct_course_selection_system.service.ifs.CourseService;
import com.example.projct_course_selection_system.service.ifs.StudentService;
import com.example.projct_course_selection_system.vo.Request;
import com.example.projct_course_selection_system.vo.Response;

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 為了可以使用@BeforeAll和@AfterAll
public class StudentServiceTests {

	@Autowired
	private StudentService sSer;

	@Autowired
	private StudentDao sDao;

	@BeforeEach
	public void beforeEach() {
		// 各單元執行前執行，預設資料內容
		Student student = newStudentData();
		sDao.save(student);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		Student student = newStudentData();
		sDao.deleteById(student.getStudentID());
	}

	@Test
	public void addStudentEmptyTest() {
		// 帶入生成資料
		Student s = newStudentData();
		// 狀況:學生Id為空
		s.setStudentID(null);
		Response res1 = sSer.addStudent(s.getStudentID(), s.getName());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:學生姓名為空
		s.setName("");
		Response res2 = sSer.addStudent(s.getStudentID(), s.getName());
		Assert.isTrue(res2.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addStudentAlreadyExistTest() {
		// 帶入生成資料
		Student s = newStudentData();
		// 狀況:已存在學生
		Response res1 = sSer.addStudent(s.getStudentID(), s.getName());
		Assert.isTrue(res1.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void addStudentSuccessTest() {
		// 帶入生成資料
		Student s = newStudentData();
		// 狀況:新增資料
		s.setStudentID("TXXX1");
		Response res1 = sSer.addStudent(s.getStudentID(), s.getName());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		sDao.deleteById("TXXX1");
	}

	@Test
	public void deleteStudentEmptyTest() {
		// 帶入生成資料
		Student s = newStudentData();
		// 狀況:學生Id為空
		s.setStudentID(null);
		Response res1 = sSer.deleteStudent(s.getStudentID());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteStudentNotFoundTest() {
		// 帶入生成資料
		Student s = newStudentData();
		// 狀況:不存在學生
		s.setStudentID("TXXX2");
		Response res1 = sSer.deleteStudent(s.getStudentID());
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteStudentSuccessTest() {
		// 帶入生成資料
		Student s = newStudentData();
		// 狀況:刪除成功
		s.setStudentID("TXXX1");
		sDao.save(s);
		Response res1 = sSer.deleteStudent(s.getStudentID());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	private Student newStudentData() {
		// 生成測試資料
		Student student = new Student("TXXXX", "Test");
		return student;
	}
}

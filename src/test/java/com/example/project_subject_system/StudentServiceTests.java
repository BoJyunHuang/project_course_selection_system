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
class StudentServiceTests {

	@Autowired
	private StudentService sSer;

	@Autowired
	private StudentDao sDao;

	@BeforeAll
	public void beforeAll() {
		// 最初執行，建立假資料
		Student newData = resetData();
		sDao.save(newData);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		Request r = newStudentData();
		sDao.deleteById(r.getStudentID());
	}

	@AfterEach
	public void afterEach() { 
		// 各單元執行後執行，恢復預設資料內容
		Student newData = resetData();
		sDao.save(newData);
	}
	
	@Test
	public void findStudentCoursesByCourseListTest() {
		
	}
	

	@Test
	public void addStudentEmptyTest() {
		// 帶入生成資料
		Request r = newStudentData();
		// 狀況:學生Id為空
		r.setStudentID(null);
		Response res1 = sSer.addStudent(r.getStudentID(), r.getStudentName());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:學生姓名為空
		r.setStudentName("");
		Response res2 = sSer.addStudent(r.getStudentID(), r.getStudentName());
		Assert.isTrue(res2.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addStudentAlreadyExistTest() {
		// 帶入生成資料
		Request r = newStudentData();
		// 狀況:已存在學生
		Response res1 = sSer.addStudent(r.getStudentID(), r.getStudentName());
		Assert.isTrue(res1.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
	
	@Test
	public void addStudentSuccessTest() {
		// 帶入生成資料
		Request r = newStudentData();
		// 狀況:新增資料
		r.setStudentID("TXX1");
		Response res1 = sSer.addStudent(r.getStudentID(), r.getStudentName());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESS.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		sDao.deleteById("TXX1");
	}
	
	@Test
	public void deleteStudentEmptyTest() {
		// 帶入生成資料
		Request r = newStudentData();
		// 狀況:學生Id為空
		r.setStudentID(null);
		Response res1 = sSer.deleteStudent(r.getStudentID());
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	@Test
	public void deleteStudentNotFoundTest() {
		// 帶入生成資料
		Request r = newStudentData();
		// 狀況:不存在學生
		r.setStudentID("TXX2");
		Response res1 = sSer.deleteStudent(r.getStudentID());
		Assert.isTrue(res1.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}
	
	@Test
	public void deleteStudentSuccessTest() {
		// 帶入生成資料
		Request r = newStudentData();
		// 狀況:新增資料
		Response res1 = sSer.deleteStudent(r.getStudentID());
		Assert.isTrue(res1.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST1_ERROR.getMessage());
	}

	private Request newStudentData() {
		// 生成測試資料
		Request request = new Request();
		request.setStudentID("TXXXX");
		request.setStudentName("Test");
		return request;
	}

	private Student resetData() {
		Request r = newStudentData();
		Student student = new Student(r.getStudentID(), r.getStudentName());
		return student;
	}

//	@BeforeEach
//	public void beforeEach() { // 各單元執行前執行
//		System.out.println("===== before_each ====");
//	}

}

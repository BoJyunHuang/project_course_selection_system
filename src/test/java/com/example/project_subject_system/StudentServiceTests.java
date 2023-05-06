package com.example.project_subject_system;

import org.junit.jupiter.api.AfterAll;
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
import com.example.projct_course_selection_system.service.ifs.StudentService;
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
		Student student = new Student("TXXXX", "Test");
		sDao.save(student);
	}

	@AfterAll
	public void afterAll() {
		// 最後執行，刪除假資料
		sDao.deleteById("TXXXX");
	}

	@Test
	public void insertStudentTest() {
		// 狀況:資料已存在
		int res1 = sDao.insertStudent("TXXXX", "Test");
		Assert.isTrue(res1 == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:儲存成功
		int res2 = sDao.insertStudent("TXXX1", "Test");
		Assert.isTrue(res2 == 1, RtnCode.TEST2_ERROR.getMessage());
		sDao.deleteById("TXXX1");
	}
	
	@Test
	public void reviseStudentCreditsTest() {
		// 狀況:資料不存在
		int res1 = sDao.reviseStudentCredits("TXXX1", "TestClass", 6);
		Assert.isTrue(res1 == 0, RtnCode.TEST1_ERROR.getMessage());
		// 狀況:儲存成功
		int res2 = sDao.reviseStudentCredits("TXXXX", "TestClass", 6);
		Assert.isTrue(res2 == 1, RtnCode.TEST2_ERROR.getMessage());
	}

	@Test
	public void addStudentTest() {
		// 狀況:學生Id為空
		Response res1 = sSer.addStudent(null, "Test");
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:學生姓名為空
		Response res2 = sSer.addStudent("TXXXX", "");
		Assert.isTrue(res2.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:已存在學生
		Response res3 = sSer.addStudent("TXXXX", "Test");
		Assert.isTrue(res3.getMessage().equals(RtnCode.ALREADY_EXISTED.getMessage()), RtnCode.TEST3_ERROR.getMessage());
		// 狀況:新增資料
		Response res4 = sSer.addStudent("TXXX1", "Test");
		Assert.isTrue(res4.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST4_ERROR.getMessage());
		sDao.deleteById("TXXX1");
	}

	@Test
	public void deleteStudentTest() {
		// 狀況:輸入為空
		Response res1 = sSer.deleteStudent(null);
		Assert.isTrue(res1.getMessage().equals(RtnCode.CANNOT_EMPTY.getMessage()), RtnCode.TEST1_ERROR.getMessage());
		// 狀況:不存在學生
		Response res2 = sSer.deleteStudent("TXXX2");
		Assert.isTrue(res2.getMessage().equals(RtnCode.NOT_FOUND.getMessage()), RtnCode.TEST2_ERROR.getMessage());
		// 狀況:刪除成功
		Student s = new Student("TXXX1", "Test");
		sDao.save(s);
		Response res3 = sSer.deleteStudent("TXXX1");
		Assert.isTrue(res3.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), RtnCode.TEST3_ERROR.getMessage());
	}
}

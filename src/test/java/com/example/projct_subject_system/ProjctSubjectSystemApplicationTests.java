package com.example.projct_subject_system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.projct_course_selection_system.ProjectCourseSelectionApplication;
import com.example.projct_course_selection_system.service.ifs.CourseSelection;

@SpringBootTest(classes = ProjectCourseSelectionApplication.class)
class ProjctSubjectSystemApplicationTests {
	
	@Autowired
	private CourseSelection s;

	@Test
	void selectCourseTest() {
		String name = "S0001";
		List<String> list = new ArrayList<>(Arrays.asList("W003"));
		s.selectCourse(name, list);
	}

}

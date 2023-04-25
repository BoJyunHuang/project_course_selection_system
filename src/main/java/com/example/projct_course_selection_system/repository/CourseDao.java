package com.example.projct_course_selection_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projct_course_selection_system.entity.Course;

@Repository
public interface CourseDao extends JpaRepository<Course, String>{

	// 課程名稱查詢
	public List<Course> findByCourseTitle(String courseTitle);
}

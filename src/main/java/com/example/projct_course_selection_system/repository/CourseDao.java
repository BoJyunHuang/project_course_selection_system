package com.example.projct_course_selection_system.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.projct_course_selection_system.entity.Course;

@Repository
public interface CourseDao extends JpaRepository<Course, String> {

	// 課程名稱查詢
	public List<Course> findByCourseTitle(String courseTitle);

	// 更新課程資訊-更改課名
	@Transactional
	@Modifying
	@Query("update Course c set c.courseTitle = :title where c.courseNumber = :id")
	public int updateCourseTitleById(
			@Param("id") String courseNumber, 
			@Param("title") String courseTitle);

	// 更新課程資訊-更改日期
	@Transactional
	@Modifying
	@Query("update Course c set c.schedule = :schedule where c.courseNumber = :id")
	public int updateCourseScheduleById(
			@Param("id") String courseNumber, 
			@Param("schedule") String courseSchedule);
	
	// 更新課程資訊-更改時間
	@Transactional
	@Modifying
	@Query("update Course c set c.startTime = :startTime, c.endTime = :endTime where c.courseNumber = :id")
	public int updateCourseTimeById(
			@Param("id") String courseNumber, 
			@Param("startTime") LocalTime startTime, 
			@Param("endTime") LocalTime endTime);

	// 更新課程資訊-更改學分
	@Transactional
	@Modifying
	@Query("update Course c set c.credits = :credits where c.courseNumber = :id")
	public int updateCourseCreditsById(
			@Param("id") String courseNumber, 
			@Param("credits") int credits);
}

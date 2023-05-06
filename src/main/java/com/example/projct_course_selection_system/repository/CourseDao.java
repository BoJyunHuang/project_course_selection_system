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

	// 新增課程
	@Transactional
	@Modifying
	@Query(value = "insert into course (course_number, course_title, schedule, start_time, end_time, credits, person_limit) "
			+ "select :courseNumber, :courseTitle, :inputSchedule, :inputStartTime, :inputEndTime , :inputCredits, 3 "
			+ "where not exists (select 1 from course where course_number = :courseNumber)", nativeQuery = true)
	public int insertCourse(@Param("courseNumber") String courseNumber, @Param("courseTitle") String courseTitle,
			@Param("inputSchedule") String schedule, @Param("inputStartTime") LocalTime startTime,
			@Param("inputEndTime") LocalTime endTime, @Param("inputCredits") int credits);

	// 刪除課程
	@Transactional
	@Modifying
	@Query("delete from Course c where c.courseNumber = :number and c.personLimit = 3")
	public int deleteCourse(@Param("number") String courseNumber);

	// 由課程編號或課程名稱尋找課程資訊
	@Query("select c.courseNumber, c.courseTitle, c.schedule, c.startTime, c.endTime, c.credits, c.personLimit "
			+ "from Course c where c.courseNumber = :number or c.courseTitle = :title")
	public List<Course> findByNumberOrTitle(@Param("number") String courseNumber, @Param("title") String courseTitle);

	// 更新修課人數
	@Transactional
	@Modifying
	@Query("update Course c set c.personLimit = :personLimit where c.courseNumber = :courseNumber")
	public int reviseCoursePerson(@Param("courseNumber") String courseNumber,@Param("personLimit") int personLimit);
}

package com.example.projct_course_selection_system.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.projct_course_selection_system.entity.Student;
import com.example.projct_course_selection_system.vo.StudentCourseTable;

@Repository
public interface StudentDao extends JpaRepository<Student, String> {

	// 依照學生ID找出學生修習課程
	@Transactional
	@Modifying
	@Query("select new com.example.projct_course_selection_system.vo.StudentCourseTable(c.courseNumber, c.courseTitle, "
			+ "c.schedule, c.startTime,	c.endTime, c.credits, c.personlimit, s.studentID, s.name, s.creditsLimit) "
			+ "from Course c join Student s on s.courseNumber like concat('%' , c.courseNumber , '%') "
			+ "where s.studentID = :studentID")
	public List<StudentCourseTable> findStudentCourseList(@Param("studentID")String studentID);
	
}

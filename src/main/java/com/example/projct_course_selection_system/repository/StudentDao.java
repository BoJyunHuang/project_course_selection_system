package com.example.projct_course_selection_system.repository;

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

	// 新增學生
	@Transactional
	@Modifying
	@Query(value = "insert into student (student_ID, name, course_numbers, credits_limit) select :inputID, :inputName,"
			+ " null, 10 where not exists (select 1 from student where student_ID = :inputID)", nativeQuery = true)
	public int insertStudent(@Param("inputID") String studentID, @Param("inputName") String name);

	// 依照學生ID找出學生修習課程
	@Query("select new com.example.projct_course_selection_system.vo.StudentCourseTable "
			+ "(c.courseNumber, s.courseNumbers, c.courseTitle, c.schedule, c.startTime, c.endTime, c.credits, "
			+ "c.personLimit, s.studentID, s.name, s.creditsLimit) "
			+ "from Course c join Student s on s.courseNumbers like concat('%' , c.courseNumber , '%') "
			+ "where s.studentID = :studentID")
	public List<StudentCourseTable> findStudentCourseList(@Param("studentID") String studentID);

	// 依照學生ID及課程資訊尋找是否有資料
	@Query("select new com.example.projct_course_selection_system.vo.StudentCourseTable"
			+ "(c.courseNumber, s.courseNumbers, c.courseTitle, c.schedule, c.startTime, c.endTime, c.credits, "
			+ "c.personLimit, s.studentID, s.name, s.creditsLimit) "
			+ "from Course c join Student s on s.courseNumbers like concat('%' , c.courseNumber, '%') "
			+ "where s.studentID = :studentID and c.courseNumber = :courseNumber")
	public StudentCourseTable findStudentCourse(@Param("studentID") String studentID,
			@Param("courseNumber") String courseNumber);

	// 更新學生課表及學分
	@Transactional
	@Modifying
	@Query("update Student s set s.courseNumbers = :numbers, s.creditsLimit = :limit where s.studentID = :ID")
	public int updateStudentCredits(@Param("ID") String studentID, @Param("numbers") String courseNumbers,
			@Param("limit") int creditsLimit);

}

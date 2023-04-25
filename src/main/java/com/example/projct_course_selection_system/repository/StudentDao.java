package com.example.projct_course_selection_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.projct_course_selection_system.entity.Student;

@Repository
public interface StudentDao extends JpaRepository<Student, String>{

}

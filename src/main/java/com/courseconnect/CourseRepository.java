package com.courseconnect;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT c FROM Course c WHERE SIZE(c.students) = 0")
    List<Course> findCoursesWithNoStudents();

    @Query("SELECT c FROM Course c WHERE c.instructor = ?1")
    List<Course> findByInstructor(String instructor);
}

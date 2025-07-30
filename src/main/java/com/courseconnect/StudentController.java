package com.courseconnect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentService studentService;

    // POST /students – Add a new student
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        try {
            if (studentRepository.existsByEmail(student.getEmail())) {
                return ResponseEntity.badRequest().build();
            }
            Student savedStudent = studentRepository.save(student);
            System.out.println("Received: " + student.getStudentId() + ", " + student.getName() + ", " + student.getEmail());
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /students – Get all students
    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // GET /students/{id} – Get a student by ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /students/{id} – Update a student
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        try {
            Student updatedStudent = studentService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /students/{id} – Delete a student
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /students/{id}/courses – Enroll a student in a course
    @PostMapping("/{studentId}/courses")
    public ResponseEntity<Student> enrollCourse(@PathVariable String studentId, @RequestBody Course courseRequest) {
        try {
            Student student = studentRepository.findById(studentId).orElseThrow();
            Course course = courseRepository.findById(courseRequest.getId()).orElseThrow();

            if (!student.getCourses().contains(course)) {
                student.getCourses().add(course);
                studentRepository.save(student);
            }

            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /students/{id}/courses – Get all courses a student is enrolled in
    @GetMapping("/{studentId}/courses")
    public ResponseEntity<List<Course>> getStudentCourses(@PathVariable String studentId) {
        try {
            Student student = studentRepository.findById(studentId).orElseThrow();
            return ResponseEntity.ok(student.getCourses());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /students/{id}/courses/{courseId} – Unenroll a student from a course
    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<Student> unenrollCourse(@PathVariable String studentId, @PathVariable String courseId) {
        try {
            Student student = studentRepository.findById(studentId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();

            student.getCourses().remove(course);
            studentRepository.save(student);

            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /students/no-courses – Get students with no courses
    @GetMapping("/no-courses")
    public List<Student> getStudentsWithNoCourses() {
        return studentRepository.findStudentsWithNoCourses();
    }
}
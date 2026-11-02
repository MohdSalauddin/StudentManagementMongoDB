package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface StudentRepository extends MongoRepository<Student, String> {
    List<Student> findByDepartmentIgnoreCase(String department);

    List<Student> findByPercentageGreaterThanEqualOrderByPercentageDesc(Double percentage);

    List<Student> findByDepartmentIgnoreCaseAndPercentageGreaterThanEqualOrderByPercentageDesc(
            String department, Double percentage);
}

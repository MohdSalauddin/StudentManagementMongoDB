package com.example.studentmanagement.service;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student create(Student student) {
        student.setId(null);
        return repository.save(student);
    }

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Student getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    public Student update(String id, Student input) {
        Student existing = getById(id);
        existing.setName(input.getName());
        existing.setEmail(input.getEmail());
        existing.setDepartment(input.getDepartment());
        existing.setPercentage(input.getPercentage());
        existing.setSemester(input.getSemester());
        return repository.save(existing);
    }

    public void delete(String id) {
        Student existing = getById(id);
        repository.delete(existing);
    }

    public List<Student> byDepartment(String department) {
        return repository.findByDepartmentIgnoreCase(department);
    }

    public List<Student> byMinimumPercentage(Double percentage) {
        return repository.findByPercentageGreaterThanEqualOrderByPercentageDesc(percentage);
    }

    public List<Student> search(String department, Double minPercentage) {
        if (department != null && !department.isBlank() && minPercentage != null) {
            return repository.findByDepartmentIgnoreCaseAndPercentageGreaterThanEqualOrderByPercentageDesc(
                    department, minPercentage);
        }
        if (department != null && !department.isBlank())
            return byDepartment(department);
        if (minPercentage != null)
            return byMinimumPercentage(minPercentage);
        return getAll();
    }
}

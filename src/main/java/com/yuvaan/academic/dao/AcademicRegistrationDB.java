package com.yuvaan.academic.dao;

import com.yuvaan.academic.exception.DBServiceException;
import com.yuvaan.academic.exception.DataNotAvailableException;
import com.yuvaan.academic.rest.domain.Course;
import com.yuvaan.academic.rest.domain.Professor;
import com.yuvaan.academic.rest.domain.Student;

import java.util.List;

public class AcademicRegistrationDB {
    
    MySqlDataSourceManager dataSourceManager = new MySqlDataSourceManager();
    
    public  Course getCourse(final String name) throws DataNotAvailableException {
        return dataSourceManager.getCourse(name);
    }
    
    public  long createCourse(final String name, final String details, final Integer fees) throws DataNotAvailableException, DBServiceException {
        return dataSourceManager.createCourse(name, details, fees);
    }
    
    public List<Course> getAllCourses() throws DBServiceException {
        return dataSourceManager.getAllCourse();
    }
    
    public long createStudent(final String firstName, final String lastName, final String address, final String email) throws DataNotAvailableException,
        DBServiceException {
        return dataSourceManager.createStudent(firstName, lastName, address, email);
    }
    
    public List<Student> getAllStudents() throws DBServiceException{
        return dataSourceManager.getAllStudent();
    }
    
    public  Student getStudent(final Integer studentId) throws DataNotAvailableException {
        return dataSourceManager.getStudent(studentId);
    }
    
    public Integer enrollStudent(final Integer studentId, final Integer courseId) throws DataNotAvailableException, DBServiceException {
        return dataSourceManager.enrollStudent(studentId, courseId);
    }
    
    public Professor getProfessor(Integer professorId) throws DataNotAvailableException, DBServiceException {
        return dataSourceManager.getProfessor(professorId);
    }
    
    public long createProfessor(final String firstName, final String lastName, final String address, final String email, final String designation) throws DataNotAvailableException, DBServiceException {
        return dataSourceManager.createProfessor(firstName, lastName, address, email, designation);
    }
    
    public List<Professor> getAllProfessor() throws DataNotAvailableException, DBServiceException{
        return  dataSourceManager.getAllProfessor();
    }
    
    public Integer assignProfessor(final Integer studentId, final Integer courseId) throws DBServiceException {
        return dataSourceManager.assignProfessor(studentId, courseId);
    }
    
    public Course getCourseByID(Integer courseId) throws DataNotAvailableException {
        return dataSourceManager.getCourseByID(courseId );
    }
}

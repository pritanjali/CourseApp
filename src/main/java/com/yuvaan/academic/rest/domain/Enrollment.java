package com.yuvaan.academic.rest.domain;


public class Enrollment {
    
    Integer studentId;
    
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public Integer getProfessorId() {
        return professorId;
    }
    
    public void setProfessorId(Integer professorId) {
        this.professorId = professorId;
    }
    
    public Integer getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }
    
    Integer professorId;
    Integer courseId;
}

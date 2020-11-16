package com.yuvaan.academic.rest.domain;

import java.util.List;
import java.util.Objects;

public class Course {
    private int id;
    private String courseName;
    private String description;
    private Integer courseFee;
    private List<Student> students;
    private Professor professor;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(final String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getCourseFee() {
        return this.courseFee;
    }

    public void setCourseFee(final Integer courseFee) {
        this.courseFee = courseFee;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(final List<Student> students) {
        this.students = students;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(final Professor professor) {
        this.professor = professor;
    }
    
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Course course = (Course) o;
        return id == course.id &&
            courseName.equals(course.courseName) &&
            description.equals(course.description) &&
            courseFee.equals(course.courseFee) &&
            Objects.equals(students, course.students) &&
            Objects.equals(professor, course.professor);
    }
    
    @Override public int hashCode() {
        return Objects.hash(id, courseName, description, courseFee, students, professor);
    }
}

package com.yuvaan.academic.rest.domain;

import java.sql.Date;
import java.util.Objects;

public class Student {

    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private Course course;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return id.equals(student.id) &&
            firstName.equals(student.firstName) &&
            lastName.equals(student.lastName) &&
            address.equals(student.address) &&
            email.equals(student.email) &&
            Objects.equals(course, student.course);
    }
    
    @Override public int hashCode() {
        return Objects.hash(id, firstName, lastName, address, email, course);
    }
}

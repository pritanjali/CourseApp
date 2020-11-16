package com.yuvaan.academic.rest.domain;

import java.util.Objects;

public class Professor {
    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private Course course;
    private String designation;

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

    public void setId(final Integer id) {
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
    
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Professor professor = (Professor) o;
        return id.equals(professor.id) &&
            firstName.equals(professor.firstName) &&
            lastName.equals(professor.lastName) &&
            address.equals(professor.address) &&
            email.equals(professor.email) &&
            Objects.equals(course, professor.course) &&
            Objects.equals(designation, professor.designation);
    }
    
    @Override public int hashCode() {
        return Objects.hash(id, firstName, lastName, address, email, course, designation);
    }
}

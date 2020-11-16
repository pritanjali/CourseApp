package com.yuvaan.academic.rest.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.yuvaan.academic.dao.AcademicRegistrationDB;
import com.yuvaan.academic.exception.DBServiceException;
import com.yuvaan.academic.exception.DataNotAvailableException;
import com.yuvaan.academic.rest.domain.Course;
import com.yuvaan.academic.rest.domain.Enrollment;
import com.yuvaan.academic.rest.domain.Professor;
import com.yuvaan.academic.rest.domain.ResponseMessage;
import com.yuvaan.academic.rest.domain.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/academic")
@Produces("application/json")
public class AcademicRegistrationResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AcademicRegistrationResource.class);
    AcademicRegistrationDB academicDb = new AcademicRegistrationDB();
    
    /**
     * This REST endpoint is to get all Courses.
     *
     * @return {@link Response} containing {@link List} of {@link Course} or error details.
     */
    @GET
    @Path("/courses")
    public Response getCourses() {
        List<Course> list = null;
        try {
            list = academicDb.getAllCourses();
            return Response.ok(list).build();
        } catch (DBServiceException e) {
            LOGGER.error("Error while retrieving all courses", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage("Error while retrieving all courses")).build();
        }
    }
    
    /**
     * This REST endpoint is to find a course using course name.
     *
     * @param courseName
     *            : {@link String} Name of the course.
     * @return {@link Response} containing {@link Course} or error details.
     */
    @GET
    @Path("/courses/{name}")
    public Response getCourseByName(@PathParam("name") String courseName) {
        Course course = null;
        try {
            course = academicDb.getCourse(courseName);
            if (course == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            return Response.ok(course).build();
        } catch (DataNotAvailableException e) {
            final String message = String.format("Could not find specified course with name: '%s'", courseName);
            LOGGER.error(message);
            return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message)).build();
        }
        
    }
    
    /**
     * This REST endpoint is to create a Course.
     *
     * @param course
     *            : {@link Course}
     * @return {@link Response} containing {@link Course} or error details
     */
    @POST
    @Consumes("application/json")
    @Path("/courses")
    public Response createCourse(Course course) {
        if (course.getCourseName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ResponseMessage("Course name not found"))
                .build();
        }
        try {
            long id = academicDb.createCourse(course.getCourseName(), course.getDescription(), course.getCourseFee());
            final String user_info = String.format("Course is created with '%d'", id);
            LOGGER.info(user_info);
            return Response.status(Response.Status.CREATED).entity(new ResponseMessage(user_info)).build();
        } catch (final DataNotAvailableException | DBServiceException e) {
            final String message = String.format("Not able to create Course with name : '%s'", course.getCourseName());
            LOGGER.error(message);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage(message)).build();
        }
    }
    
    /**
     * This rest is point is to register student to a course.
     *
     * @param enroll
     *            {@link Enrollment} details of student registration.
     * @return {@link Response} containing {@link String} message or error details
     */
    @POST
    @Consumes("application/json")
    @Path("/course/student_enroll/")
    public Response enrollStudent(final Enrollment enroll) {
        //Validate User. Only student ID is required.
        if (enroll.getProfessorId() != null && enroll.getStudentId() != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ResponseMessage("Please enter only student id and course id"))
                .build();
        }
        if (enroll.getCourseId() == null || enroll.getStudentId() == null) {
            final String message = enroll.getCourseId() == null ? String.format("Course ID is null. Please enter valid course ID.")
                : String.format("Student ID is null. Please enter valid Student ID.");
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ResponseMessage(message))
                .build();
        }
        
        try {
            Student student = academicDb.getStudent(enroll.getStudentId());
            if (student == null) {
                final String message = String.format("Error retrieving student with id: '%d'", enroll.getStudentId());
                LOGGER.error(message);
                return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message)).build();
            }
            
            Course course = academicDb.getCourseByID(enroll.getCourseId());
            if (course == null) {
                final String message = String.format("Error retrieving specified course with id: '%d'", enroll.getCourseId());
                LOGGER.error(message);
                return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message)).build();
            }
            
            academicDb.enrollStudent(enroll.getStudentId(), enroll.getCourseId());
            
        } catch (final DataNotAvailableException | DBServiceException e) {
            final String errorMessage = String.format("Failed to enroll Student '%d' to course '%d'", enroll.getStudentId(), enroll.getCourseId());
            LOGGER.error(e.getMessage() + "\n " + errorMessage);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage(e.getMessage() + "\n " + errorMessage)).build();
        }
        
        final String message = String.format("Student '%d' is enrolled to course '%d'", enroll.getStudentId(), enroll.getCourseId());
        LOGGER.info(message);
        return Response.status(Response.Status.OK).entity(new ResponseMessage("Student enrolled successfully")).build();
    }
    
    /**
     * This rest is point is to register Professor to a course.
     *
     * @param enroll
     *            {@link Enrollment} details of student registration.
     * @return {@link Response} containing {@link String} message or error details
     */
    @POST
    @Consumes("application/json")
    @Path("/course/assign_professor/")
    public Response assignProfessor(Enrollment enroll) {
        {
            //Validate Professor. Only ProfessorID and Course ID is required.
            if (enroll.getProfessorId() != null && enroll.getStudentId() != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ResponseMessage("Please enter only professor id and course id"))
                    .build();
            }
            if (enroll.getCourseId() == null || enroll.getProfessorId() == null) {
                final String message = enroll.getCourseId() == null ? String.format("Course ID is null. Please enter valid course ID.")
                    : String.format("Professor ID is null. Please enter valid Professor ID.");
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ResponseMessage(message))
                    .build();
            }
            
            try {
                Professor professor = academicDb.getProfessor(enroll.getProfessorId());
                if (professor == null) {
                    final String message = String.format("Error retrieving professor with id: '%d'", enroll.getProfessorId());
                    LOGGER.error(message);
                    return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message)).build();
                }
                
                Course course = academicDb.getCourseByID(enroll.getCourseId());
                if (course == null) {
                    final String message = String.format("Error retrieving specified course with id: '%d'", enroll.getCourseId());
                    LOGGER.error(message);
                    return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message)).build();
                }
                
                academicDb.assignProfessor(enroll.getProfessorId(), enroll.getCourseId());
                
            } catch (DataNotAvailableException | DBServiceException e) {
                final String errorMessage = String.format("Failed to assign Professor '%d' to course '%d'", enroll.getStudentId(),
                    enroll.getCourseId());
                LOGGER.error(e.getMessage() + "\n " + errorMessage);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage(e.getMessage() + "\n " + errorMessage))
                    .build();
            }
            final String user_info = String.format("Professor id: '%d' is assign to Course id : '%d' ", enroll.getProfessorId(),
                enroll.getCourseId());
            LOGGER.info(user_info);
            return Response.status(Response.Status.OK).entity(new ResponseMessage(user_info)).build();
        }
    }
    
    /**
     * This REST endpoint is to get all Students.
     *
     * @return {@link Response} containing {@link List} of {@link Student} or error details.
     */
    @GET
    @Path("/students")
    public Response getStudents() {
        List<Student> list = null;
        try {
            list = academicDb.getAllStudents();
            return Response.ok(list).build();
        } catch (DBServiceException e) {
            LOGGER.error("Error while retrieving all students", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage("Error while retrieving all students")).build();
        }
    }
    
    /**
     * This REST endpoint is to find a Student using student id.
     *
     * @param studentId
     *            : {@link Integer} ID of the student.
     * @return {@link Response} containing {@link Student} or error details.
     */
    @GET
    @Path("/students/{id}")
    public Response getStudent(@PathParam("id") Integer studentId) {
        Student student = null;
        try {
            student = academicDb.getStudent(studentId);
            if (student == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(student).build();
        } catch (DataNotAvailableException e) {
            final String message = String.format("Could not find specified student with ID: '%d'", studentId);
            LOGGER.error(message + "\n" + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message + "\n" + e.getMessage())).build();
        }
        
    }
    
    /**
     * This REST endpoint is to create Student.
     *
     * @param student
     *            : {@link Student}
     * @return {@link Response} containing {@link Student} or error details
     */
    @POST
    @Consumes("application/json")
    @Path("/students")
    public Response createStudent(final Student student) {
        if (student.getFirstName() == null || student.getLastName() == null || student.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ResponseMessage("Please enter first_name,last_name and email."))
                .build();
        }
        try {
            long id = academicDb.createStudent(student.getFirstName(), student.getLastName(),
                student.getAddress(), student.getEmail());
            final String user_info = String.format("Student is created with '%d'", id);
            LOGGER.info(user_info);
            return Response.status(Response.Status.CREATED).entity(new ResponseMessage(user_info)).build();
        } catch (final DataNotAvailableException | DBServiceException e) {
            final String message = String.format("Not able to create Student with email : '%s'", student.getEmail());
            LOGGER.error(message + "\n" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage(message + "\n" + e.getMessage())).build();
        }
    }
    
    /**
     * This REST endpoint is to find a Professor using professor id.
     *
     * @param professorId
     *            : {@link Integer} ID of the professor.
     * @return {@link Response} containing {@link Professor} or error details.
     */
    @GET
    @Path("/professors/{id}")
    public Response getProfessor(@PathParam("id") final Integer professorId) {
        Professor professor = null;
        try {
            professor = academicDb.getProfessor(professorId);
            if (professor == null) {
                final String user_info = String.format("Professor with '%d' does not exits", professorId);
                LOGGER.error(user_info);
                return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(user_info)).build();
            }
            return Response.ok(professor).build();
        } catch (DBServiceException | DataNotAvailableException e) {
            final String message = String.format("Could not find specified student with ID: '%d'", professorId);
            LOGGER.error(message + "\n" + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(new ResponseMessage(message + "\n" + e.getMessage())).build();
        }
        
    }
    
    /**
     * This REST endpoint is to create Professor.
     *
     * @param professor
     *            : {@link Professor}
     * @return {@link Response} containing {@link Professor} or error details
     */
    @POST
    @Consumes("application/json")
    @Path("/professors")
    public Response createProfessor(final Professor professor) {
        if (professor.getFirstName() == null || professor.getLastName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ResponseMessage("Professor first and last name not found"))
                .build();
        }
        try {
            long id = academicDb.createProfessor(professor.getFirstName(), professor.getLastName(),
                professor.getAddress(), professor.getEmail(), professor.getDesignation());
            final String user_info = String.format("Professor is created with '%d'", id);
            LOGGER.info(user_info);
            return Response.status(Response.Status.CREATED).entity(new ResponseMessage(user_info)).build();
        } catch (final DataNotAvailableException | DBServiceException e) {
            final String message = String.format("Not able to create Professor with email : '%s'", professor.getEmail());
            LOGGER.error(message + "\n" + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage(message + "\n" + e.getMessage())).build();
        }
    }
    
    /**
     * This REST endpoint is to get all Professor.
     *
     * @return {@link Response} containing {@link List} of {@link Professor} or error details.
     */
    @GET
    @Path("/professors")
    public Response getAllProfessor() {
        List<Professor> list = null;
        try {
            list = academicDb.getAllProfessor();
            return Response.ok(list).build();
        } catch (final DBServiceException | DataNotAvailableException e) {
            LOGGER.error("Error while retrieving all professor", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ResponseMessage("Error while retrieving all professor")).build();
        }
    }
    
}
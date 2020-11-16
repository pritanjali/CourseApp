package com.yuvaan.academic.dao;

import com.yuvaan.academic.exception.DBServiceException;
import com.yuvaan.academic.exception.DataNotAvailableException;
import com.yuvaan.academic.rest.domain.Course;
import com.yuvaan.academic.rest.domain.Professor;
import com.yuvaan.academic.rest.domain.Student;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MySqlDataSourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MySqlDataSourceManager.class);

    private static final String COURSE_TABLE_NAME = "course";
    private static final String ID_COLUMN_NAME = "id";
    private static final String COURSE_NAME_COLUMN_NAME = "name";
    private static final String DESCRIPTION_COLUMN_NAME = "description";
    private static final String QUERY_SEPARATOR = ", ";
    private static final String STUDENT_TABLE_NAME = "student";
    private static final String FIRST_NAME_COLUMN_NAME = "first_name";
    private static final String LAST_NAME_COLUMN_NAME = "last_name";
    private static final String ADDRESS_COLUMN_NAME = "address";

    private static final String PROFESSOR_TABLE_NAME = "professor";
    private static final String STUDENT_COURSE_TABLE_NAME = "student_course";
    private static final String PROFESSOR_COURSE_TABLE_NAME = "professor_course";
    private static final String STUDENT_ID = "student_id";
    private static final String PROFESSOR_ID = "professor_id";
    private static final String COURSE_ID = "course_id";

    /**
     * @param courseName
     * @return
     * @throws DataNotAvailableException
     */
    public Course getCourse(final String courseName) throws DataNotAvailableException {
        LOGGER.debug("Retrieving course from database based on course name");
        final String query = String.format("SELECT * FROM %s WHERE %s = ?;", COURSE_TABLE_NAME, COURSE_NAME_COLUMN_NAME);
        Course resultCourse = new Course();
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement statement = createPreparedStatementWithStringParameter(connection, query, courseName);
                final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resultCourse.setCourseName(resultSet.getString(COURSE_NAME_COLUMN_NAME));
                resultCourse.setCourseFee(resultSet.getInt("fees"));
                resultCourse.setId(Integer.parseInt(resultSet.getString(ID_COLUMN_NAME)));
                resultCourse.setDescription(resultSet.getString(DESCRIPTION_COLUMN_NAME));
            }
            if (resultCourse.getCourseName().isEmpty()) {
                throw new DataNotAvailableException(String.format("course with  name %s is not found in the table.", courseName));
            }
        } catch (final SQLException e) {
            LOGGER.warn("Could not retrieve data from db", e);
            throw new DataNotAvailableException("Error retrieving course", e);
        }
        return resultCourse;
    }

    public Course getCourseByID(final Integer courseId) throws DataNotAvailableException {
        LOGGER.debug("Retrieving course from database based on course ID");
        final String query = String.format("SELECT * FROM %s WHERE %s = ?;", COURSE_TABLE_NAME, ID_COLUMN_NAME);
        Course resultCourse = new Course();
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement statement = createPreparedStatementWithStringParameter(connection, query, courseId);
                final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resultCourse.setCourseName(resultSet.getString(COURSE_NAME_COLUMN_NAME));
                resultCourse.setCourseFee(resultSet.getInt("fees"));
                resultCourse.setId(Integer.parseInt(resultSet.getString(ID_COLUMN_NAME)));
                resultCourse.setDescription(resultSet.getString(DESCRIPTION_COLUMN_NAME));
            }
            if (resultCourse.getCourseName().isEmpty()) {
                throw new DataNotAvailableException(String.format("course with  name %d is not found in the table.", courseId));
            }
        } catch (final SQLException e) {
            LOGGER.warn("Could not retrieve data from db", e);
            throw new DataNotAvailableException("Error retrieving course", e);
        }
        return resultCourse;
    }

    /**
     * @param courseName
     * @param details
     * @param course_fee
     * @return
     * @throws DBServiceException
     */
    public long createCourse(final String courseName, final String details, final Integer course_fee)
            throws DBServiceException {
        final StringBuilder query = new StringBuilder().append("INSERT INTO ")
                .append(COURSE_TABLE_NAME)
                .append(" (")
                .append(COURSE_NAME_COLUMN_NAME)
                .append(QUERY_SEPARATOR)
                .append(DESCRIPTION_COLUMN_NAME)
                .append(QUERY_SEPARATOR)
                .append("fees")
                .append(") VALUES (?,?,?)");
        LOGGER.info(query.toString());
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement stmt = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
            addParametersToPreparedStatement(stmt, courseName, details, course_fee);
            stmt.executeUpdate();
            try (final ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new DBServiceException("Error creating course, no id was generated when creating course");
            }
        } catch (final SQLException e) {
            throw new DBServiceException("Error creating course", e);
        }
    }

    /**
     * @return
     * @throws DBServiceException
     */
    public List<Course> getAllCourse()
            throws DBServiceException {
        final List<Course> courseList = new ArrayList<>();
        final String query = String.format("SELECT * FROM %s;", COURSE_TABLE_NAME);
        try (final Connection connection = DBConnectionPool.getConnection();
                final Statement stmt = connection.createStatement();
                final ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                Course resultCourse = new Course();
                resultCourse.setCourseName(resultSet.getString(COURSE_NAME_COLUMN_NAME));
                resultCourse.setCourseFee(resultSet.getInt("fees"));
                resultCourse.setId(Integer.parseInt(resultSet.getString(ID_COLUMN_NAME)));
                resultCourse.setDescription(resultSet.getString(DESCRIPTION_COLUMN_NAME));
                courseList.add(resultCourse);
            }
        } catch (final SQLException e) {
            throw new DBServiceException("Error retrieving all courses", e);
        }
        return courseList;
    }

    /**
     * @param firstName
     * @param lastName
     * @param address
     * @param email
     * @return
     * @throws DBServiceException
     */
    public long createStudent(final String firstName, final String lastName, final String address, final String email) throws DBServiceException {
        if (isEmailAvailable(email, STUDENT_TABLE_NAME)) {
            final StringBuilder query = new StringBuilder().append("INSERT INTO ")
                    .append(STUDENT_TABLE_NAME)
                    .append(" (")
                    .append(FIRST_NAME_COLUMN_NAME)
                    .append(QUERY_SEPARATOR)
                    .append(LAST_NAME_COLUMN_NAME)
                    .append(QUERY_SEPARATOR)
                    .append(ADDRESS_COLUMN_NAME)
                    .append(QUERY_SEPARATOR)
                    .append("email")
                    .append(") VALUES (?,?,?,?) ");
            LOGGER.info(query.toString());
            try (final Connection connection = DBConnectionPool.getConnection();
                    final PreparedStatement stmt = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
                addParametersToPreparedStatement(stmt, firstName, lastName, address, email);
                stmt.executeUpdate();
                try (final ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new DBServiceException("Error creating course, no id was generated when creating student");
                }
            } catch (final SQLException e) {
                throw new DBServiceException("Error creating student", e);
            }
        } else {
            throw new DBServiceException("Error creating student. Student with email already exists.");
        }
    }

    /**
     * @return
     * @throws DBServiceException
     */
    public List<Student> getAllStudent() throws DBServiceException {
        final List<Student> studentList = new ArrayList<>();
        final String query = "SELECT student.id, student.first_name, student.last_name, student.address, student.email,"
                + "course.id AS course_id, course.name AS course_name "
                + "FROM student "
                + "LEFT JOIN student_course on (student.id=student_course.student_id) "
                + "LEFT JOIN course on (student_course.course_id=course.id);";
        try (final Connection connection = DBConnectionPool.getConnection();
                final Statement stmt = connection.createStatement();
                final ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                Student resultStudent = new Student();
                Course resultCourse = new Course();
                resultStudent.setId(Integer.parseInt(resultSet.getString(ID_COLUMN_NAME)));
                resultStudent.setFirstName(resultSet.getString(FIRST_NAME_COLUMN_NAME));
                resultStudent.setFirstName(resultSet.getString(LAST_NAME_COLUMN_NAME));
                resultStudent.setAddress(resultSet.getString(ADDRESS_COLUMN_NAME));
                resultStudent.setEmail(resultSet.getString("email"));
                resultCourse.setId(resultSet.getInt("course_id"));
                resultCourse.setCourseName(resultSet.getString("course_name"));
                resultStudent.setCourse(resultCourse);
                studentList.add(resultStudent);
            }
        } catch (final SQLException e) {
            throw new DBServiceException("Error retrieving all students", e);
        }
        return studentList;

    }

    public Student getStudent(final int studentId) throws DataNotAvailableException {
        LOGGER.debug("Retrieving student from database");
        try {
            final String query = "SELECT student.id, student.first_name, student.last_name, student.address,student.email,"
                    + "course.id AS course_id, course.name AS course_name "
                    + "FROM student "
                    + "LEFT JOIN student_course on (student.id=student_course.student_id) "
                    + "LEFT JOIN course on (student_course.course_id=course.id) "
                    + " where student.id = ?";
            Student resultStudent = new Student();
            Course resultCourse = new Course();
            try (final Connection connection = DBConnectionPool.getConnection();
                    final PreparedStatement statement = createPreparedStatementWithStringParameter(connection, query, studentId);
                    final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    resultStudent.setFirstName(resultSet.getString(FIRST_NAME_COLUMN_NAME));
                    resultStudent.setFirstName(resultSet.getString(LAST_NAME_COLUMN_NAME));
                    resultStudent.setEmail(resultSet.getString("email"));
                    resultStudent.setId(resultSet.getInt(ID_COLUMN_NAME));
                    resultStudent.setAddress(resultSet.getString(ADDRESS_COLUMN_NAME));
                    resultCourse.setId(resultSet.getInt("course_id"));
                    resultCourse.setCourseName(resultSet.getString("course_name"));
                    resultStudent.setCourse(resultCourse);
                }
                if (resultStudent.getId() == null) {
                    throw new DataNotAvailableException(String.format("Student with  id %s is not found in the table.", studentId));
                }
            } catch (final SQLException e) {
                throw new DBServiceException("Error retrieving student", e);
            }
            return resultStudent;
        } catch (final DBServiceException e) {
            LOGGER.warn("Could not retrieve data from db", e);
            throw new DataNotAvailableException("ERROR_GETTING_ student", e);
        }
    }

    /**
     * @param studentId
     * @param courseId
     * @return
     * @throws DBServiceException
     */

    public int enrollStudent(final Integer studentId, final Integer courseId) throws DBServiceException {
        final StringBuilder query = new StringBuilder().append("INSERT INTO ")
                .append(STUDENT_COURSE_TABLE_NAME)
                .append(" (")
                .append(STUDENT_ID)
                .append(QUERY_SEPARATOR)
                .append(COURSE_ID)
                .append(") VALUES (?,?)");
        System.out.println(query.toString());
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement stmt = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
            addParametersToPreparedStatement(stmt, studentId, courseId);
            stmt.executeUpdate();
            return studentId;
        } catch (final SQLException e) {
            throw new DBServiceException("Error enrolling Student to course", e);
        }
    }

    /**
     * @param professorId
     * @param courseId
     * @return
     * @throws DBServiceException
     */
    public int assignProfessor(final Integer professorId, final Integer courseId) throws DBServiceException {
        final StringBuilder query = new StringBuilder().append("INSERT INTO ")
                .append(PROFESSOR_COURSE_TABLE_NAME)
                .append(" (")
                .append(PROFESSOR_ID)
                .append(QUERY_SEPARATOR)
                .append(COURSE_ID)
                .append(") VALUES (?,?)");
        System.out.println(query.toString());
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement stmt = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
            addParametersToPreparedStatement(stmt, professorId, courseId);
            stmt.executeUpdate();
            return professorId;
        } catch (final SQLException e) {
            throw new DBServiceException("Error assigning Professor to course", e);
        }
    }

    /**
     * @param firstName
     * @param lastName
     * @param address
     * @param email
     * @param designation
     * @return
     * @throws DBServiceException
     */
    public long createProfessor(final String firstName, final String lastName, final String address, final String email, final String designation)
            throws DBServiceException {
        if (isEmailAvailable(email, PROFESSOR_TABLE_NAME)) {
            final StringBuilder query = new StringBuilder().append("INSERT INTO ")
                    .append(PROFESSOR_TABLE_NAME)
                    .append(" (")
                    .append(FIRST_NAME_COLUMN_NAME)
                    .append(QUERY_SEPARATOR)
                    .append(LAST_NAME_COLUMN_NAME)
                    .append(QUERY_SEPARATOR)
                    .append(ADDRESS_COLUMN_NAME)
                    .append(QUERY_SEPARATOR)
                    .append("email")
                    .append(QUERY_SEPARATOR)
                    .append("designation")
                    .append(") VALUES (?,?,?,?,?) ");
            LOGGER.info("Create Professor Query : {} ", query.toString());
            try (final Connection connection = DBConnectionPool.getConnection();
                    final PreparedStatement stmt = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
                addParametersToPreparedStatement(stmt, firstName, lastName, address, email, designation);
                stmt.executeUpdate();
                try (final ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new DBServiceException("Error creating professor, no id was generated when creating professor");
                }
            } catch (final SQLException e) {
                throw new DBServiceException("Error creating professor", e);
            }
        } else {
            throw new DBServiceException("Error creating professor. Professor with email already exists.");
        }
    }

    private boolean isEmailAvailable(final String email, final String tablename) throws DBServiceException {
        LOGGER.info("Checking email {} in table {}", email, tablename);
        final String query = String.format("SELECT email FROM %s WHERE email = ?;", tablename);
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement statement = createPreparedStatementWithStringParameter(connection, query, email);
                final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String dbEmail = resultSet.getString("email");
                LOGGER.info(" Retrieved email is : {}", dbEmail);
                return !StringUtils.equals(dbEmail, email);
            }
        } catch (final SQLException e) {
            throw new DBServiceException("Error validating email from database", e);
        }
        return true;

    }

    /**
     * @param professorId
     * @return
     * @throws DataNotAvailableException
     */
    public Professor getProfessor(final Integer professorId) throws DataNotAvailableException, DBServiceException {
        LOGGER.debug("Retrieving professor from database");
        final String query = "SELECT professor.id, professor.first_name, professor.last_name, professor.address, professor.email,"
                + "professor.designation, course.id AS course_id, course.name AS course_name "
                + "FROM professor "
                + "LEFT JOIN professor_course on (professor.id=professor_course.professor_id) "
                + "LEFT JOIN course on (professor_course.course_id=course.id)"
                + "where professor.id =?;";
        Professor resultProfessor = new Professor();
        Course resultCourse = new Course();
        try (final Connection connection = DBConnectionPool.getConnection();
                final PreparedStatement statement = createPreparedStatementWithStringParameter(connection, query, professorId);
                final ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                resultProfessor.setFirstName(resultSet.getString(FIRST_NAME_COLUMN_NAME));
                resultProfessor.setLastName(resultSet.getString(LAST_NAME_COLUMN_NAME));
                resultProfessor.setId(resultSet.getInt(ID_COLUMN_NAME));
                resultProfessor.setAddress(resultSet.getString(ADDRESS_COLUMN_NAME));
                resultProfessor.setDesignation(resultSet.getString("designation"));
                resultCourse.setId(resultSet.getInt("course_id"));
                resultCourse.setCourseName(resultSet.getString("course_name"));
                resultProfessor.setCourse(resultCourse);
            }
            if (resultProfessor.getId() == null) {
                throw new DataNotAvailableException(String.format("Professor with  id %s is not found in the table.", professorId));
            }
        } catch (final SQLException e) {
            throw new DBServiceException("Error retrieving professor", e);
        }
        return resultProfessor;

    }
    
    
    public List<Professor> getAllProfessor() throws DataNotAvailableException, DBServiceException {
        LOGGER.debug("Retrieving professor from database");
        final String query = "SELECT professor.id, professor.first_name, professor.last_name, professor.address, professor.email,"
            + "professor.designation, course.id AS course_id, course.name AS course_name "
            + "FROM professor "
            + "LEFT JOIN professor_course on (professor.id=professor_course.professor_id) "
            + "LEFT JOIN course on (professor_course.course_id=course.id);";
        List<Professor> professorList = new ArrayList<>();
        try (final Connection connection = DBConnectionPool.getConnection();
            final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Professor resultProfessor = new Professor();
                Course resultCourse = new Course();
                resultProfessor.setFirstName(resultSet.getString(FIRST_NAME_COLUMN_NAME));
                resultProfessor.setLastName(resultSet.getString(LAST_NAME_COLUMN_NAME));
                resultProfessor.setId(resultSet.getInt(ID_COLUMN_NAME));
                resultProfessor.setAddress(resultSet.getString(ADDRESS_COLUMN_NAME));
                resultProfessor.setDesignation(resultSet.getString("designation"));
                resultCourse.setId(resultSet.getInt("course_id"));
                resultCourse.setCourseName(resultSet.getString("course_name"));
                resultProfessor.setCourse(resultCourse);
                professorList.add(resultProfessor);
            }
        } catch (final SQLException e) {
            throw new DBServiceException("Error retrieving professor", e);
        }
        return professorList;
        
    }
    
    private static PreparedStatement createPreparedStatementWithStringParameter(final Connection connection, final String preparedSqlQuery,
            final String courseName)
            throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(preparedSqlQuery);
            preparedStatement.setString(1, courseName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Executing SQL statement: '{}'", preparedStatement);
            }
            return preparedStatement;
        } catch (final SQLException e) {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            throw e;
        }
    }

    private static PreparedStatement createPreparedStatementWithStringParameter(final Connection connection, final String preparedSqlQuery,
            final Integer id)
            throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(preparedSqlQuery);
            preparedStatement.setInt(1, id);
            LOGGER.info("Executing SQL statement: '{}'", preparedStatement);
            return preparedStatement;
        } catch (final SQLException e) {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            throw e;
        }
    }

    private static void addParametersToPreparedStatement(final PreparedStatement statement, final Object... columnValues) {
        IntStream.range(0, columnValues.length).forEach(index -> {
            final Object columnValue = columnValues[index];
            final int statementIndex = index + 1;
            try {
                if (columnValue instanceof String) {
                    final String value = (String) columnValue;
                    if (StringUtils.isNumeric(value)) {
                        statement.setLong(statementIndex, Long.parseLong(value));
                    } else {
                        statement.setString(statementIndex, value);
                    }
                } else if (columnValue instanceof Long) {
                    statement.setLong(statementIndex, (Long) columnValue);
                } else if (columnValue instanceof Integer) {
                    statement.setInt(statementIndex, (Integer) columnValue);
                } else if (columnValue instanceof Date) {
                    statement.setDate(statementIndex, (Date) columnValue);
                }
            } catch (final SQLException e) {
                LOGGER.warn("Failed to add parameter to '{}' to the query", columnValue, e);
            }
        });
    }

}

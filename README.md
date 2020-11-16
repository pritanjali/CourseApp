# CourseApp

#About
This project is about a simple API that implements an academic class registration system with REST endpoints.

Features:
Create Courses
Create Professors
Assign Professors to Courses
Create Students
Students register for Courses

Requirements
Docker

This section gives you a quick overview on how to get started.

Build the project using below command from project directory:
mvn clean install

Go to project folder "docker-test" and run the below command:
# starts the `app` and `db` containers
$ docker-compose up --build

Verify container are up by executing delow command:
$ docker ps 

Once both "app" and "db" container are up then able to access below rest endpoints:
 To get all courses: GET http://127.0.0.1:8080/CourseApp/academic/courses 
 To get all students: GET http://127.0.0.1:8080/CourseApp/academic/students
 To get all professors: GET http://127.0.0.1:8080/CourseApp/academic/professors
 
 To get course by name: GET http://127.0.0.1:8080/CourseApp/academic/courses/{name}
 To get student by id: GET  http://127.0.0.1:8080/CourseApp/academic/students/{id}
 To get professor by id: GET http://127.0.0.1:8080/CourseApp/academic/professors/{id}
 
 To create a course: POST http://127.0.0.1:8080/CourseApp/academic/courses
  JSON request to add a new course:
  {
  "courseName": "Scripting",
  "description": "Python",
  "courseFee" : 1000
  }
 
 To create a student: POST http://127.0.0.1:8080/CourseApp/academic/students
 JSON request to add a new student:
 {
   "firstName" : "Jake",
   "lastName": "Anderson",
   "address": " Permanent Address",
   "email": "myfirst@email.com"
 }
 To create a professor: POST http://127.0.0.1:8080/CourseApp/academic/professors
 JSON request to add a new professor
 {
   "firstName" : "David",
   "lastName": "Dolan",
   "address": " Local Address",
   "email": "myfirst@email.com",
   "designation": "Professor"
}
To enroll student to a course: POST http://127.0.0.1:8080/CourseApp/academic/course/student_enroll
 JSON request to enroll a student to course:
 {
  "courseId": 11,
  "studentId":1
}
To enroll professor to a course: POST http://127.0.0.1:8080/CourseApp/academic/course/assign_professor
 JSON request to enroll a student to course:
 {
  "courseId": 11,
  "professorId":1
}








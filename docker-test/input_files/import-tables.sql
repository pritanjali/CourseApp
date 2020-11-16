CREATE DATABASE IF NOT EXISTS test;

USE test;

CREATE TABLE IF NOT EXISTS course (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    fees INT NOT NULL,
    description TEXT
); 


CREATE TABLE IF NOT EXISTS student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
	address VARCHAR(255)
) ;


CREATE TABLE IF NOT EXISTS professor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    designation VARCHAR(255) NOT NULL,
    address VARCHAR(255)
);
 

CREATE TABLE student_course (
  student_id INT NOT NULL,
  course_id INT NOT NULL,
  FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  PRIMARY KEY (student_id, course_id)
);

CREATE TABLE professor_course (
  professor_id INT NOT NULL,
  course_id INT NOT NULL,
  FOREIGN KEY (professor_id) REFERENCES professor (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  FOREIGN KEY (course_id) REFERENCES course (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  PRIMARY KEY (professor_id, course_id)
);


INSERT INTO course (id,name,fees,description) VALUES (11,'Java','1000','Object Oriented');
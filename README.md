# Student-management-part-2-
Lab Assignment 3
📘 Student Management System — Lab Assignment 3
Exception Handling | Multithreading | Wrapper Classes

This project is the implementation of Java Lab Assignment 3 as provided by Dr. Manish Kumar, focusing on improving a basic Student Management System by adding:

✔ Robust exception handling
✔ Custom exceptions
✔ Multithreading for loading simulation
✔ Use of wrapper classes and autoboxing
✔ Clean object-oriented structure

🚀 Project Overview

This Java console application accepts student details (Roll No, Name, Email, Course, Marks), validates the inputs, simulates a loading process using multithreading, and finally displays the student information with a grade.

The project directly implements the requirements and flowchart from the assignment PDF.

🎯 Objectives

Implement try–catch–finally for runtime error handling

Create and use a custom exception (StudentNotFoundException)

Use Thread + Runnable to simulate a loading screen

Convert primitive inputs into Integer and Double wrapper objects

Build a simple, modular Student Management System

🏗️ Class Structure
🟦 1. StudentManager

Implements RecordActions and handles:

Taking input

Validating data

Triggering the loading thread

Creating the Student object

🟦 2. Loader (implements Runnable)

Simulates a loading process using:

Thread.sleep(2000);

🟦 3. Student Class

Stores student information and calculates grade.

🟦 4. StudentNotFoundException

Custom exception for missing name or course.

🟦 5. LabAssignment3

Main class containing:

public static void main(String[] args)

⚙️ Technologies & Concepts Used
Feature Description
Exception Handling try–catch–finally, custom exception
Multithreading Thread + Runnable to simulate loading
Wrapper Classes Integer, Double with autoboxing
OOP Concepts Classes, interfaces, encapsulation
🧪 Sample Expected Output
Enter Roll No (Integer): 102
Enter Name: Karan
Enter Email: karan@mail.com
Enter Course: BCA
Enter Marks: 77.5
Loading.....
Roll No: 102
Name: Karan
Email: karan@mail.com
Course: BCA
Marks: 77.5
Grade: B
Program execution completed.

📂 How to Run the Program

Save the file as:

LabAssignment3.java


Open terminal or CMD and compile:

javac LabAssignment3.java


Run the program:

java LabAssignment3

🧩 Flowchart (From Assignment)

The logic follows the flowchart provided in the assignment PDF (Input → Validate → Loading Thread → Create Student → Display → End).

📄 Submission Includes

✔ Java source code with exception handling
✔ Multithreading using Thread
✔ Wrapper classes & autoboxing
✔ Follows assignment guidelines completely

👨‍💻 Author

Parinita Singh
Btech cse 
2401010252

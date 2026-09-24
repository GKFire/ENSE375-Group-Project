# Personal Password Manager
*ENSE 375 - Fall 2026*
## Team Members
 - Poojitha Dayalan
 - Korbin Wyssen
 - Adai Yisah

## Table of contents
- [1. Introduction](#1-introduction)
- [2. Design Problem](#2-design-problem)
    - [2.1 Problem Definition](#21-problem-definition)
    - [2.2 Design Requirements](#22-design-requirements)
        - [2.2.1 Functions](#221-functions)
        - [2.2.2 Objectives](#222-objectives)
        - [2.2.3 Constraints](#223-constraints)
- [3. Solution](#3-solution)
    - [3.1 Solution 1](#31-solution-1)
    - [3.2 Solution 2](#32-solution-2)
    - [3.3 Final Solution](#33-final-solution)
        - [3.3.1 Components](#331-components)
        - [3.3.2 Environmental, Societal, Safety, and Economic Considerations](#332-environmental-societal-safety-and-economic-considerations)
        - [3.3.3 Test Cases and Results](#334-test-cases-and-results)
        - [3.3.4 Limitations](#334-limitations)
- [4. Team work](#4-team-work)
    - [4.1 Meeting 1](#41-meeting-1)
    - [4.2 Meeting 2](#42-meeting-2)
    - [4.3 Meeting 3](#43-meeting-3)
    - [4.4 Meeting 4](#44-meeting-4)
- [5. Project Management](#project-management)
- [6. Conclusion and Future Work](#conclusion-and-future-work)
- [7. References](#references)
- [8. Appendix](#appendix)
---

## 1. Introduction

This project aims to relieve the pain experienced by users having to create and manage multiple accounts' passwords by offering a program the user can use to securely store their various passwords for these accounts that online services require. The program not just gives the user with a way to keep track of both the user's password to a website, but also allows the user to setup 2FA for their account through the use of a Time-based One-Time Password (TOTP), as well as keeping a record of the account's recovery keys in the event the user has difficulties logging into their 2FA secured account.

--- 
## 2. Design Problem

### 2.1 Problem Definition

In this age of online services and web applications, many websites require that users create a personal account so that they can access the features and services the application offers. Majority of applications require a user's account to be secured with a text-based password, and some method of two-factor-authentication, to ensure that only the authorized user who created the account can log into the account. 
 
This creates a strain on the user to memorize different passwords for each account and be able to recall what password was used for which account. Many users opt to either use the same password for multiple accounts or utilize dictionary to both meet general password length requirements and make the password rememberable. 
 
These both create security issues for the user, as if the user's password hash is leaked due a site's security being compromised, and is cracked, it could be used to gain access to a user's other account on another site. Furthermore, dictionary-based passwords are easy for an infiltrator to brute-force and crack. 

### 2.2 Design Requirements

#### 2.2.1 Functions
1. **Store Passwords**: The solution must be able to store user login information for various websites in the form of login entries.
2. **Store Text-based Passwords**: A text-based login entry must be able to store a password.
3. **Store TOTP-based Keys**: A TOTP-based login entry must be able to store a TOTP security key.
4. **Store Recovery Keys**: A TOTP-based login entry must be able to store associated account recovery keys.
5. **Base32 decoding**: The solution must have base32 decoding abilities for security keys.
6. **Generate one-time password**: The solution must be able to generate correct one-time passwords from the security key of a given login entry.
7. **HOTP specification**: The solution must use the HMAC-one-time-password specification to generate correct one-time passwords.
8. **One-time password validity**: The solution should inform the user how long the generated one-time password is valid for.
9. **List login entries**: The solution should have the ability to list all login entries to the user.
10. **Filter login entries**: The user should have the ability to search or filter through the list of login entries
11. **Create login entry**: The user must be able to create a new login entry.
12. **Modify login entry**: The user must be able to modify an existing login entry.
13. **Delete login entry**: The user must be able to delete an existing login entry.
14. **Create database file**: The solution must be able to create a new database file in the instance an existing database file could not be found.
15. **Read database file**: The solution must be able to read the database file.
16. **Write database file**: The solution must be able to write to the database file.
17. **Encrypt data**: The solution must have the ability to encrypt a user’s login entries before writing to the database file.
18. **Decrypt data**: The solution must have the ability to successfully decrypt a user’s login entries back to the original data after reading the database file.
19. **Prompt for password**: The solution must be able to prompt the user for a primary key to decrypt the database file.


#### 2.2.2 Objectives
1. Develop a functional local desktop password manager that provides centralized management of account credentials.
2. Provide secure local storage for usernames, password, time-based one-time password (TOTP) information, and recovery keys.
3. Reduce the user's dependence on memorizing multiple account passwords by providing controlled access to stored credentials.
4. Protect stored credentials from unauthorized access through authentications and encryptions.
5. Provide the core credential-management functions required to add, retrieve, search, modify, and delete stored account information.
6. Develop the application using an iterative engineering design process in which alternative designs, algorithms, modules, and feature additions can be evaluated before the final design is selected.
7. Produce a design that is modular, testable, and suitable for systematic validation using the testing required by ENSE 375.


#### 2.2.3 Constraints
**Economic Factors**: 
Constraint: As students, we have limited budgets and cannot rely on paid cloud services, API or any other hosting infrastructure. Most of the security tools are expensive to use, and it is financially out of our reach, still the system requires strong security. 

Solution: Using free and open-source cryptographic libraries and storing all the data locally on the user’s device. Using free developer tools like GitHub, JUnit and VS code. Verify before using any library which is free for development and safe to use. 
- Finance and budget limitations 
- Use free and open-source tools and libraries 
- Only use the user’s local system resources (storage and computational power); Do not use cloud services to off-load resource requirements 
- Only use the Java Programming Language for implementation of solution logic 

**Regulatory Compliance (Security and Standards)**:
Constraint: This application handles sensitive information, and there are many major issues like password leaks, weak passwords, and accidental data release. Security failures will lead to serious safety and credibility issues. 

Solution: Users will have to enter a master password before accessing other stored credentials. All sensitive information, like passwords, should be protected from external users. Security requirements will be verified through appropriate integration and validation testing. 
- Use academia/course approved tools (git, GitHub, VSCode, Eclipse IDE) 
- Use JUnit for requirements implementation testing 
- Use the test-driven development methodology for implementation 

**Reliability**:
Constraint: The password manager application might crash while saving the passwords and make the system unavailable for a while. Users might lose access to their accounts while incorrectly modifying stored credentials.

Solution: Password managers will use storage to enter and manage more data, and it reduces the possibility of corruption and data loss. Adding, Modifying, retrieving and deleting credentials must be tested through Integration and unit testing methods.
- The database file is reliant on the condition of the physical storage medium it's stored on.
- Cannot automatically create external backups of the database file.
- The solution must rely on the user's ability to remember the primary password to unlock the database file.
- The solution must rely on the user's initiative to ensure login entries are kept up to date.

**Ethics**:
Constraint: Password manager manages private and sensitive information. There are many risks in the real world. We have an ethical responsibility to avoid revealing, misusing, or collecting other user’s info.

Solution: The development team should focus on Confidentiality, Privacy and responsible for handling user’s information.
- By the sensitive nature of login details and passwords, the solution must encrypt data in the database file to uphold security expectations from the user.

**Architectural**:
We are using MVC architecture for this project. It follows the principle of “Separation of concerns”. It organizes the code in an efficient way by separating it into different parts based on its purpose rather than mixing all the responsibilities.
&nbsp;&nbsp;&nbsp;&nbsp;Model: This part of code deals with the data of the application
&nbsp;&nbsp;&nbsp;&nbsp;View: This part of code deals with the UI of the application 
&nbsp;&nbsp;&nbsp;&nbsp;Controller: This part of code deals with the functionality of the application

**Testing Constraints**: 
Testing requirements create several testing constraints while building the project. The following testing techniques must be used in this project. 
- **Test Driven Development**: Testing must be considered while developing the project rather than waiting for the project to be completed. 
- **JUnit Testing**: JUnit testing needs to be done for most of the unit test cases. 
- **Integration Testing**: The whole application needs not to be tested as a single unit. A subset of units must be selected. 
- **Path Testing**: Based on the possible execution path, at least one function should be tested. 
- **Data flow Testing**: At least one function should be tested on how the data is defined, modified, and used in the project. 
- **Boundary Value Testing**: Input values at important boundaries should be tested. 
- **Equivalence class testing**: Input values will be separated into valid and invalid classes, and it needs to be tested. 
- **Decision tables testing**: Decision tables testing will be created to test the functionality that depends on many conditions. 
- **State transition testing**: The testing will be performed in the application based on different states and transitions. 
- **Use case testing**: This testing will be performed based on the user’s perspective.

---
## 3 Solution

### 3.1 Solution 1

### 3.2 Solution 2

### 3.3 Final Solution

#### 3.3.1 Components

#### 3.3.2 Environmental, Societal, Safety, and Economic Considerations

#### 3.3.3 Test Cases and Results

#### 3.3.4 Limitations

---
## 4. Team Work

### 4.1 Meeting 1

Time: Thurs. Sept. 10th 2026, 12:45-12:55pm
Agenda: Delegate work, have work completed by Wednesday

| Team Member      | Previous Task | Progress        | Next Task                          |
|------------------|---------------|-----------------|------------------------------------|
| Korbin Wyssen    | N/A           | N/A             | Work on project business case docx |
| Poojitha Dayalan | N/A           | N/A             | Assist on project business case    |
| Adai Yisah       | N/A           | N/A             | Assist on project business case    |

### 4.2 Meeting 2

Time: Thurs. Sept. 17th 2026, 12:45-12:55pm
Agenda: Review current task progress. Delegate tasks for next part

| Team Member      | Previous Task                          | Progress       | Next Task                          |
|------------------|----------------------------------------|----------------|------------------------------------|
| Korbin Wyssen    | Work on project business case docx     | 100%           | Section 2.2.1 Functions            |
| Poojitha Dayalan | assist on project business case        | 100%           | Section 2.2.3 Constraints          |
| Adai Yisah       | assist on project business case        | Missed         | Section 2.2.3 Objectives           |

### 4.3 Meeting 3

Time: Wed. Sept. 23rd 2026 10:00am-10:30am
Agenda: Update on task progress, finalized section 2.2 and sub-sections

| Team Member      | Previous Task               | Progress       | Next Task                          |
|------------------|-----------------------------|----------------|------------------------------------|
| Korbin Wyssen    | Section 2.2.1 Functions     | Completed      | TBD                                |
| Poojitha Dayalan | Section 2.2.3 Constraints   | Completed      | TBD                                |
| Adai Yisah       | Section 2.2.3 Objectives    | Completed      | TBD                                |

### 4.4 Meeting 4

---
## 5. Project Management

---
## 6. Conclusion and Future Work

---
## 7. References

---
## 8. Appendix
---

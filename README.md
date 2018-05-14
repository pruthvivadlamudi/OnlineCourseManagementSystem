# OnlineCourseManagementSystem
Miniworld: This application is a Course management system through which a student can register courses online by logging into the course management system or the course provider page. The Course management system helps in managing the courses for people who cannot attend the courses by going to the university.

#Purpose of Application/database and Intended Users:
Nowadays when everything is online, why cannot the courses online and can be managed or taught online by professors. Therefore, the idea behind developing this application is university will list the courses in online course management system so that students can take the courses online. Through the application, different students can join the course, the university can add many courses depending on the availability of the courses, and professors can join the courses for teaching them.

#Objects/Actors/Roles:
•	Online Course Management system: The course management system can add the course delete the course, add the students and update their information. It acts as admin to the whole system.
•	Student: Student will register for the courses listed by the university thorough course management system also can use the forum to ask questions etc.
•	Professor: The course management system will add the professor to the university and the university will add the professors to the respective courses from the list of the professors that were added to the school.
•	University: The University will be registered to the course management system and can add the courses and respective professors to the courses.(The online course management system is for many universities who want to offer courses online).


Planned functionality and operations:
The application should have a login (All The passwords ae encrypted using MD5) for all the users such as everyone should signup before using the application. A login page will pop out as soon as all the users open the application. The online course management system can monitor all the records and authorize the students, university and the professors who can use the application. Below are the planned functionalities for the DB and application. (Admin here is online course management system)
•	The Admin will have full access to the application
•	The Admin maintains the courses, professors, and the university.
•	Students must be registered to the admin before they can access the courses.
•	Professors must be assigned to university before they are assigned to the course.
•	The registered student/university/professor can log in from the login module.
(Yes, Students can search browse and enroll into courses by searching the courses)
•	University assigns a course to the admin before adding it the course should have a professor assigned to it.( Yes, University also has admin page as they have to add the courses from their side)
•	For forums, the student must be registered for that particular course and there should be a professor for that course. (Forums are for specific course only the student must be registered in the class in order to post in the form)

#Scenarios:
•	Students search for the courses.
•	A student registering for the course.
•	Professors uploading the course material for their respective courses.
(No, Professors cannot assign grades, as it is an online course)
•	Forums, where students can ask questions and professors, can answer their questions.
•	Adding a new professor to the university.
•	University adding a new subject or course to the course management system, which is available for the student to register, and assigning a new professor to the course.
•	A student can view his entire courses registered.

 Project Overview

The Campus Lost & Found Portal is a web-based platform built to help students and staff report, search, and recover lost or found items inside the college campus.
It eliminates manual noticeboard or WhatsApp-group announcements by providing a secure, centralized, and automated portal for lost and found management.
How to Run the Project
 Download or Clone the Repository
git clone <YOUR_REPOSITORY_URL>

 Extract the ZIP (if downloaded)

Right-click the ZIP file → “Extract All…”

Open the folder in VS Code or Spring Tool Suite (STS).

 Set Up Backend (Spring Boot)

Open the folder in STS.

Configure application.properties with your MySQL credentials:

spring.datasource.url=jdbc:mysql://localhost:3306/lostfound
spring.datasource.username=root
spring.datasource.password=yourpassword


Run the backend:

mvn spring-boot:run

 Open Frontend (if separate)

If the frontend (HTML/CSS/JS) is in another folder:

Open the folder in VS Code

Run using Live Server extension or open index.html directly in the browser

Access the Web App

Visit:

http://localhost:8080


or the port shown in your terminal.

 Future Enhancements

Email/SMS notifications for matched items

Integration with Supabase or Firebase for real-time data sync

Mobile App version (React Native / Flutter)

AI-based image recognition to match lost/found photos automatically

 Conclusion

This system transforms the traditional lost-and-found process into an efficient, secure, and trackable digital experience.
It enhances campus communication, saves time, and helps recover items faster — all while maintaining data integrity and transparency.


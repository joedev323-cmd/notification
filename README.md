Notification Service

A reminder and email notification service built with Java and Spring Boot. The application allows clients to schedule reminders through a REST API, stores notification records in an H2 database, and automatically attempts to send email notifications when reminders become due.

The project also includes WebSocket and STOMP configuration for exploring real-time messaging.

Features
REST API: Create notification and reminder records over HTTP.
Scheduled reminders: Check for due notifications every 30 seconds.
Email delivery: Send reminder emails using Spring Boot Mail.
Persistent storage: Store notifications in an H2 file-based database.
Input validation: Validate email addresses, subjects, messages, and required timestamps.
WebSocket configuration: Register a WebSocket endpoint and configure STOMP message destinations.
Delivery tracking: Record notification status, attempts, send timestamps, and errors.
Maven Wrapper: Build and run the project without a separate Maven installation.
Tech Stack
Technology	Purpose
Java 17	Programming language
Spring Boot 3.5.16	Application framework
Spring Web	REST API
Spring WebSocket	WebSocket and STOMP configuration
Spring Boot Mail	Email delivery
Spring Data JPA	Database persistence
H2 Database	Local database
Jakarta Validation	Request validation
Maven	Build and dependency management
Prerequisites

Before getting started, ensure you have:

Java Development Kit (JDK) 17 or later
Git
A terminal or Java IDE
An SMTP server for testing email delivery

For local email testing, you can use MailHog or another SMTP testing service.

Getting Started
1. Clone the Repository
git clone https://github.com/joedev323-cmd/notification.git
cd notification

2. Run the Application

Linux / macOS

chmod +x mvnw
./mvnw spring-boot:run


Windows

.\mvnw.cmd spring-boot:run


The application is configured to start at:

http://localhost:8080

3. Build and Test

Build the application:

./mvnw clean package


Run the tests:

./mvnw test


On Windows, replace ./mvnw with .\mvnw.cmd.

Configuration

The main configuration file is located at:

src/main/resources/application.properties

Database

The application uses an H2 file-based database.

spring.datasource.url=jdbc:h2:file:./data/notificationdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false


The database files are stored under data/notificationdb.

H2 Console

The H2 web console is enabled for development.

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console


Open the console in your browser:

http://localhost:8080/h2-console

Use the following connection details:

JDBC URL: jdbc:h2:file:./data/notificationdb
Username: sa
Password: Leave blank if using the default configuration.
Security note: The H2 console and default database settings are intended for local development. Secure or disable them before deploying the application publicly.
Email / SMTP

The default configuration expects an SMTP server at localhost:1025, suitable for a local email testing setup.

spring.mail.host=localhost
spring.mail.port=1025

spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false


Make sure your SMTP testing service is running before expecting emails to be delivered.

For a real email provider, configure the appropriate SMTP host, port, authentication, and TLS settings. Keep credentials outside version control.

REST API
Create a Notification

Creates a new notification or reminder.

Endpoint

POST /api/notifications


Content-Type: application/json

Example Request
{
  "recipient": "recipient@example.com",
  "subject": "Project deadline reminder",
  "message": "Please submit your project before the deadline.",
  "deadlineAt": "2027-01-20T17:00:00Z",
  "reminderAt": "2027-01-20T16:00:00Z"
}


Replace the example email address and timestamps with appropriate values.

Example Using cURL
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "recipient@example.com",
    "subject": "Project deadline reminder",
    "message": "Please submit your project before the deadline.",
    "deadlineAt": "2027-01-20T17:00:00Z",
    "reminderAt": "2027-01-20T16:00:00Z"
  }'


A successful request returns HTTP 201 Created and the saved notification object.

Request Fields
Field	Type	Validation
recipient	String	Required; valid email address
subject	String	Required; maximum 200 characters
message	String	Required; maximum 5,000 characters
deadlineAt	ISO-8601 timestamp	Required
reminderAt	ISO-8601 timestamp	Required

Both timestamps must be supplied as valid ISO-8601 instants. The reminder time must be before the deadline, and the deadline must be in the future.

How Reminder Delivery Works
A client submits a reminder through the REST API.
The service validates the request and saves the notification with a PENDING status.
The scheduler runs every 30 seconds and looks for pending notifications whose reminder time has arrived.
The service attempts to send an email containing the message and deadline.
On success, the notification is marked SENT, and its send timestamp is recorded.
On failure, the error is recorded, and the notification remains pending for a later scheduler attempt.

Delivery depends on a reachable and correctly configured SMTP server.

WebSocket Configuration

The application registers a WebSocket endpoint and configures a simple STOMP message broker.

Setting	Value
WebSocket endpoint	/ws
Broker destination prefix	/topic
Application destination prefix	/app

The WebSocket endpoint is available at:

ws://localhost:8080/ws


The /topic prefix is intended for broker-managed message destinations, while /app is the configured prefix for application-bound messages.

These settings establish the messaging infrastructure. A client-facing STOMP subscription or publishing example requires a corresponding message handler in the application.

Project Structure
notification/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/notificationdemo/
│   │   │       ├── config/
│   │   │       │   └── WebSocketConfig.java
│   │   │       ├── controller/
│   │   │       │   └── NotificationController.java
│   │   │       ├── dto/
│   │   │       │   └── NotificationRequest.java
│   │   │       ├── entity/
│   │   │       │   └── Notification.java
│   │   │       ├── repository/
│   │   │       ├── scheduler/
│   │   │       │   └── NotificationScheduler.java
│   │   │       ├── service/
│   │   │       │   └── NotificationService.java
│   │   │       └── NotificationDemoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── data/
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md

Testing

Run automated tests with:

./mvnw test


You can also manually test notification creation using the cURL request in the REST API section.

To test email delivery, run the application with a working SMTP server and submit a reminder whose reminder time is due.

Production Considerations

Before using the service in production, consider:

Protecting API endpoints with authentication and authorization.
Securing database credentials and SMTP credentials.
Disabling the H2 console in production.
Adding appropriate error responses and centralized exception handling.
Implementing robust retry policies and preventing duplicate email delivery.
Monitoring scheduler execution and failed deliveries.
Adding integration tests for database operations, scheduling, and email delivery.
Contributing

Contributions, suggestions, and bug reports are welcome.

Fork the repository.
Create a feature branch.
Make your changes and add tests where appropriate.
Submit a pull request describing your changes.

License

No license has been specified. Add a LICENSE file if you intend to distribute the project under an open-source license.

Built with Java 17 and Spring Boot.

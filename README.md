# Library App — Project Overview

The Library App is a Spring Boot back-end that manages a complete lending workflow for books. It combines secure, token-based authentication with strictly separated business modules so that librarians and patrons can perform their day-to-day tasks through a clear and well-protected REST API.


### Auth Module

New users register with basic personal details, then log in to receive a short-lived access token and a long-lived refresh token. A dedicated endpoint renews access tokens, while logout immediately invalidates the refresh token. Spring Security enforces role checks, returning 200 OK on success, 400 Bad Request for bad input, 401 Unauthorized for missing or invalid credentials, and 409 Conflict for clashes such as duplicate e-mail addresses.

### Book Module

Librarians create, update and delete book records that store title, author, ISBN, publication date and genre. Every write operation passes validation, and duplicate ISBNs produce a 409 Conflict. Both librarians and patrons can list or search books—with full-text and filter options—using paginated endpoints, so large catalogues remain responsive.

### User Module

During registration each account is labelled as either PATRON or LIBRARIAN. Patrons can view and edit only their own profile, whereas librarians may list, update or remove any user. All such operations rely on JWT claims, ensuring patrons can never reach administrative routes.

### Borrowing Module

Patrons request a borrow; the system checks that the book is available and the borrower has not exceeded their quota, then records borrow and due dates. Returning a book updates inventory and closes the record. Patrons can view their own history; librarians can inspect every user’s history, see overdue items, and generate follow-up reports.

### Security & Access Control
- All protected endpoints require a valid JWT access token; roles are checked via Spring Security.
- Librarian privileges gate any operation that modifies shared data (books, other users, system-wide reports).
- Standard HTTP status codes communicate success and error conditions clearly.

### Observability & Ops
- Centralized request logging (correlation IDs) is persisted in the database.
- JVM, HTTP, and SQL metrics are exposed through Micrometer, scraped by Prometheus and visualised in Grafana using the Micrometer dashboard.
- Ready-to-run configurations are provided for both docker-compose and Kubernetes / Minikube deployments.

### Technologies

---
- Java 21
- Spring Boot 3.0
- Open Api (Swagger)
- Maven
- Docker
- Postman
- PostgreSQL
- Prometheus
- Grafana
- Kubernetes
- JaCoCo (Test Report)


### Rest APIs
---
<table style="width:100%;">
  <tr>
    <th>Method</th>
    <th>URL</th>
    <th>Description</th>
  </tr>

  <!-- ─────── AUTH ─────── -->
  <tr>
    <td>POST</td>
    <td>/api/v1/auth/register</td>
    <td>Register a new user</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/api/v1/auth/login</td>
    <td>User login</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/api/v1/auth/refresh-token</td>
    <td>Refresh access token</td>
  </tr>
  <tr>
    <td>POST</td>
    <td>/api/v1/auth/logout</td>
    <td>Invalidate refresh token (logout)</td>
  </tr>

  <!-- ─────── USERS ─────── -->
  <tr>
    <td>GET</td>
    <td>/api/v1/users/{id}</td>
    <td>Get user details</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/users</td>
    <td>List users (paginated)</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/api/v1/users/{id}</td>
    <td>Update user</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/api/v1/users/{id}/soft-delete</td>
    <td>Soft-delete user</td>
  </tr>

  <!-- ─────── BOOKS ─────── -->
  <tr>
    <td>POST</td>
    <td>/api/v1/books</td>
    <td>Add a new book</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/books/{id}</td>
    <td>Get book by ID</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/books</td>
    <td>Search books (paginated)</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/api/v1/books/{id}</td>
    <td>Update book</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/api/v1/books/{id}/soft-delete</td>
    <td>Soft-delete book</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/books/availability/stream</td>
    <td>Stream availability events (SSE)</td>
  </tr>

  <!-- ─────── GENRES ─────── -->
  <tr>
    <td>POST</td>
    <td>/api/v1/genres</td>
    <td>Create genre</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/genres/{id}</td>
    <td>Get genre by ID</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/genres</td>
    <td>List genres (paginated)</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/api/v1/genres/{id}</td>
    <td>Update genre</td>
  </tr>
  <tr>
    <td>DELETE</td>
    <td>/api/v1/genres/{id}</td>
    <td>Delete genre</td>
  </tr>

  <!-- ─────── BORROWINGS ─────── -->
  <tr>
    <td>POST</td>
    <td>/api/v1/borrowings</td>
    <td>Borrow a book</td>
  </tr>
  <tr>
    <td>PUT</td>
    <td>/api/v1/borrowings/{id}/return</td>
    <td>Return a book</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/borrowings</td>
    <td>List borrowings (paginated + filters)</td>
  </tr>
  <tr>
    <td>GET</td>
    <td>/api/v1/borrowings/overdue</td>
    <td>Download overdue borrowings PDF</td>
  </tr>
</table>

### Open Api (Swagger)

```
http://localhost:8080/swagger-ui/index.html
```
---
### Database Schema
<p align="center">
    <img src="images/databaseschema.png" height="600" >
</p>


### Prerequisites

#### Define Variable in .env file

```
url: ${SPRING_DATASOURCE_URL}
username: ${DATABASE_USERNAME}
password: ${DATABASE_PASSWORD}
```

### Maven Run
To build and run the application with `Maven`, please follow the directions shown below;

```sh
$ cd libraryapp
$ mvn clean install
$ mvn spring-boot:run
```

---
### Docker Run
The application can be built and run by the `Docker` engine. The `Dockerfile` has multistage build, so you do not need to build and run separately.

Please follow directions shown below in order to build and run the application with Docker Compose file;

```sh
$ cd libraryapp
$ docker-compose up -d
```

If you change anything in the project and run it on Docker, you can also use this command shown below

```sh
$ cd libraryapp
$ docker-compose up --build
```

To monitor the application, you can use the following tools:

- **Prometheus**:  
  Open in your browser at [http://localhost:9090](http://localhost:9090)  
  Prometheus collects and stores application metrics.

- **Grafana**:  
  Open in your browser at [http://localhost:3000](http://localhost:3000)  
  Grafana provides a dashboard for visualizing the metrics.  
  **Default credentials**:
    - Username: `admin`
    - Password: `admin123!`

 ### Kubernetes Run
To run the application, please follow the directions shown below;

- Start Minikube

```sh
$ minikube start
```

- Open Minikube Dashboard

```sh
$ minikube dashboard
```

- To deploy the application on Kubernetes, apply the Kubernetes configuration file underneath k8s folder

```sh
$ kubectl apply -f k8s
```

- To open Prometheus, click tunnel url link provided by the command shown below to reach out Prometheus

```sh
minikube service prometheus-service
```

- To open Grafana, click tunnel url link provided by the command shown below to reach out Prometheus

```sh
minikube service grafana-service
```

### ### JaCoCo (Test Report)
---

After running mvn clean install, JaCoCo generates an HTML coverage report at:
```
target/site/jacoco/index.html
```
	1.Open a file explorer or terminal and navigate to target/site/jacoco/.
	2.Launch index.html in your web browser.

 You’ll see a detailed breakdown of line, branch, and method coverage for every module and class in the project.
<p align="center">
    <img src="images/jacocoTest.png" height="600" >
</p>
---

### API Request Samples (Postman)
#### Borrow Book
// explain with english 
<p align="center">
  <img src="images/BorrowBook.png" height="500">
</p>

#### CreateBook
<p align="center">
  <img src="images/CreateBook.png" height="500">
</p>

#### Borrow Book  
Allows a user to borrow a book by providing the book ID and user credentials.  

<p align="center">
  <img src="images/BorrowBook.png" height="500">
</p>

#### Create Book  
Used by librarians to add a new book to the system with details like title, author, and ISBN.  

<p align="center">
  <img src="images/CreateBook.png" height="500">
</p>

#### Create Genre  
Allows a librarian to create a new genre category for books.  

<p align="center">
  <img src="images/CreateGenre.png" height="500">
</p>

#### Delete Genre  
Removes a genre from the system. Only accessible by librarian roles.  

<p align="center">
  <img src="images/DeleteGenre.png" height="500">
</p>

#### Delete User  
Soft-deletes a user account. Typically used by librarians.  

<p align="center">
  <img src="images/DeleteUser.png" height="500">
</p>

#### Get Book  
Retrieves detailed information about a specific book by its ID.  

<p align="center">
  <img src="images/GetBook.png" height="500">
</p>

#### Get Books  
Fetches a paginated list of books with optional filters like title, genre, or author.  

<p align="center">
  <img src="images/GetBooks.png" height="500">
</p>

#### Get Borrowing  
Fetches borrowing details for a specific record.  

<p align="center">
  <img src="images/GetBorrowing.png" height="500">
</p>

#### Get Genre  
Fetches details of a specific genre by ID.  

<p align="center">
  <img src="images/GetGenre.png" height="500">
</p>

#### Get Genres  
Returns a list of all genres with pagination support.  

<p align="center">
  <img src="images/GetGenres.png" height="500">
</p>

#### Get User  
Retrieves a user's details by their ID.  

<p align="center">
  <img src="images/GetUser.png" height="500">
</p>

#### Get Users  
Returns a paginated list of users in the system.  

<p align="center">
  <img src="images/GetUsers.png" height="500">
</p>

#### Login  
Authenticates a user and returns JWT access and refresh tokens.  

<p align="center">
  <img src="images/Login.png" height="500">
</p>

#### Logout  
Invalidates the user's refresh token to log them out.  

<p align="center">
  <img src="images/Logout.png" height="500">
</p>

#### Refresh Token  
Issues a new access token using a valid refresh token.  

<p align="center">
  <img src="images/RefreshToken.png" height="500">
</p>

#### Return Book  
Marks a borrowed book as returned and updates availability.  

<p align="center">
  <img src="images/ReturnBook.png" height="500">
</p>

#### Stream 1  
Streams book availability updates via Server-Sent Events (SSE).  

<p align="center">
  <img src="images/Stream-1.png" height="500">
</p>

#### Stream 2  
Alternative view for streaming real-time book availability.  

<p align="center">
  <img src="images/Sream-2.png" height="500">
</p>

#### Update Book  
Updates an existing book's details. Only librarians can perform this.  

<p align="center">
  <img src="images/UpdateBook.png" height="500">
</p>

#### Update Genre  
Modifies an existing genre’s name or attributes.  

<p align="center">
  <img src="images/UpdateGenre.png" height="500">
</p>

#### Update User  
Used to update user profile information such as name or contact.  

<p align="center">
  <img src="images/UpdateUser.png" height="500">
</p>


#### Overdue Borrowings  
PDF report of overdue borrowings, accessible by librarians.  

<p align="center">
  <img src="images/overdue.png" height="500">
</p>

#### Grafana Dashboard  
Shows application metrics visualized via Grafana dashboards.  

<p align="center">
  <img src="images/grafana-dashboard.png" height="500">
</p>

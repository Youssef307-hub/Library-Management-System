# Library Management System

## Overview
This project is a Restful API developed using the Spring Boot framework. It handles HTTP requests and responses to manage a library system. The system includes entities such as authors, books, customers, and borrowing records.
Each book has an author, and many books can have the same author, the borrowing records entity has a many-to-one relation with both the customers and the books. When creating a new book if the author of this book exists we fetch this author,
and if it doesn't exist in the system we add this author to the system, but, when deleting the book the author doesn't get deleted.

## Entities

![Image Description](https://github.com/Youssef307-hub/Library-Management-System/blob/master/EntitiesRelationShips.png)

### Author
- **id**: Unique identifier for the author.
- **name**: Name of the author.
- **birthDate**: Date of birth of the author.
- **nationality**: Nationality of the author.

### Book
- **id**: Unique identifier for the book.
- **title**: Title of the book.
- **authorId**: Foreign key referencing the author of the book.
- **isbn**: ISBN (International Standard Book Number) of the book.
- **publicationDate**: Publication date of the book.
- **genre**: Genre of the book.
- **available**: Indicates whether the book is currently available for borrowing.

### Customer
- **id**: Unique identifier for the customer.
- **name**: Name of the customer.
- **email**: Email address of the customer.
- **address**: Address of the customer.
- **phoneNumber**: Phone number of the customer.
- **password**: Password for the customer, stored in encrypted format.

### Borrowing Record
- **id**: Unique identifier for the borrowing record.
- **userId**: Foreign key referencing the user who borrowed the book.
- **bookId**: Foreign key referencing the book that was borrowed.
- **borrowDate**: Date when the book was borrowed.
- **returnDate**: Date when the book is expected to be returned.

## Endpoints

### Authors
- **GET /authors**: Retrieve all authors.
- **GET /authors/{id}**: Retrieve an author by ID.
- **POST /authors**: Create a new author.
- **PUT /authors/{id}**: Update an existing author.
- **DELETE /authors/{id}**: Delete an author by ID.

### Books
- **GET /books**: Retrieve all books.
- **GET /books/{id}**: Retrieve a book by ID.
- **POST /books**: Create a new book.
- **PUT /books/{id}**: Update an existing book.
- **DELETE /books/{id}**: Delete a book by ID.
- **GET /books/search?title={title}**: Search for books by title.
- **GET /books/search?author={author}**: Search for books by author.
- **GET /books/search?isbn={isbn}**: Search for books by ISBN.

### Customers
- **GET /customers**: Retrieve all customers.
- **GET /customers/{id}**: Retrieve a customer by ID.
- **POST /customers**: Create a new customer.
- **PUT /customers/{id}**: Update an existing customer.
- **DELETE /customers/{id}**: Delete a customer by ID.

### Borrowing Records
- **GET /borrowings**: Retrieve all borrowing records.
- **GET /borrowings/{id}**: Retrieve a borrowing record by ID.
- **POST /borrowings**: Create a new borrowing record.
- **PUT /borrowings/{id}**: Update an existing borrowing record.
- **DELETE /borrowings/{id}**: Delete a borrowing record by ID.
- **GET /borrowings/search?userId={userId}**: Retrieve borrowing records for a specific user.
- **GET /borrowings/search?bookId={bookId}**: Retrieve borrowing records for a specific book.

## Database
This application uses a SQL database (PostgreSQL) to store and retrieve data for the entities. It supports CRUD (Create, Read, Update, Delete) operations to manage the data.

## Validation and Exception Handling
The application implements validation checks to ensure data integrity and handles exceptions gracefully.

## API Documentation
Swagger is used to generate API documentation, describing the endpoints, request parameters, response formats, etc.

## Getting Started
1. **Clone the repository**:
   ```bash
   git clone <https://github.com/Youssef307-hub/Library-Management-System.git>
   ```
2. **Navigate to the project directory**:
   ```bash
   cd libraryManagementSystem
   ```
3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
4. **Access the Swagger UI**:
   Open your browser and navigate to `http://localhost:8000/swagger-ui.html` to view and interact with the API documentation.

## Example Data

### Customers
```json
[
  {
    "name": "Ahmed Ali",
    "email": "ahmed.ali@example.com",
    "address": "123 Tahrir St, Cairo, Egypt",
    "phoneNumber": "01012345678",
    "password": "StrongPass1!"
  },
  {
    "name": "Fatima Hassan",
    "email": "fatima.hassan@example.com",
    "address": "456 Zamalek St, Cairo, Egypt",
    "phoneNumber": "01123456789",
    "password": "StrongPass2@"
  }
]
```


### Authors
```json
[
  {
    "id": 1,
    "name": "الطيب صالح",
    "birthDate": "1929-07-12",
    "nationality": "سوداني"
  },
  {
    "id": 2,
    "name": "محمد شكري",
    "birthDate": "1935-07-15",
    "nationality": "مغربي"
  },
  {
    "id": 3,
    "name": "غسان كنفاني",
    "birthDate": "1936-04-09",
    "nationality": "فلسطيني"
  },
  {
    "id": 4,
    "name": "أحمد خالد توفيق",
    "birthDate": "1962-06-10",
    "nationality": "مصري"
  }
]
```


### Books
```json
[
  {
    "id": 1,
    "title": "موسم الهجرة إلى الشمال",
    "publicationDate": "1966-01-01",
    "isbn": "9780141187052",
    "genre": "رواية",
    "available": true,
    "author": {
      "id": 1,
      "name": "الطيب صالح",
      "birthDate": "1929-07-12",
      "nationality": "سوداني"
    }
  },
  {
    "id": 2,
    "title": "الخبز الحافي",
    "publicationDate": "1972-01-01",
    "isbn": "9789953267883",
    "genre": "سيرة ذاتية",
    "available": true,
    "author": {
      "id": 2,
      "name": "محمد شكري",
      "birthDate": "1935-07-15",
      "nationality": "مغربي"
    }
  },
  {
    "id": 3,
    "title": "رجال في الشمس",
    "publicationDate": "1963-01-01",
    "isbn": "9789953893395",
    "genre": "رواية",
    "available": true,
    "author": {
      "id": 3,
      "name": "غسان كنفاني",
      "birthDate": "1936-04-09",
      "nationality": "فلسطيني"
    }
  }
]
```

### Borrowing Records
```json
[
  {
    "customerId": 1,
    "bookId": 2,
    "borrowDate": "2023-01-10",
    "returnDate": "2023-02-10"
  },
  {
    "customerId": 3,
    "bookId": 5,
    "borrowDate": "2023-03-15",
    "returnDate": "2023-04-15"
  }
]
```

---

This README file is structured to provide a comprehensive overview of the Library Management System, including its entities, endpoints, and how to get started. The provided example data can be used to populate the database for testing purposes.

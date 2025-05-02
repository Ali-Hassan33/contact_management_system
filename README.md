# Contactly

Contactly is a responsive contact management application built with React & Spring Boot.
It provides an intuitive interface for managing your contacts with features for adding, editing, deleting, and importing/exporting contacts.
![Image](https://github.com/user-attachments/assets/294b8904-8056-44da-bd2d-bb2fc7d0ac1a)
![Image](https://github.com/user-attachments/assets/214f1ca8-1e4f-4615-a87f-3489e2c77c05)

---

## Technologies Used

- **Backend** (running on localhost:8080):
    - Java 23
    - Spring Boot 3.4.3
    - PostgreSQL
    - Docker
    - Lombok
    - Java Mail Sender

- **Frontend**:
    - React 19
    - React Router 7
    - Material UI (MUI) components
    - Axios

### Build and Run the Application

1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Run the project**:
   ```bash
   mvn spring-boot:run
   ```
   
---
### Database Initialization Mode
To ensure that SQL scripts are executed when the application starts. Modify the value of the following property to `always` in `application.yml` file:

```yaml
spring:
  sql:
    init:
      mode: always
```

Use the mode `always` only during the initial startup.

## Contribution

Contributions are welcome! Follow these steps:

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature-name`.
3. Commit your changes: `git commit -m "Add feature"`.
4. Push to the branch: `git push origin feature-name`.
5. Open a pull request.

---

## License

This project is **not licensed**. All rights are reserved by the project owner, **Ali Hassan**. Unauthorized use, distribution, or modification is strictly prohibited.

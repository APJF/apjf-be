# Capstone Project

## Project Setup

### Prerequisites
- Java 17 or higher
- Gradle
- Docker (for running services like MinIO)

### Environment Variables

Update your `.env` file to include the following variables:

```
# Database Configuration
POSTGRES_URL=your_database_url
POSTGRES_USERNAME=your_database_username
POSTGRES_PASSWORD=your_database_password

# Mail Configuration
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password

# OAuth2 Configuration
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# MinIO Configuration
MINIO_ACCESS_KEY=your_minio_access_key
MINIO_SECRET_KEY=your_minio_secret_key
```

Ensure these variables are correctly set before running the application.

### Running the Project
1. Build the project:
   ```bash
   ./gradlew build
   ```
2. Start the application:
   ```bash
   ./gradlew bootRun
   ```
3. Access the application at `http://localhost:8080`.

## Error Handling

### GlobalExceptionHandler
The `GlobalExceptionHandler` is a centralized mechanism for handling exceptions across the application. It ensures that all errors are processed uniformly and provides meaningful error messages to the client. This improves maintainability and reduces redundancy in error handling logic.

### ApiResponseDto
The `ApiResponseDto` class standardizes the JSON response format for all API endpoints. It typically includes fields like:
- `status`: HTTP status code
- `message`: A brief description of the response
- `data`: The payload of the response (if any)

This ensures consistency in API responses, making it easier for clients to parse and handle them.

## API Documentation

### Swagger Integration
The project includes Swagger for API documentation. To access the Swagger UI:
1. Start the application.
2. Navigate to `http://localhost:8080/swagger-ui.html`.

### API Endpoints

### Authentication
- **POST /api/auth/login**
  - Description: Authenticate a user and return a JWT token.
  - Request Body:
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```
  - Response:
    ```json
    {
      "success": true,
      "message": "Login successful",
      "data": {
        "token": "jwt_token_here"
      },
      "timestamp": 1690713600000
    }
    ```

### Units
- **GET /api/units**
  - Description: Retrieve all units.
  - Response:
    ```json
    {
      "success": true,
      "message": "Danh sách bài học",
      "data": [
        {
          "id": "unit1",
          "name": "Unit 1",
          "description": "Description of Unit 1"
        }
      ],
      "timestamp": 1690713600000
    }
    ```

- **GET /api/units/chapter/{chapterId}**
  - Description: Retrieve units by chapter ID.
  - Response:
    ```json
    {
      "success": true,
      "message": "Danh sách bài học theo chương",
      "data": [
        {
          "id": "unit1",
          "name": "Unit 1",
          "description": "Description of Unit 1"
        }
      ],
      "timestamp": 1690713600000
    }
    ```

### Approval Requests
- **GET /api/approval-requests**
  - Description: Retrieve approval requests with optional filters.
  - Query Parameters:
    - `pending` (boolean): Filter by pending status.
    - `targetType` (string): Filter by target type.
    - `createdBy` (string): Filter by creator.
    - `reviewedBy` (string): Filter by reviewer.
  - Response:
    ```json
    {
      "success": true,
      "message": "Danh sách approval requests",
      "data": [
        {
          "id": "request1",
          "status": "PENDING",
          "targetType": "COURSE"
        }
      ],
      "timestamp": 1690713600000
    }
    ```

### Learning Paths
- **POST /api/learning-paths**
  - Description: Create a new learning path.
  - Request Body:
    ```json
    {
      "name": "Learning Path 1",
      "description": "Description of Learning Path 1"
    }
    ```
  - Response:
    ```json
    {
      "success": true,
      "message": "Tạo lộ trình học tập thành công",
      "data": {
        "id": "path1",
        "name": "Learning Path 1"
      },
      "timestamp": 1690713600000
    }
    ```

- **PUT /api/learning-paths/{id}**
  - Description: Update an existing learning path.
  - Request Body:
    ```json
    {
      "name": "Updated Learning Path",
      "description": "Updated description"
    }
    ```
  - Response:
    ```json
    {
      "success": true,
      "message": "Cập nhật lộ trình học tập thành công",
      "data": {
        "id": "path1",
        "name": "Updated Learning Path"
      },
      "timestamp": 1690713600000
    }
    ```

- **DELETE /api/learning-paths/{id}**
  - Description: Delete a learning path.
  - Response:
    ```json
    {
      "success": true,
      "message": "Xóa lộ trình học tập thành công",
      "data": null,
      "timestamp": 1690713600000
    }
    ```

### Exam History
- **GET /api/exam-history/user/{userId}**
  - Description: Retrieve a user's exam history.
  - Response:
    ```json
    {
      "success": true,
      "message": "Lịch sử làm bài kiểm tra",
      "data": [
        {
          "examId": "exam1",
          "score": 85,
          "status": "PASSED"
        }
      ],
      "timestamp": 1690713600000
    }
    ```

- **GET /api/exam-history/user/{userId}/paginated**
  - Description: Retrieve a user's paginated exam history.
  - Response:
    ```json
    {
      "success": true,
      "message": "Lịch sử làm bài kiểm tra phân trang",
      "data": {
        "content": [
          {
            "examId": "exam1",
            "score": 85,
            "status": "PASSED"
          }
        ],
        "pageable": {
          "pageNumber": 0,
          "pageSize": 10
        }
      },
      "timestamp": 1690713600000
    }
    ```

### Materials
- **GET /api/materials**
  - Description: Retrieve all materials.
  - Response:
    ```json
    {
      "success": true,
      "message": "Danh sách tài liệu",
      "data": [
        {
          "id": "material1",
          "name": "Material 1",
          "type": "PDF"
        }
      ],
      "timestamp": 1690713600000
    }
    ```

### Questions
- **GET /api/questions**
  - Description: Retrieve questions with filtering and pagination options.
  - Query Parameters:
    - `page` (int): Page number (default: 0).
    - `size` (int): Page size (default: 10).
    - `sort` (string): Field to sort by (default: "createdAt").
    - `direction` (string): Sort direction ("asc" or "desc", default: "desc").
    - `keyword` (string): Search keyword.
    - `type` (string): Question type (e.g., MULTIPLE_CHOICE).
    - `examId` (string): Filter by exam ID.
  - Response:
    ```json
    {
      "success": true,
      "message": "Danh sách câu hỏi",
      "data": {
        "content": [
          {
            "id": "question1",
            "content": "What is Java?",
            "type": "MULTIPLE_CHOICE"
          }
        ],
        "pageable": {
          "pageNumber": 0,
          "pageSize": 10
        }
      },
      "timestamp": 1690713600000
    }
    ```

## Additional Notes
- Ensure the `.env` file is not committed to version control.
- For further customization, refer to the `application.yml` file in the `src/main/resources` directory.

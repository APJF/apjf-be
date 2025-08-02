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


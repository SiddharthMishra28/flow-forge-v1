# 🚀 Pipeline Orchestrator

A comprehensive Spring Boot 3.x application for End-to-End Test Automation Pipeline Orchestration that integrates with GitLab Pipelines, supports runtime test data ingestion, executes flows asynchronously, provides replay capabilities for failed executions, and offers real-time log streaming via WebSockets.

## 🧑‍💻 Development Team

- **Siddharth Mishra**
- **Ashwin Jiwane**

## 🚀 Features

- **GitLab Integration**: Trigger and monitor GitLab pipelines.
- **Async Flow Execution**: Execute test flows asynchronously with sequential step execution.
- **Runtime Data Exchange**: Parse and merge `output.env` files between pipeline steps.
- **Replay/Resume Capability**: Resume failed flows from a specific step with automatic data ingestion.
- **Test Data Management**: Manage test data associated with applications, including categories and descriptions.
- **Enhanced Flow Creation**: A single API call to create a flow with embedded flow steps and test data.
- **Real-time Log Streaming**: Stream logs for a flow execution in real-time using WebSockets.
- **REST API**: Full REST API with OpenAPI/Swagger documentation.
- **Database Support**: H2 for development, PostgreSQL for production.

## 💻 System Requirements

- **Java**: OpenJDK 17 or higher
- **Maven**: 3.6+

## 🏗 Architecture

### Core Entities

1.  **Application**: Represents a GitLab project with access credentials.
2.  **TestData**: Key-value pairs of test data associated with an Application, with category and description.
3.  **FlowStep**: An individual test automation step with a branch, test tag, and initial data.
4.  **Flow**: An orchestrated sequence of flow steps.
5.  **FlowExecution**: An instance of a flow execution with runtime state.
6.  **PipelineExecution**: A GitLab pipeline execution within a flow.

## 🛠 Setup Instructions

1.  **Clone the repository**
    ```bash
    git clone <repository-url>
    cd pipeline-orchestrator
    ```

2.  **Build the application**
    ```bash
    mvn clean install
    ```

3.  **Run the application**
    ```bash
    mvn spring-boot:run
    ```

4.  **Access the application**
    -   Application: http://localhost:8080
    -   Swagger UI: http://localhost:8080/swagger-ui.html
    -   H2 Console: http://localhost:8080/h2-console
        -   JDBC URL: `jdbc:h2:mem:testdb`
        -   Username: `sa`
        -   Password: `password`

## 📖 API Reference

### 1. Applications

-   `POST /api/applications`: Create a new application.
-   `GET /api/applications/{id}`: Get an application by ID.
-   `GET /api/applications`: Get all applications.
-   `PUT /api/applications/{id}`: Update an application.
-   `DELETE /api/applications/{id}`: Delete an application.

### 2. Test Data

**Create Test Data**

```bash
curl -X POST http://localhost:8080/api/test-data \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": 1,
    "category": "API_CREDENTIALS",
    "description": "Credentials for the staging environment API",
    "testData": {
      "USERNAME": "testuser",
      "PASSWORD": "testpass123"
    }
  }'
```

-   `GET /api/test-data/{id}`: Get test data by ID.
-   `GET /api/test-data`: Get all test data.
-   `PUT /api/test-data/{id}`: Update test data.
-   `DELETE /api/test-data/{id}`: Delete test data.

### 3. Flows & Flow Steps

**Enhanced Flow Creation**

The recommended way to create a flow is to use the enhanced flow creation API, which allows you to define the flow, its steps, and their test data in a single call.

```bash
curl -X POST http://localhost:8080/api/flows \
  -H "Content-Type: application/json" \
  -d '{
    "squashTestCaseId": 12345,
    "flowSteps": [
      {
        "applicationId": 1,
        "branch": "main",
        "testTag": "login-tests",
        "testStage": "test",
        "description": "Login functionality tests",
        "testData": [
          {
            "applicationId": 1,
            "category": "LOGIN_CREDENTIALS",
            "description": "Standard user credentials",
            "testData": {
              "USERNAME": "testuser",
              "PASSWORD": "testpass123"
            }
          }
        ]
      },
      {
        "applicationId": 1,
        "branch": "main",
        "testTag": "dashboard-tests",
        "testStage": "test",
        "description": "Dashboard functionality tests",
        "testData": []
      }
    ]
  }'
```

-   `GET /api/flows/{id}`: Get a flow by ID.
-   `POST /api/flows/{id}/execute`: Execute a flow.
-   `GET /api/flow-executions/{id}`: Get details of a flow execution.

### 4. Real-time Log Streaming

To view live logs for a running flow execution, open the following URL in your browser. Replace `{flowExecutionUUID}` with the ID of your flow execution.

`http://localhost:8080/logs.html?flowExecutionUUID={flowExecutionUUID}`

This will open a terminal-like view in your browser that streams logs in real-time.

## 🤝 Contributing

1.  Fork the repository
2.  Create a feature branch
3.  Make your changes
4.  Add tests
5.  Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
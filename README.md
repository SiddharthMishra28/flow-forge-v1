# FlowForge: E2E Test Automation Orchestrator

**FlowForge** is a powerful, open-source Spring Boot 3.x application designed to orchestrate End-to-End (E2E) Test Automation pipelines. It provides a robust platform to manage, execute, and monitor complex testing workflows, with seamless integration with GitLab pipelines.

## 🌟 Key Features

- **GitLab Integration**: Natively trigger and monitor GitLab CI/CD pipelines for test execution.
- **Asynchronous Flow Execution**: Run complex test flows asynchronously, with support for sequential and parallel step execution.
- **Dynamic Data Exchange**: Exchange data between pipeline steps at runtime by parsing and merging `output.env` files.
- **Resilience and Recovery**: Easily replay or resume failed test flows from any specific step, with automatic data ingestion.
- **Centralized Test Data Management**: Manage and version your test data alongside your applications, with support for categorization and descriptions.
- **Simplified Flow Creation**: A single, intuitive API to define and create an entire E2E flow, including all its steps and associated test data.
- **Real-time Log Streaming**: Monitor your test executions in real-time with live log streaming directly in your browser via WebSockets.
- **Comprehensive REST API**: A full-fledged REST API with detailed OpenAPI/Swagger documentation for easy integration and management.
- **Flexible Database Support**: Out-of-the-box support for H2 (development) and PostgreSQL (production).
- **Customizable Timer-Based Execution**: Schedule flows with cron expressions or introduce delays between steps.

## 🏗️ System Architecture

FlowForge is built on a modular, scalable architecture designed for high performance and maintainability.

### Core Components

1.  **Application**: Represents a GitLab project, including its access credentials (personal access token). This is the primary entity for which flows are defined.
2.  **TestData**: A flexible key-value store for test data associated with an `Application`. Each `TestData` entity can be categorized for better organization (e.g., `API_CREDENTIALS`, `USER_PROFILES`).
3.  **FlowStep**: An individual, executable step within a flow. It is linked to an `Application` and defines the GitLab pipeline branch, test tag, and test stage to be executed.
4.  **Flow**: An ordered sequence of `FlowSteps` that represents a complete E2E test scenario.
5.  **FlowExecution**: A runtime instance of a `Flow`, capturing its state (`RUNNING`, `PASSED`, `FAILED`), start/end times, and the aggregated runtime variables.
6.  **PipelineExecution**: Represents the execution of a single GitLab pipeline within a `FlowExecution`, tracking its status and associated logs.

### Execution Workflow

1.  A user triggers a `Flow` execution via the `/api/flows/{id}/execute` endpoint.
2.  The `FlowExecutionService` creates a `FlowExecution` record and begins processing the `FlowSteps` sequentially.
3.  For each `FlowStep`, the service merges the configured `TestData` to create a set of runtime variables.
4.  The `GitLabApiClient` triggers the corresponding GitLab pipeline, passing the runtime variables as environment variables.
5.  The application continuously polls the GitLab API to monitor the pipeline status.
6.  If the pipeline generates an `output.env` file as an artifact, the system downloads, parses, and merges it into the `FlowExecution`'s runtime variables for subsequent steps to use.
7.  Logs are streamed in real-time via WebSockets and can be viewed at `http://localhost:8080/logs.html`.
8.  If a step fails, the flow execution is marked as `FAILED`, and the user can later trigger a replay.

## 💻 System Requirements

- **Java**: OpenJDK 17 or higher
- **Maven**: 3.6+
- **PostgreSQL** (Optional, for production)

## 🛠️ Getting Started: A Beginner's Guide

This guide will walk you through setting up and running your first E2E test flow with FlowForge.

### 1. Clone and Build

First, clone the repository and build the application using Maven.

```bash
git clone <repository-url>
cd flow-forge-v1
mvn clean install
```

### 2. Run the Application

Run the application using the Spring Boot Maven plugin. By default, it runs with an in-memory H2 database.

```bash
mvn spring-boot:run
```

Once started, you can access the following:
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - **JDBC URL**: `jdbc:h2:mem:testdb`
  - **Username**: `sa`
  - **Password**: `password`

### 3. Configure Your First Application

Before creating a flow, you need to register your GitLab project with FlowForge.

**Endpoint**: `POST /api/applications`

**Payload**:
```json
{
  "applicationName": "My E2E Project",
  "applicationDescription": "My project for end-to-end testing",
  "gitlabProjectId": "YOUR_GITLAB_PROJECT_ID",
  "personalAccessToken": "YOUR_GITLAB_PAT"
}
```
- `gitlabProjectId`: You can find this on your GitLab project's homepage.
- `personalAccessToken`: A GitLab Personal Access Token with `api` scope.

### 4. Create a Test Flow

Now, let's create a complete flow with two steps. This single API call will define the flow and all its components.

**Endpoint**: `POST /api/flows`

**Payload**:
```json
{
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
          "variables": {
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
}
```
This creates a flow linked to Squash TM test case `12345` with two sequential steps. The first step includes inline test data for login.

### 5. Execute the Flow

Trigger the flow execution using its ID (which was returned in the previous step).

**Endpoint**: `POST /api/flows/{id}/execute`

**Payload**:
You can optionally provide additional runtime variables.
```json
{
  "additionalProp1": "string",
  "additionalProp2": "string"
}
```

### 6. Monitor in Real-time

Open the log streaming page in your browser to see live logs. Replace `{flowExecutionUUID}` with the ID from the execution response.

`http://localhost:8080/logs.html?flowExecutionUUID={flowExecutionUUID}`

## 📖 API Reference & Schemas

### Endpoints

#### Applications
- `POST /api/applications`: Create a new application.
- `GET /api/applications/{id}`: Get an application by ID.
- `GET /api/applications`: Get all applications.
- `PUT /api/applications/{id}`: Update an application.
- `DELETE /api/applications/{id}`: Delete an application.

#### Test Data
- `POST /api/test-data`: Create new test data.
- `GET /api/test-data/{id}`: Get test data by ID.
- `GET /api/test-data`: Get all test data.
- `PUT /api/test-data/{id}`: Update test data.
- `DELETE /api/test-data/{id}`: Delete test data.

#### Flows
- `POST /api/flows`: Create a new flow with embedded steps and test data.
- `GET /api/flows/{id}`: Get a flow by ID.
- `GET /api/flows`: Get all flows.
- `PUT /api/flows/{id}`: Update a flow.
- `DELETE /api/flows/{id}`: Delete a flow.
- `POST /api/flows/{id}/execute`: Execute a flow.

#### Flow Steps
- `POST /api/flow-steps`: Create a new flow step.
- `GET /api/flow-steps/{id}`: Get a flow step by ID.
- `GET /api/flow-steps`: Get all flow steps.
- `PUT /api/flow-steps/{id}`: Update a flow step.
- `DELETE /api/flow-steps/{id}`: Delete a flow step.

### Schemas

#### `CombinedFlowDto` (for `POST /api/flows`)
```json
{
  "squashTestCaseId": 0,
  "flowSteps": [
    {
      "applicationId": 0,
      "branch": "string",
      "testTag": "string",
      "testStage": "string",
      "description": "string",
      "squashStepIds": [ 0 ],
      "testData": [
        {
          "applicationId": 0,
          "category": "string",
          "description": "string",
          "variables": {
            "additionalProp1": "string",
            "additionalProp2": "string"
          }
        }
      ],
      "invokeTimer": {
        "delay": {
          "timeUnit": "minutes",
          "value": "+10"
        },
        "isScheduled": true,
        "scheduledCron": "0 0 2 * * *"
      }
    }
  ]
}
```

#### `TestDataDto` (for `POST /api/test-data`)
```json
{
  "applicationId": 0,
  "category": "string",
  "description": "string",
  "variables": {
    "additionalProp1": "string",
    "additionalProp2": "string"
  }
}
```

## 🤝 Contributing

1.  Fork the repository.
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.

# Pipeline Orchestrator

A Spring Boot 3.x application for End-to-End Test Automation Pipeline Orchestration that integrates GitLab Pipelines and SquashTM, supports runtime test data ingestion, and executes flows asynchronously.

## 🚀 Features

- **GitLab Integration**: Trigger and monitor GitLab pipelines
- **Async Flow Execution**: Execute test flows asynchronously with sequential step execution
- **Runtime Data Exchange**: Parse and merge `output.env` files between pipeline steps
- **SquashTM Integration**: Map test steps with SquashTM test cases
- **REST API**: Comprehensive REST API with OpenAPI/Swagger documentation
- **Database Support**: H2 for development, PostgreSQL for production
- **Production Ready**: Includes monitoring, logging, and error handling

## 🏗 Architecture

### Core Entities

1. **Application**: Represents a GitLab project with access credentials
2. **FlowStep**: Individual test automation step with branch, test tag, and initial data
3. **Flow**: Orchestrated sequence of flow steps with global variables
4. **FlowExecution**: Instance of flow execution with runtime state
5. **PipelineExecution**: GitLab pipeline execution within a flow

### Execution Flow

1. Create Applications (GitLab projects)
2. Define FlowSteps (test automation steps)
3. Create Flows (sequence of steps)
4. Execute Flows asynchronously
5. Monitor execution progress and results

## 🛠 Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- GitLab account with Personal Access Token
- (Optional) PostgreSQL for production

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd pipeline-orchestrator
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: `password`

### Production Setup

1. **Set environment variables**
   ```bash
   export SPRING_PROFILES_ACTIVE=production
   export DATABASE_URL=jdbc:postgresql://localhost:5432/pipeline_orchestrator
   export DATABASE_USERNAME=postgres
   export DATABASE_PASSWORD=your_password
   ```

2. **Build and run**
   ```bash
   mvn clean package
   java -jar target/pipeline-orchestrator-1.0.0.jar
   ```

## 📖 API Usage Guide

### 1. Create an Application

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "gitlabProjectId": "12345",
    "personalAccessToken": "glpat-xxxxxxxxxxxxxxxxxxxx"
  }'
```

### 2. Create Flow Steps

```bash
curl -X POST http://localhost:8080/api/flow-steps \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": 1,
    "branch": "main",
    "testTag": "smoke-tests",
    "testStage": "test",
    "squashStepIds": [101, 102],
    "initialTestData": {
      "USERNAME": "testuser",
      "PASSWORD": "testpass"
    }
  }'
```

### 3. Create a Flow

```bash
curl -X POST http://localhost:8080/api/flows \
  -H "Content-Type: application/json" \
  -d '{
    "flowStepIds": [1, 2, 3],
    "squashTestCaseId": 500,
    "globalVariables": {
      "ENVIRONMENT": "staging",
      "TIMEOUT": "30"
    }
  }'
```

### 4. Execute a Flow

```bash
curl -X POST http://localhost:8080/api/flows/1/execute
```

### 5. Monitor Flow Execution

```bash
# Get comprehensive flow execution details (includes nested flow, steps, applications, pipelines)
curl http://localhost:8080/api/flow-executions/{flowExecutionUUID}

# Get all pipeline executions for a flow execution
curl http://localhost:8080/api/flow-executions/{flowExecutionUUID}/pipelines

# Get specific pipeline execution by database ID
curl http://localhost:8080/api/flow-executions/{flowExecutionUUID}/pipelines/{pipelineExecutionId}

# Get specific pipeline execution by GitLab pipeline ID
curl http://localhost:8080/api/flow-executions/{flowExecutionUUID}/gitlab-pipelines/{gitlabPipelineId}
```

## 🔧 Configuration

### GitLab Configuration

The application supports both **mock mode** (for testing) and **real GitLab integration**:

```yaml
# GitLab configuration
gitlab:
  base-url: https://gitlab.com
  mock-mode: false  # Set to true for testing without real GitLab
  timeout: 60
  max-retries: 3
```

### Application Properties

Key configuration options in `application.yml`:

```yaml
spring:
  profiles:
    active: local  # or production or mock
  
  # Database configuration
  datasource:
    url: jdbc:h2:mem:testdb  # H2 for local
    # url: jdbc:postgresql://localhost:5432/pipeline_orchestrator  # PostgreSQL for production
  
  # JPA configuration
  jpa:
    hibernate:
      ddl-auto: update  # or validate for production
    show-sql: false

# Server configuration
server:
  port: 8080

# Swagger configuration
springdoc:
  swagger-ui:
    enabled: true  # false for production
```

### Async Configuration

Thread pool settings in `AsyncConfig.java`:

- **Flow Execution Pool**: 5-20 threads for flow executions
- **Pipeline Polling Pool**: 10-50 threads for GitLab API polling

## 📊 Monitoring and Observability

### Health Checks

- **Endpoint**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Logging

Structured logging with different levels:
- **DEBUG**: Detailed execution flow
- **INFO**: Key business events
- **ERROR**: Failures and exceptions

## 🔄 Data Flow Example

### output.env Format

GitLab pipelines should generate `output.env` files:

```bash
# Example output.env
USER_ID=12345
SESSION_TOKEN="abc123def456"
TRANSACTION_ID=txn_789
export API_ENDPOINT=https://api.example.com
```

### Variable Merging

1. **Initial Variables**: From FlowStep.initialTestData
2. **Global Variables**: From Flow.globalVariables  
3. **Runtime Variables**: Parsed from each pipeline's output.env
4. **Merged Variables**: Passed to subsequent pipeline steps

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### Integration Testing

```bash
# Start application
mvn spring-boot:run

# Run integration tests
curl -X POST http://localhost:8080/api/applications -H "Content-Type: application/json" -d '{"gitlabProjectId":"test","personalAccessToken":"test"}'
```

## 🚨 Error Handling

The application includes comprehensive error handling:

- **Validation Errors**: 400 Bad Request with field-level errors
- **Not Found**: 404 for missing resources
- **Conflicts**: 409 for duplicate resources
- **Server Errors**: 500 for unexpected failures

## 📝 API Documentation

Complete API documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact: support@pipelineorchestrator.com

---

**Happy Testing! 🎯**
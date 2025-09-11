# 🚀 Pipeline Orchestrator

**Author:** Siddharth Mishra  
**Email:** mishra.siddharth90@gmail.com

A comprehensive Spring Boot 3.x application for End-to-End Test Automation Pipeline Orchestration that integrates GitLab Pipelines and SquashTM, supports runtime test data ingestion across pipelines, executes flows asynchronously, and provides advanced replay/resume capabilities for failed executions.

## 🚀 Features

- **GitLab Integration**: Trigger and monitor GitLab pipelines with comprehensive API support
- **Async Flow Execution**: Execute test flows asynchronously with sequential step execution
- **Runtime Data Exchange**: Parse and merge `output.env` files between pipeline steps
- **Replay/Resume Capability**: Resume failed flows from any specific step with automatic data ingestion
- **SquashTM Integration**: Map test steps with SquashTM test cases and projects
- **Advanced Analytics**: Comprehensive metrics, trends, and failure analysis
- **REST API**: Full REST API with OpenAPI/Swagger documentation
- **Database Support**: H2 for development, PostgreSQL for production
- **Production Ready**: Includes monitoring, logging, error handling, and scalability features
- **Test Data Management**: Sophisticated test data management with runtime variable merging

## 💻 System Requirements

### Minimum System Requirements
- **Java**: OpenJDK 17 or higher
- **Memory**: 2 GB RAM
- **Storage**: 1 GB free disk space
- **CPU**: 2 cores
- **Database**: H2 (embedded) or PostgreSQL 12+
- **Network**: Internet access for GitLab API calls

### Optimum System Requirements (50 Parallel Flows)
- **Java**: OpenJDK 17 or higher
- **Memory**: 8 GB RAM (recommended 16 GB)
- **Storage**: 10 GB free disk space (with database growth consideration)
- **CPU**: 8 cores (recommended 16 cores)
- **Database**: PostgreSQL 14+ with dedicated server
  - **DB Memory**: 4 GB allocated to PostgreSQL
  - **DB Storage**: SSD with 50 GB+ space
  - **DB Connections**: Pool size of 100+ connections
- **Network**: High-speed internet (100+ Mbps) for GitLab API calls
- **Load Balancer**: For multiple application instances
- **Monitoring**: Prometheus + Grafana for observability

### Production Deployment Recommendations
- **Application Instances**: 2-3 instances behind load balancer
- **Thread Pool Configuration**:
  - Flow Execution Pool: 20-50 threads
  - Pipeline Polling Pool: 50-100 threads
- **Database**: PostgreSQL cluster with read replicas
- **Caching**: Redis for session management and caching
- **Monitoring**: Full observability stack with alerts

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

## 📖 Complete API Reference

### Core Entity Management

#### 1. Applications (GitLab Projects)

**Create Application**
```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "gitlabProjectId": "12345",
    "personalAccessToken": "glpat-xxxxxxxxxxxxxxxxxxxx"
  }'
```

**Get Application**
```bash
curl http://localhost:8080/api/applications/1
```

**Update Application**
```bash
curl -X PUT http://localhost:8080/api/applications/1 \
  -H "Content-Type: application/json" \
  -d '{
    "gitlabProjectId": "12345",
    "personalAccessToken": "glpat-new-token"
  }'
```

**Delete Application**
```bash
curl -X DELETE http://localhost:8080/api/applications/1
```

**Validate GitLab Connection**
```bash
curl -X POST http://localhost:8080/api/applications/validate \
  -H "Content-Type: application/json" \
  -d '{
    "accessToken": "glpat-xxxxxxxxxxxxxxxxxxxx",
    "projectId": "12345"
  }'
```

**Successful Response (200):**
```json
{
  "valid": true,
  "message": "GitLab connection successful",
  "projectName": "username/my-awesome-project",
  "projectUrl": "https://gitlab.com/username/my-awesome-project"
}
```

**Failed Response (401/403/404/500):**
```json
{
  "valid": false,
  "message": "Invalid access token or insufficient permissions"
}
```

#### 2. Test Data Management

**Create Test Data**
```bash
curl -X POST http://localhost:8080/api/test-data \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Login Credentials",
    "description": "Test credentials for staging environment",
    "data": {
      "USERNAME": "testuser",
      "PASSWORD": "testpass123",
      "API_KEY": "staging-api-key"
    }
  }'
```

**Get Test Data**
```bash
curl http://localhost:8080/api/test-data/1
```

#### 3. Flow Steps

**Create Flow Step**
```bash
curl -X POST http://localhost:8080/api/flow-steps \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": 1,
    "branch": "main",
    "testTag": "smoke-tests",
    "testStage": "test",
    "squashStepIds": [101, 102],
    "testDataIds": [1, 2]
  }'
```

**Get Flow Step**
```bash
curl http://localhost:8080/api/flow-steps/1
```

**Update Flow Step**
```bash
curl -X PUT http://localhost:8080/api/flow-steps/1 \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": 1,
    "branch": "develop",
    "testTag": "regression-tests",
    "testStage": "integration-test",
    "squashStepIds": [101, 102, 103],
    "testDataIds": [1, 3]
  }'
```

#### 4. Flows

**Create Flow**
```bash
curl -X POST http://localhost:8080/api/flows \
  -H "Content-Type: application/json" \
  -d '{
    "flowStepIds": [1, 2, 3],
    "squashTestCaseId": 500
  }'
```

**Get Flow**
```bash
curl http://localhost:8080/api/flows/1
```

### Flow Execution & Monitoring

#### 5. Execute Flows

**Start Flow Execution**
```bash
curl -X POST http://localhost:8080/api/flows/1/execute
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "flowId": 1,
  "startTime": "2024-01-15T10:30:00",
  "status": "RUNNING",
  "runtimeVariables": {},
  "createdAt": "2024-01-15T10:30:00"
}
```

#### 6. Monitor Flow Executions

**Get Comprehensive Flow Execution Details**
```bash
curl http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000
```

**Get All Executions for a Flow**
```bash
curl http://localhost:8080/api/flows/1/executions
```

#### 7. Pipeline Execution Monitoring

**Get All Pipeline Executions for Flow Execution**
```bash
curl http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000/pipelines
```

**Get Pipeline Executions by Flow Step (includes replays)**
```bash
curl http://localhost:8080/api/flow-executions/flow-steps/1/pipelines
```

**Get Specific Pipeline Execution**
```bash
curl http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000/pipelines/123
```

### 🔄 Replay/Resume Failed Executions

#### 8. Replay Failed Flow from Specific Step

**Replay from Failed Step**
```bash
curl -X POST http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000/replay/2 \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "flowId": 1,
  "startTime": "2024-01-15T11:00:00",
  "status": "RUNNING",
  "runtimeVariables": {
    "USER_ID": "12345",
    "SESSION_TOKEN": "abc123def456"
  },
  "createdAt": "2024-01-15T11:00:00"
}
```

**Key Features of Replay:**
- Automatically extracts all runtime variables from successful steps before the failed step
- Creates a new FlowExecution with pre-populated runtime data
- Resumes execution from the specified failed step onwards
- All pipeline executions are marked with `isReplay: true` and reference the original execution
- Both original and replay pipelines appear in monitoring endpoints

### 📊 Analytics & Metrics

#### 9. Analytics Endpoints

**Overall Metrics Summary**
```bash
curl http://localhost:8080/api/metrics/summary
```

**Execution Statistics**
```bash
# Group by flow
curl http://localhost:8080/api/metrics/executions?groupBy=flow

# Group by application
curl http://localhost:8080/api/metrics/executions?groupBy=application

# Group by pipeline
curl http://localhost:8080/api/metrics/executions?groupBy=pipeline
```

**Duration Statistics**
```bash
# Flow duration stats
curl http://localhost:8080/api/metrics/duration?type=flow

# Pipeline duration stats
curl http://localhost:8080/api/metrics/duration?type=pipeline
```

**Trend Analysis**
```bash
# Pass/Fail trends by day for last 30 days
curl http://localhost:8080/api/metrics/trends/pass-fail?period=day&days=30

# Duration trends by week
curl http://localhost:8080/api/metrics/trends/duration?period=week&days=90

# Top failing flows
curl http://localhost:8080/api/metrics/trends/failures?type=flow&limit=10
```

### 🔧 SquashTM Integration

#### 10. SquashTM Operations

**Get SquashTM Projects**
```bash
curl http://localhost:8080/api/squashtm/projects
```

**Get Test Cases from Project**
```bash
curl http://localhost:8080/api/squashtm/projects/1/test-cases
```

**Get Test Steps for Test Case**
```bash
curl http://localhost:8080/api/squashtm/test-cases/100/steps
```

**Associate Flow with Test Case**
```bash
curl -X POST http://localhost:8080/api/squashtm/associate \
  -H "Content-Type: application/json" \
  -d '{
    "flowId": 1,
    "testCaseId": 100
  }'
```

## 🎓 Complete Beginner's Guide

### Step-by-Step Flow Configuration for Absolute Beginners

This section provides a complete walkthrough for setting up and executing your first automated test flow.

#### Prerequisites Checklist
- [ ] Java 17+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] GitLab account with a project containing test automation code
- [ ] GitLab Personal Access Token with API permissions
- [ ] Basic understanding of REST APIs and JSON

#### Step 1: Start the Application

1. **Clone and Start**
   ```bash
   git clone <repository-url>
   cd pipeline-orchestrator
   mvn spring-boot:run
   ```

2. **Verify Application is Running**
   - Open browser: http://localhost:8080/swagger-ui.html
   - You should see the API documentation

#### Step 2: Create Your First Application (GitLab Project)

An "Application" represents a GitLab project that contains your test automation code.

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "gitlabProjectId": "your-project-id",
    "personalAccessToken": "glpat-your-token-here"
  }'
```

**How to find your GitLab Project ID:**
1. Go to your GitLab project
2. Look at the URL: `https://gitlab.com/username/project-name`
3. Or check project settings → General → Project ID

**Response:** Note the `id` field (e.g., `1`) - you'll need this for the next steps.

#### Step 3: Create Test Data (Optional but Recommended)

Test data contains variables that your tests need (credentials, URLs, etc.).

```bash
curl -X POST http://localhost:8080/api/test-data \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Staging Environment Data",
    "description": "Test data for staging environment",
    "data": {
      "BASE_URL": "https://staging.example.com",
      "USERNAME": "test.user@example.com",
      "PASSWORD": "SecurePassword123",
      "API_KEY": "staging-api-key-here"
    }
  }'
```

**Response:** Note the `id` field (e.g., `1`) for use in Flow Steps.

#### Step 4: Create Flow Steps

Flow Steps define individual test automation stages. Each step runs a GitLab pipeline.

**Example: Login Test Step**
```bash
curl -X POST http://localhost:8080/api/flow-steps \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": 1,
    "branch": "main",
    "testTag": "login-tests",
    "testStage": "test",
    "squashStepIds": [],
    "testDataIds": [1]
  }'
```

**Example: User Management Test Step**
```bash
curl -X POST http://localhost:8080/api/flow-steps \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": 1,
    "branch": "main", 
    "testTag": "user-management-tests",
    "testStage": "test",
    "squashStepIds": [],
    "testDataIds": [1]
  }'
```

**Field Explanations:**
- `applicationId`: The ID from Step 2
- `branch`: Git branch to run tests on
- `testTag`: Maven test tag (e.g., `-Dtest.tag=login-tests`)
- `testStage`: GitLab CI stage name that runs tests
- `testDataIds`: Array of test data IDs from Step 3

**Response:** Note the `id` fields (e.g., `1`, `2`) for the next step.

#### Step 5: Create a Flow

A Flow defines the sequence of Flow Steps to execute.

```bash
curl -X POST http://localhost:8080/api/flows \
  -H "Content-Type: application/json" \
  -d '{
    "flowStepIds": [1, 2],
    "squashTestCaseId": null
  }'
```

**Field Explanations:**
- `flowStepIds`: Array of Flow Step IDs in execution order
- `squashTestCaseId`: Optional SquashTM test case ID

**Response:** Note the `id` field (e.g., `1`) for execution.

#### Step 6: Execute Your Flow

```bash
curl -X POST http://localhost:8080/api/flows/1/execute
```

**Response Example:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "flowId": 1,
  "startTime": "2024-01-15T10:30:00",
  "status": "RUNNING",
  "runtimeVariables": {},
  "createdAt": "2024-01-15T10:30:00"
}
```

**Important:** Save the `id` (UUID) - this is your Flow Execution ID for monitoring.

#### Step 7: Monitor Your Execution

**Check Overall Status:**
```bash
curl http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000
```

**Check Pipeline Details:**
```bash
curl http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000/pipelines
```

#### Step 8: Handle Failures and Replay

If a step fails, you can replay from that step:

```bash
curl -X POST http://localhost:8080/api/flow-executions/550e8400-e29b-41d4-a716-446655440000/replay/2
```

This creates a new execution that:
- Starts from Flow Step ID `2`
- Automatically includes all runtime variables from successful previous steps
- Creates new pipeline executions marked as replays

### Understanding Runtime Variables

**How Variables Flow Between Steps:**

1. **Step 1 Execution:**
   - Input: Test Data from `testDataIds`
   - Output: `output.env` file with new variables
   - Example output.env:
     ```bash
     USER_ID=12345
     SESSION_TOKEN=abc123def456
     ```

2. **Step 2 Execution:**
   - Input: Test Data + Runtime variables from Step 1
   - Available variables: `BASE_URL`, `USERNAME`, `PASSWORD`, `USER_ID`, `SESSION_TOKEN`
   - Output: Additional variables in `output.env`

3. **Step 3 Execution:**
   - Input: Test Data + All accumulated runtime variables
   - And so on...

### GitLab CI Configuration

Your `.gitlab-ci.yml` should include a stage that generates `output.env`:

```yaml
stages:
  - test

test:
  stage: test
  script:
    - echo "Running tests with tag: $TEST_TAG"
    - mvn test -Dtest.tag=$TEST_TAG
    - echo "USER_ID=12345" >> target/output.env
    - echo "SESSION_TOKEN=$(generate_token)" >> target/output.env
  artifacts:
    paths:
      - target/output.env
    expire_in: 1 hour
  only:
    - main
```

### Common Troubleshooting

**Problem:** GitLab pipeline not triggering
- **Solution:** Check GitLab project ID and access token permissions

**Problem:** No runtime variables passed between steps
- **Solution:** Ensure your GitLab job creates `target/output.env` as an artifact

**Problem:** Flow execution stuck in RUNNING
- **Solution:** Check GitLab pipeline status manually; may need to cancel and replay

**Problem:** Step fails immediately
- **Solution:** Check application configuration and GitLab connectivity

### Best Practices for Beginners

1. **Start Simple:** Begin with one Flow Step, then add more
2. **Test Incrementally:** Test each Flow Step individually before combining
3. **Use Mock Mode:** Set `gitlab.mock-mode: true` for initial testing
4. **Monitor Logs:** Check application logs for detailed error messages
5. **Version Control:** Keep your Flow configurations in version control
6. **Documentation:** Document your test tags and expected variables

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
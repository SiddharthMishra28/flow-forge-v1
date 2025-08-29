#!/bin/bash

# Test script for Pipeline Orchestrator API
# This script demonstrates the complete workflow using the mock mode

BASE_URL="http://localhost:8080"

echo "🚀 Testing Pipeline Orchestrator API"
echo "======================================"

# 1. Create an Application
echo "1. Creating Application..."
APP_RESPONSE=$(curl -s -X POST "$BASE_URL/api/applications" \
  -H "Content-Type: application/json" \
  -d '{
    "gitlabProjectId": "12345",
    "personalAccessToken": "mock-token-for-testing"
  }')

APP_ID=$(echo $APP_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "   ✅ Application created with ID: $APP_ID"

# 2. Create Flow Steps
echo "2. Creating Flow Steps..."

# Step 1
STEP1_RESPONSE=$(curl -s -X POST "$BASE_URL/api/flow-steps" \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": '$APP_ID',
    "branch": "main",
    "testTag": "smoke-tests",
    "testStage": "test",
    "squashStepIds": [101, 102],
    "initialTestData": {
      "USERNAME": "testuser",
      "PASSWORD": "testpass",
      "ENVIRONMENT": "staging"
    }
  }')

STEP1_ID=$(echo $STEP1_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "   ✅ Flow Step 1 created with ID: $STEP1_ID"

# Step 2
STEP2_RESPONSE=$(curl -s -X POST "$BASE_URL/api/flow-steps" \
  -H "Content-Type: application/json" \
  -d '{
    "applicationId": '$APP_ID',
    "branch": "main",
    "testTag": "integration-tests",
    "testStage": "integration",
    "squashStepIds": [103, 104],
    "initialTestData": {
      "API_ENDPOINT": "https://api.staging.example.com",
      "TIMEOUT": "30"
    }
  }')

STEP2_ID=$(echo $STEP2_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "   ✅ Flow Step 2 created with ID: $STEP2_ID"

# 3. Create a Flow
echo "3. Creating Flow..."
FLOW_RESPONSE=$(curl -s -X POST "$BASE_URL/api/flows" \
  -H "Content-Type: application/json" \
  -d '{
    "flowStepIds": ['$STEP1_ID', '$STEP2_ID'],
    "squashTestCaseId": 500,
    "globalVariables": {
      "GLOBAL_ENV": "staging",
      "GLOBAL_TIMEOUT": "60",
      "TEST_SUITE": "end-to-end"
    }
  }')

FLOW_ID=$(echo $FLOW_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
echo "   ✅ Flow created with ID: $FLOW_ID"

# 4. Execute the Flow
echo "4. Executing Flow..."
EXECUTION_RESPONSE=$(curl -s -X POST "$BASE_URL/api/flows/$FLOW_ID/execute")
EXECUTION_ID=$(echo $EXECUTION_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "   ✅ Flow execution started with ID: $EXECUTION_ID"

# 5. Monitor execution progress
echo "5. Monitoring execution progress..."
for i in {1..10}; do
  echo "   Checking status (attempt $i/10)..."
  
  STATUS_RESPONSE=$(curl -s "$BASE_URL/api/flow-executions/$EXECUTION_ID")
  STATUS=$(echo $STATUS_RESPONSE | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
  
  echo "   Current status: $STATUS"
  
  if [ "$STATUS" = "PASSED" ] || [ "$STATUS" = "FAILED" ]; then
    break
  fi
  
  sleep 3
done

# 6. Get final execution details
echo "6. Getting final execution details..."
curl -s "$BASE_URL/api/flow-executions/$EXECUTION_ID" | jq '.' 2>/dev/null || echo "   (Install jq for pretty JSON output)"

# 7. Get pipeline executions
echo "7. Getting pipeline executions..."
curl -s "$BASE_URL/api/flow-executions/$EXECUTION_ID/pipelines" | jq '.' 2>/dev/null || echo "   (Install jq for pretty JSON output)"

echo ""
echo "🎉 Test completed!"
echo "📊 You can view the Swagger UI at: $BASE_URL/swagger-ui.html"
echo "🗄️  You can view the H2 Console at: $BASE_URL/h2-console"
echo "    JDBC URL: jdbc:h2:mem:testdb"
echo "    Username: sa"
echo "    Password: password"
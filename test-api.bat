@echo off
REM Test script for Pipeline Orchestrator API (Windows)
REM This script demonstrates the complete workflow using the mock mode

set BASE_URL=http://localhost:8080

echo 🚀 Testing Pipeline Orchestrator API
echo ======================================

REM 1. Create an Application
echo 1. Creating Application...
curl -s -X POST "%BASE_URL%/api/applications" ^
  -H "Content-Type: application/json" ^
  -d "{\"gitlabProjectId\": \"12345\", \"personalAccessToken\": \"mock-token-for-testing\"}" > app_response.json

echo    ✅ Application created - check app_response.json for details

REM 2. Create Flow Steps
echo 2. Creating Flow Steps...
curl -s -X POST "%BASE_URL%/api/flow-steps" ^
  -H "Content-Type: application/json" ^
  -d "{\"applicationId\": 1, \"branch\": \"main\", \"testTag\": \"smoke-tests\", \"testStage\": \"test\", \"squashStepIds\": [101, 102], \"initialTestData\": {\"USERNAME\": \"testuser\", \"PASSWORD\": \"testpass\", \"ENVIRONMENT\": \"staging\"}}" > step1_response.json

curl -s -X POST "%BASE_URL%/api/flow-steps" ^
  -H "Content-Type: application/json" ^
  -d "{\"applicationId\": 1, \"branch\": \"main\", \"testTag\": \"integration-tests\", \"testStage\": \"integration\", \"squashStepIds\": [103, 104], \"initialTestData\": {\"API_ENDPOINT\": \"https://api.staging.example.com\", \"TIMEOUT\": \"30\"}}" > step2_response.json

echo    ✅ Flow Steps created - check step1_response.json and step2_response.json

REM 3. Create a Flow
echo 3. Creating Flow...
curl -s -X POST "%BASE_URL%/api/flows" ^
  -H "Content-Type: application/json" ^
  -d "{\"flowStepIds\": [1, 2], \"squashTestCaseId\": 500, \"globalVariables\": {\"GLOBAL_ENV\": \"staging\", \"GLOBAL_TIMEOUT\": \"60\", \"TEST_SUITE\": \"end-to-end\"}}" > flow_response.json

echo    ✅ Flow created - check flow_response.json

REM 4. Execute the Flow
echo 4. Executing Flow...
curl -s -X POST "%BASE_URL%/api/flows/1/execute" > execution_response.json
echo    ✅ Flow execution started - check execution_response.json

REM 5. Wait a bit and check status
echo 5. Waiting for execution to complete...
timeout /t 10 /nobreak > nul

REM Get execution status (you'll need to extract the UUID from execution_response.json manually)
echo 6. Check execution status manually using the UUID from execution_response.json
echo    Example: curl "%BASE_URL%/api/flow-executions/{UUID}"

echo.
echo 🎉 Test completed!
echo 📊 You can view the Swagger UI at: %BASE_URL%/swagger-ui.html
echo 🗄️  You can view the H2 Console at: %BASE_URL%/h2-console
echo     JDBC URL: jdbc:h2:mem:testdb
echo     Username: sa
echo     Password: password

REM Clean up temp files
del app_response.json step1_response.json step2_response.json flow_response.json execution_response.json 2>nul

pause
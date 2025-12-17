오케스트레이션

curl -X POST http://localhost:8081/api/orchestration/transfer \
-H "Content-Type: application/json" \
-d '{
"fromAccountNumber": "1000-0001",
"toAccountNumber": "1000-0002",
"amount": 100000
}'


코레오그래피

curl -X POST http://localhost:8081/api/choreography/transfer \
-H "Content-Type: application/json" \
-d '{
"fromAccountNumber": "1000-0001",
"toAccountNumber": "1000-0002",
"amount": 100000
}'
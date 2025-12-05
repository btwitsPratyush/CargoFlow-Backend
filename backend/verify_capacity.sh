#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo "1. Registering Transporter..."
TRANSPORTER_RES=$(curl -s -X POST $BASE_URL/transporters \
-H "Content-Type: application/json" \
-d '{"companyName": "TestTransporter_'$(date +%s)'"}')
echo "Response: $TRANSPORTER_RES"

TRANSPORTER_ID=$(echo $TRANSPORTER_RES | grep -o '"transporterId":"[^"]*' | cut -d'"' -f4)
echo "Transporter ID: $TRANSPORTER_ID"
echo "--------------------------------"

echo "2. Setting Capacity to 5 Trucks..."
curl -s -X PUT $BASE_URL/transporters/$TRANSPORTER_ID/trucks \
-H "Content-Type: application/json" \
-d '{"truckType": "FLATBED", "count": 5}'
echo "Capacity Updated."
echo "--------------------------------"

echo "3. Creating Load..."
LOAD_RES=$(curl -s -X POST $BASE_URL/loads \
-H "Content-Type: application/json" \
-d '{
  "shipperId": "shipper1",
  "loadingCity": "Delhi",
  "unloadingCity": "Mumbai",
  "loadingDate": "2025-12-10T10:00:00.000+00:00",
  "productType": "Steel",
  "weight": 1000,
  "weightUnit": "KG",
  "truckType": "FLATBED",
  "noOfTrucks": 10
}')
echo "Response: $LOAD_RES"

LOAD_ID=$(echo $LOAD_RES | grep -o '"loadId":"[^"]*' | cut -d'"' -f4)
echo "Load ID: $LOAD_ID"
echo "--------------------------------"

echo "4. Attempting to Bid 6 Trucks (Expected: FAILURE)..."
BID_FAIL_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/bids \
-H "Content-Type: application/json" \
-d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANSPORTER_ID'",
  "proposedRate": 5000,
  "trucksOffered": 6
}')

if [ "$BID_FAIL_STATUS" -eq 400 ] || [ "$BID_FAIL_STATUS" -eq 409 ]; then
    echo "✅ SUCCESS: Bid failed as expected with status $BID_FAIL_STATUS"
else
    echo "❌ FAILURE: Bid should have failed but got status $BID_FAIL_STATUS"
fi
echo "--------------------------------"

echo "5. Attempting to Bid 5 Trucks (Expected: SUCCESS)..."
BID_SUCCESS_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/bids \
-H "Content-Type: application/json" \
-d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANSPORTER_ID'",
  "proposedRate": 5000,
  "trucksOffered": 5
}')

if [ "$BID_SUCCESS_STATUS" -eq 201 ]; then
    echo "✅ SUCCESS: Bid accepted as expected with status $BID_SUCCESS_STATUS"
else
    echo "❌ FAILURE: Bid should have succeeded but got status $BID_SUCCESS_STATUS"
fi
echo "--------------------------------"

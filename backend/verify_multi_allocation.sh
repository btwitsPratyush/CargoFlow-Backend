#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo "1. Registering Transporter A..."
TRANS_A_RES=$(curl -s -X POST $BASE_URL/transporters \
-H "Content-Type: application/json" \
-d '{"companyName": "TransporterA_'$(date +%s)'"}')
TRANS_A_ID=$(echo $TRANS_A_RES | grep -o '"transporterId":"[^"]*' | cut -d'"' -f4)
echo "Transporter A ID: $TRANS_A_ID"

echo "2. Registering Transporter B..."
TRANS_B_RES=$(curl -s -X POST $BASE_URL/transporters \
-H "Content-Type: application/json" \
-d '{"companyName": "TransporterB_'$(date +%s)'"}')
TRANS_B_ID=$(echo $TRANS_B_RES | grep -o '"transporterId":"[^"]*' | cut -d'"' -f4)
echo "Transporter B ID: $TRANS_B_ID"

echo "3. Setting Capacity (A=5, B=5)..."
curl -s -X PUT $BASE_URL/transporters/$TRANS_A_ID/trucks -H "Content-Type: application/json" -d '{"truckType": "FLATBED", "count": 5}'
curl -s -X PUT $BASE_URL/transporters/$TRANS_B_ID/trucks -H "Content-Type: application/json" -d '{"truckType": "FLATBED", "count": 5}'

echo "4. Creating Load (10 Trucks)..."
LOAD_RES=$(curl -s -X POST $BASE_URL/loads \
-H "Content-Type: application/json" \
-d '{
  "shipperId": "shipper_multi",
  "loadingCity": "Delhi",
  "unloadingCity": "Kolkata",
  "loadingDate": "2025-12-20T10:00:00.000+00:00",
  "productType": "Coal",
  "weight": 1000,
  "weightUnit": "KG",
  "truckType": "FLATBED",
  "noOfTrucks": 10
}')
LOAD_ID=$(echo $LOAD_RES | grep -o '"loadId":"[^"]*' | cut -d'"' -f4)
echo "Load ID: $LOAD_ID"

echo "5. Transporter A Bids 5 Trucks..."
BID_A_RES=$(curl -s -X POST $BASE_URL/bids \
-H "Content-Type: application/json" \
-d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANS_A_ID'",
  "proposedRate": 5000,
  "trucksOffered": 5
}')
BID_A_ID=$(echo $BID_A_RES | grep -o '"bidId":"[^"]*' | cut -d'"' -f4)

echo "6. Booking A (5 Trucks)..."
BOOKING_A_RES=$(curl -s -X POST $BASE_URL/bookings -H "Content-Type: application/json" -d '{"bidId": "'$BID_A_ID'"}' -v)
echo "Response A: $BOOKING_A_RES"

echo "7. Verifying Load Status (Should be OPEN_FOR_BIDS)..."
LOAD_CHECK_1=$(curl -s -X GET $BASE_URL/loads/$LOAD_ID)
STATUS_1=$(echo $LOAD_CHECK_1 | grep -o '"status":"[^"]*' | cut -d'"' -f4)

if [ "$STATUS_1" == "OPEN_FOR_BIDS" ]; then
    echo "✅ Status Correct: OPEN_FOR_BIDS (Partial Booking)"
else
    echo "❌ Status Incorrect: $STATUS_1"
fi

echo "8. Transporter B Bids 5 Trucks..."
BID_B_RES=$(curl -s -X POST $BASE_URL/bids \
-H "Content-Type: application/json" \
-d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANS_B_ID'",
  "proposedRate": 5000,
  "trucksOffered": 5
}')
BID_B_ID=$(echo $BID_B_RES | grep -o '"bidId":"[^"]*' | cut -d'"' -f4)

echo "9. Booking B (5 Trucks)..."
BOOKING_B_RES=$(curl -s -X POST $BASE_URL/bookings -H "Content-Type: application/json" -d '{"bidId": "'$BID_B_ID'"}' -v)
echo "Response B: $BOOKING_B_RES"

echo "10. Verifying Load Status (Should be BOOKED)..."
LOAD_CHECK_2=$(curl -s -X GET $BASE_URL/loads/$LOAD_ID)
STATUS_2=$(echo $LOAD_CHECK_2 | grep -o '"status":"[^"]*' | cut -d'"' -f4)

if [ "$STATUS_2" == "BOOKED" ]; then
    echo "✅ Status Correct: BOOKED (Fully Allocated)"
else
    echo "❌ Status Incorrect: $STATUS_2"
fi

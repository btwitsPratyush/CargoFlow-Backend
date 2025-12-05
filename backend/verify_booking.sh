#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo "1. Registering Transporter..."
TRANSPORTER_RES=$(curl -s -X POST $BASE_URL/transporters \
-H "Content-Type: application/json" \
-d '{"companyName": "BookingTransporter_'$(date +%s)'"}')
TRANSPORTER_ID=$(echo $TRANSPORTER_RES | grep -o '"transporterId":"[^"]*' | cut -d'"' -f4)
echo "Transporter ID: $TRANSPORTER_ID"

echo "2. Setting Capacity to 10 Trucks..."
curl -s -X PUT $BASE_URL/transporters/$TRANSPORTER_ID/trucks \
-H "Content-Type: application/json" \
-d '{"truckType": "FLATBED", "count": 10}'
echo "Capacity Set."

echo "3. Creating Load (10 Trucks)..."
LOAD_RES=$(curl -s -X POST $BASE_URL/loads \
-H "Content-Type: application/json" \
-d '{
  "shipperId": "shipper_booking",
  "loadingCity": "Delhi",
  "unloadingCity": "Chennai",
  "loadingDate": "2025-12-15T10:00:00.000+00:00",
  "productType": "Electronics",
  "weight": 500,
  "weightUnit": "KG",
  "truckType": "FLATBED",
  "noOfTrucks": 10
}')
LOAD_ID=$(echo $LOAD_RES | grep -o '"loadId":"[^"]*' | cut -d'"' -f4)
echo "Load ID: $LOAD_ID"

echo "4. Submitting Bid (10 Trucks)..."
BID_RES=$(curl -s -X POST $BASE_URL/bids \
-H "Content-Type: application/json" \
-d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANSPORTER_ID'",
  "proposedRate": 10000,
  "trucksOffered": 10
}')
BID_ID=$(echo $BID_RES | grep -o '"bidId":"[^"]*' | cut -d'"' -f4)
echo "Bid ID: $BID_ID"

echo "5. Creating Booking (Accepting Bid)..."
BOOKING_RES=$(curl -s -X POST $BASE_URL/bookings \
-H "Content-Type: application/json" \
-d '{
  "bidId": "'$BID_ID'"
}')
BOOKING_STATUS=$(echo $BOOKING_RES | grep -o '"status":"[^"]*' | cut -d'"' -f4)

if [ "$BOOKING_STATUS" == "CONFIRMED" ]; then
    echo "✅ Booking CONFIRMED"
else
    echo "❌ Booking Failed: $BOOKING_RES"
    exit 1
fi

echo "6. Verifying Load Status is BOOKED..."
LOAD_CHECK=$(curl -s -X GET $BASE_URL/loads/$LOAD_ID)
LOAD_STATUS=$(echo $LOAD_CHECK | grep -o '"status":"[^"]*' | cut -d'"' -f4)

if [ "$LOAD_STATUS" == "BOOKED" ]; then
    echo "✅ Load Status is BOOKED"
else
    echo "❌ Load Status Mismatch: $LOAD_STATUS"
fi

echo "7. Verifying Transporter Capacity Deducted (Should be 0)..."
# We don't have a direct API to check capacity, but we can try to bid again with 1 truck.
# It should fail if capacity is 0.
FAIL_BID_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/bids \
-H "Content-Type: application/json" \
-d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANSPORTER_ID'",
  "proposedRate": 1000,
  "trucksOffered": 1
}')

if [ "$FAIL_BID_STATUS" -eq 400 ] || [ "$FAIL_BID_STATUS" -eq 409 ]; then
    echo "✅ Capacity Deduction Verified (Bid Failed as expected)"
else
    echo "❌ Capacity Deduction Failed (Bid Succeeded with status $FAIL_BID_STATUS)"
fi

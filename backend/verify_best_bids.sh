#!/bin/bash

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo "1. Registering Transporter A (Rating 5.0)..."
# Note: Default rating is 5.0
TRANS_A_RES=$(curl -s -X POST $BASE_URL/transporters -H "Content-Type: application/json" -d '{"companyName": "BestBid_A_'$(date +%s)'"}')
TRANS_A_ID=$(echo $TRANS_A_RES | grep -o '"transporterId":"[^"]*' | cut -d'"' -f4)

echo "2. Registering Transporter B (Rating 5.0)..."
TRANS_B_RES=$(curl -s -X POST $BASE_URL/transporters -H "Content-Type: application/json" -d '{"companyName": "BestBid_B_'$(date +%s)'"}')
TRANS_B_ID=$(echo $TRANS_B_RES | grep -o '"transporterId":"[^"]*' | cut -d'"' -f4)

echo "3. Setting Capacity..."
curl -s -X PUT $BASE_URL/transporters/$TRANS_A_ID/trucks -H "Content-Type: application/json" -d '{"truckType": "FLATBED", "count": 10}'
curl -s -X PUT $BASE_URL/transporters/$TRANS_B_ID/trucks -H "Content-Type: application/json" -d '{"truckType": "FLATBED", "count": 10}'

echo "4. Creating Load..."
LOAD_RES=$(curl -s -X POST $BASE_URL/loads \
-H "Content-Type: application/json" \
-d '{
  "shipperId": "shipper_best",
  "loadingCity": "Delhi",
  "unloadingCity": "Pune",
  "loadingDate": "2025-12-25T10:00:00.000+00:00",
  "productType": "Wood",
  "weight": 2000,
  "weightUnit": "KG",
  "truckType": "FLATBED",
  "noOfTrucks": 5
}')
LOAD_ID=$(echo $LOAD_RES | grep -o '"loadId":"[^"]*' | cut -d'"' -f4)

echo "5. Transporter A Bids $1000..."
curl -s -X POST $BASE_URL/bids -H "Content-Type: application/json" -d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANS_A_ID'",
  "proposedRate": 1000,
  "trucksOffered": 1
}' > /dev/null

echo "6. Transporter B Bids $900 (Cheaper)..."
curl -s -X POST $BASE_URL/bids -H "Content-Type: application/json" -d '{
  "loadId": "'$LOAD_ID'",
  "transporterId": "'$TRANS_B_ID'",
  "proposedRate": 900,
  "trucksOffered": 1
}' > /dev/null

echo "7. Fetching Best Bids..."
BEST_BIDS=$(curl -s -X GET $BASE_URL/loads/$LOAD_ID/best-bids)

echo "Response: $BEST_BIDS"

# Extract first bid's transporter ID
FIRST_BID_TRANS_ID=$(echo $BEST_BIDS | grep -o '"transporterId":"[^"]*' | head -1 | cut -d'"' -f4)

if [ "$FIRST_BID_TRANS_ID" == "$TRANS_B_ID" ]; then
    echo "✅ SUCCESS: Transporter B (Cheaper) is ranked first."
else
    echo "❌ FAILURE: Transporter B should be first."
fi

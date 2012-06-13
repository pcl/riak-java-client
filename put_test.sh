#!/bin/bash
date +%H:%M:%S:%N

prefix=`date +%s`
for (( c=1; c<=10000; c++ ))
do
  curl  -X PUT -d'{"ClientID":"2","DocumentID":"2" , "ClientAgreementID":"12345678901","DocDescription":"Description of this","CategoryDescription":"This is a catogory description","CustomerNote":"This is a customer Note ", "CreationDate":"mm/dd/yyyy","EffectiveDate":"mm/dd/yyyy", "ExpirrationDate":"mm/dd/yyyy","BillDueDate":"mm/dd/yyyy","AvailableDate":"mm/dd/yyyy","HideShow":"Y", "DocStatusCD":"Y","PolicyDesc":"This is a policy Description","DeliveryChannel":"this is dcnel"}' -H "Content-Type: application/json" -H "x-riak-index-Field1_int: 1" $1/curl_put/$prefix$c  
done
 
date +%H:%M:%S:%N

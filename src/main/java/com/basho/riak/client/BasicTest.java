package com.basho.riak.client;

import com.basho.riak.client.IRiakClient;
import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.RiakException;
import com.basho.riak.client.RiakFactory;
import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.builders.RiakObjectBuilder;
import com.basho.riak.client.http.util.Constants;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;

public class BasicTest 
{
    public static void main( String[] args ) throws IOException, RiakException
    {
        IRiakClient client = RiakFactory.httpClient(args[0]);

        int count = 1;

        long tot_time1 = System.currentTimeMillis();
        while (count <= 10000)
        {
            Bucket b = client.fetchBucket("rjc_create").lazyLoadBucketProperties().execute();
            String key = RandomStringUtils.random(12);
            String value = "{\"ClientID\":\"2\",\"DocumentID\":\"2\" , \"ClientAgreementID\":\"12345678901\",\"DocDescription\":\"Description of this\",\"CategoryDescription\":\"This is a catogory description\",\"CustomerNote\":\"This is a customer Note \", \"CreationDate\":\"mm/dd/yyyy\",\"EffectiveDate\":\"mm/dd/yyyy\", \"ExpirrationDate\":\"mm/dd/yyyy\",\"BillDueDate\":\"mm/dd/yyyy\",\"AvailableDate\":\"mm/dd/yyyy\",\"HideShow\":\"Y\", \"DocStatusCD\":\"Y\",\"PolicyDesc\":\"This is a policy Description\",\"DeliveryChannel\":\"this is dcnel\"}";

            IRiakObject o = RiakObjectBuilder.newBuilder("rjc_create", key)
                .withContentType(Constants.CTYPE_JSON_UTF8)
                .addIndex("Field_1", 1)
                .withValue(value)
                .build();

            long t = System.currentTimeMillis();
            b.store(o).execute();
            long t2 = System.currentTimeMillis();

            long t3 = t2 - t;
            System.out.println("time: " + t3);
            count++;
        }

        long tot_time2 = System.currentTimeMillis();
        long tot_time = tot_time2 - tot_time1;
        System.out.println("Total Time: " + (tot_time/1000));
        System.out.println("Transactions Per Second: " + (10000 / (tot_time/1000)));
        client.shutdown();	
    }
}
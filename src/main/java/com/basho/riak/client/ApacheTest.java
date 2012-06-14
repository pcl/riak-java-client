package com.basho.riak.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import java.io.IOException;

public class ApacheTest {

    public static void main( String[] args ) throws IOException, RiakException
    {

        ThreadSafeClientConnManager tsccm = new ThreadSafeClientConnManager();
		tsccm.setMaxTotal(50);
		tsccm.setDefaultMaxPerRoute(50);
        DefaultHttpClient client = new DefaultHttpClient(tsccm);

        int count = 1;

        long tot_time1 = System.currentTimeMillis();
        while (count <= 10000)
        {
            String key = tot_time1 + "00" + count;
            String value = "{\"ClientID\":\"2\",\"DocumentID\":\"2\" , \"ClientAgreementID\":\"12345678901\",\"DocDescription\":\"Description of this\",\"CategoryDescription\":\"This is a catogory description\",\"CustomerNote\":\"This is a customer Note \", \"CreationDate\":\"mm/dd/yyyy\",\"EffectiveDate\":\"mm/dd/yyyy\", \"ExpirrationDate\":\"mm/dd/yyyy\",\"BillDueDate\":\"mm/dd/yyyy\",\"AvailableDate\":\"mm/dd/yyyy\",\"HideShow\":\"Y\", \"DocStatusCD\":\"Y\",\"PolicyDesc\":\"This is a policy Description\",\"DeliveryChannel\":\"this is dcnel\"}";
            StringEntity stringEntity = new StringEntity(value);

            String url = args[0] + "/apache_create/" + key;
            HttpPut httpPut = new HttpPut(url);

            httpPut.setEntity(stringEntity);
            httpPut.addHeader("x-riak-index-partyid_bin", "1");
            httpPut.addHeader("Content-Type", "application/json");

            long t = System.currentTimeMillis();
            HttpResponse response = client.execute(httpPut);
            long t2 = System.currentTimeMillis();

            long t3 = t2 - t;
            System.out.println("time: " + t3);
            count++;
        }

        long tot_time2 = System.currentTimeMillis();
        long tot_time = tot_time2 - tot_time1;
        System.out.println("Total Time: " + (tot_time/1000));
        System.out.println("Transactions Per Second: " + (10000 / (tot_time/1000)));
    }

}

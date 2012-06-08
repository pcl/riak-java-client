/*
 * This file is provided to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.basho.riak.client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

import com.basho.riak.client.http.RiakConfig;
import com.basho.riak.client.http.util.ClientUtils;

/**
 * @author russell
 * 
 */
public class HTTPSFRunner implements SFRunner {

    private static final String SLASH = "/";
    private final HttpClient client;
    private final String url;

    public HTTPSFRunner(String url) {
        this.url = url;
        RiakConfig rc = new RiakConfig(url);
        rc.setMaxConnections(50);
        this.client = ClientUtils.newHttpClient(rc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.basho.riak.client.SFRunner#execute(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    public void execute(String bucket, String key, byte[] data, String indexData) throws IOException {

        ByteArrayEntity bae = new ByteArrayEntity(data);
        String url = new StringBuilder(this.url).append(SLASH).append(bucket).append(SLASH).append(key).toString();

        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(bae);
        httpPut.addHeader("x-riak-index-partyid_bin", indexData);
        HttpResponse resp = client.execute(httpPut);
        EntityUtils.consume(resp.getEntity());

    }

}

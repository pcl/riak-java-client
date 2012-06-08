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

import com.basho.riak.client.http.RiakClient;
import com.basho.riak.client.http.RiakConfig;
import com.basho.riak.client.http.RiakObject;
import com.basho.riak.client.http.request.RequestMeta;

/**
 * @author russell
 *
 */
public class OldClientRunner implements SFRunner {

    private final RiakClient client;
    
    public OldClientRunner(String url) {
        RiakConfig rc = new RiakConfig(url);
        rc.setMaxConnections(50);
        this.client = new RiakClient(rc);
    }
    
    /* (non-Javadoc)
     * @see com.basho.riak.client.SFRunner#execute(java.lang.String, java.lang.String, byte[], java.lang.String)
     */
    public void execute(String bucket, String key, byte[] data, String indexData) throws IOException {
        RiakObject ro = new RiakObject(client, bucket, key, data);
        RequestMeta rm = new RequestMeta();
        rm.setHeader("x-riak-index-partyid_bin", indexData);
        client.store(ro, rm);
    }

}

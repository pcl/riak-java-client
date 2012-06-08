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

import com.basho.riak.client.bucket.Bucket;
import com.basho.riak.client.builders.RiakObjectBuilder;
import com.basho.riak.client.raw.config.Configuration;

/**
 * @author russell
 *
 */
public class RiakClientSFRUnner implements SFRunner {

    final IRiakClient client;
    
    public RiakClientSFRUnner(Configuration conf) throws RiakException {
        client = RiakFactory.newClient(conf);
    }
    
    /* (non-Javadoc)
     * @see com.basho.riak.client.SFRunner#execute(java.lang.String, java.lang.String, byte[], java.lang.String)
     */
    public void execute(String bucket, String key, byte[] data, String indexData) throws IOException {
        IRiakObject obj = RiakObjectBuilder.newBuilder(bucket, key).withValue(data).build();
        obj.addIndex("x-riak-index-partyid_bin", indexData);
        
        try {
            Bucket b = client.fetchBucket(bucket).lazyLoadBucketProperties().execute();
            b.store(obj).execute();
        } catch (RiakRetryFailedException e) {
           throw new IOException(e);
        }
    }

}

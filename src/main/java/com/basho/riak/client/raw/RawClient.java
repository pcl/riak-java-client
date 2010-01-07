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
package com.basho.riak.client.raw;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.basho.riak.client.RiakBucketInfo;
import com.basho.riak.client.RiakClient;
import com.basho.riak.client.RiakConfig;
import com.basho.riak.client.RiakObject;
import com.basho.riak.client.request.RequestMeta;
import com.basho.riak.client.request.RiakWalkSpec;
import com.basho.riak.client.response.HttpResponse;
import com.basho.riak.client.response.RiakExceptionHandler;
import com.basho.riak.client.response.RiakResponseRuntimeException;
import com.basho.riak.client.response.StreamHandler;
import com.basho.riak.client.util.ClientHelper;
import com.basho.riak.client.util.Constants;

/**
 * Implementation of the {@link RiakClient} that connects to the Riak Raw
 * interface.
 */
public class RawClient implements RiakClient {

    private ClientHelper helper;

    public RawClient(RiakConfig config) {
        helper = new ClientHelper(config);
    }

    public RawClient(String url) {
        helper = new ClientHelper(new RiakConfig(url));
    }

    // Package protected constructor used for testing
    RawClient(ClientHelper helper) {
        this.helper = helper;
    }

    public HttpResponse setBucketSchema(String bucket, RiakBucketInfo bucketInfo, RequestMeta meta) {
        JSONObject schema = null;
        try {
            schema = new JSONObject().put(Constants.RAW_FL_SCHEMA, bucketInfo.getSchema());
        } catch (JSONException unreached) {
            throw new IllegalStateException("wrapping valid json should be valid", unreached);
        }

        return helper.setBucketSchema(bucket, schema, meta);
    }

    public HttpResponse setBucketSchema(String bucket, RiakBucketInfo bucketInfo) {
        return setBucketSchema(bucket, bucketInfo, null);
    }

    public RawBucketResponse listBucket(String bucket, RequestMeta meta) {
        HttpResponse r = helper.listBucket(bucket, meta);
        try {
            return getBucketResponse(r);
        } catch (JSONException e) {
            helper.toss(new RiakResponseRuntimeException(r, e));
            return null;
        }
    }

    public RawBucketResponse listBucket(String bucket) {
        return listBucket(bucket, null);
    }

    // Sends the object's link, user-defined metadata and vclock as HTTP headers
    // and the value as the body
    public RawStoreResponse store(RiakObject object, RequestMeta meta) {
        HttpResponse r = helper.store(object, meta);
        return new RawStoreResponse(r);
    }

    public RawStoreResponse store(RiakObject object) {
        return store(object, null);
    }

    public RawFetchResponse fetchMeta(String bucket, String key, RequestMeta meta) {
        try {
            return getFetchResponse(helper.fetchMeta(bucket, key, meta));
        } catch (RiakResponseRuntimeException e) {
            return new RawFetchResponse(helper.toss(e));
        }
    }

    public RawFetchResponse fetchMeta(String bucket, String key) {
        return fetchMeta(bucket, key, null);
    }

    // Fetches the object or all sibling objects if there are multiple
    public RawFetchResponse fetch(String bucket, String key, RequestMeta meta) {
        return fetch(bucket, key, meta, false);
    }

    public RawFetchResponse fetch(String bucket, String key) {
        return fetch(bucket, key, null, false);
    }

    /**
     * Similar to fetch(), except the HTTP connection is left open for
     * successful 2xx responses, and the Riak response is provided as a stream.
     * The user must remember to call RawFetchResponse.close() on the return
     * value.
     * 
     * Sibling responses (status code 300) must be read before parsing, so they
     * are not streamed. Therefore stream() is identical to fetch(), except that
     * getBody() returns null.
     * 
     * @param bucket
     *            The bucket containing the {@link RiakObject} to fetch.
     * @param key
     *            The key of the {@link RiakObject} to fetch.
     * @param meta
     *            Extra metadata to attach to the request such as an r- value
     *            for the request, HTTP headers, and other query parameters. See
     *            RequestMeta.readParams().
     * 
     * @return A streaming {@link RawFetchResponse} containing HTTP response
     *         information and the response stream. The HTTP connection must be
     *         closed manually by the user by calling
     *         {@link RawFetchResponse#close()}.
     */
    public RawFetchResponse stream(String bucket, String key, RequestMeta meta) {
        return fetch(bucket, key, meta, true);
    }

    public RawFetchResponse stream(String bucket, String key) {
        return fetch(bucket, key, null, true);
    }

    RawFetchResponse fetch(String bucket, String key, RequestMeta meta, boolean streamResponse) {
        if (meta == null) {
            meta = new RequestMeta();
        }

        String accept = meta.getHeader(Constants.HDR_ACCEPT);
        if (accept == null) {
            meta.setHeader(Constants.HDR_ACCEPT, Constants.CTYPE_ANY + ", " + Constants.CTYPE_MULTIPART_MIXED);
        } else {
            meta.setHeader(Constants.HDR_ACCEPT, accept + ", " + Constants.CTYPE_MULTIPART_MIXED);
        }

        HttpResponse r = helper.fetch(bucket, key, meta, streamResponse);

        try {
            return getFetchResponse(r);
        } catch (RiakResponseRuntimeException e) {
            return new RawFetchResponse(helper.toss(e));
        }

    }

    public boolean stream(String bucket, String key, StreamHandler handler, RequestMeta meta) throws IOException {
        return helper.stream(bucket, key, handler, meta);
    }

    public HttpResponse delete(String bucket, String key, RequestMeta meta) {
        return helper.delete(bucket, key, meta);
    }

    public HttpResponse delete(String bucket, String key) {
        return delete(bucket, key, null);
    }

    public RawWalkResponse walk(String bucket, String key, String walkSpec, RequestMeta meta) {
        HttpResponse r = helper.walk(bucket, key, walkSpec, meta);

        try {
            return getWalkResponse(r);
        } catch (RiakResponseRuntimeException e) {
            return new RawWalkResponse(helper.toss(e));
        }
    }

    public RawWalkResponse walk(String bucket, String key, String walkSpec) {
        return walk(bucket, key, walkSpec, null);
    }

    public RawWalkResponse walk(String bucket, String key, RiakWalkSpec walkSpec) {
        return walk(bucket, key, walkSpec.toString(), null);
    }

    /** The installed exception handler or null if not installed */
    public RiakExceptionHandler getExceptionHandler() {
        return helper.getExceptionHandler();
    }

    /**
     * If an exception handler is provided, then the Riak client will hand
     * exceptions to the handler rather than throwing them.
     */
    public void setExceptionHandler(RiakExceptionHandler exceptionHandler) {
        helper.setExceptionHandler(exceptionHandler);
    }

    // Encapsulate response creation so it can be stubbed for testing
    RawBucketResponse getBucketResponse(HttpResponse r) throws JSONException {
        return new RawBucketResponse(r);
    }

    RawFetchResponse getFetchResponse(HttpResponse r) throws RiakResponseRuntimeException {
        return new RawFetchResponse(r);
    }

    RawWalkResponse getWalkResponse(HttpResponse r) throws RiakResponseRuntimeException {
        return new RawWalkResponse(r);
    }
}

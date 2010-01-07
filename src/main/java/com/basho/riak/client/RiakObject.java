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

import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpMethod;

import com.basho.riak.client.response.StoreResponse;

/**
 * A Riak object
 */
public interface RiakObject {

    /**
     * Copy the metadata and value from <code>object</code>. The bucket and key
     * are not copied.
     * 
     * @param object
     *            The source object to copy from
     */
    public void copyData(RiakObject object);

    /**
     * Update the object's metadata. This usually happens when Riak returns
     * updated metadata from a store operation.
     * 
     * @param response
     *            Response from a store operation containing an updated vclock,
     *            last modified date, and vtag
     */
    public void updateMeta(StoreResponse response);

    /**
     * The object's bucket
     */
    public String getBucket();

    /**
     * The object's key
     */
    public String getKey();

    /**
     * The object's value
     */
    public String getValue();

    /**
     * Set the object's value
     */
    public void setValue(String value);

    /**
     * The object's links -- may be empty, but never be null. New links can be
     * added using getLinks().add()
     */
    public List<RiakLink> getLinks();

    /**
     * User-specified metadata for the object in the form of key-value pairs --
     * may be empty, but never be null. New key-value pairs can be added using
     * getUsermeta().put()
     */
    public Map<String, String> getUsermeta();

    /**
     * The object's content type as a MIME type
     */
    public String getContentType();

    /**
     * The object's opaque vclock assigned by Riak
     */
    public String getVclock();

    /**
     * The modification date of the object determined by Riak
     */
    public String getLastmod();

    /**
     * An entity tag for the object assigned by Riak
     */
    public String getVtag();

    /**
     * Serializes this object to an existing {@link HttpMethod} which can be
     * sent as an HTTP request. Used by {@link RiakClient} to create PUT
     * requests.
     */
    public void writeToHttpMethod(HttpMethod httpMethod);
}
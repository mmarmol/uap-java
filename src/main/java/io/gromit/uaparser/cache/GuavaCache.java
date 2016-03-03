/**
 * Copyright 2016 gromit.it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gromit.uaparser.cache;

import com.google.common.cache.CacheBuilder;

import io.gromit.uaparser.model.Device;
import io.gromit.uaparser.model.OS;
import io.gromit.uaparser.model.UserAgent;

/**
 * The Class GuavaCache.
 */
public class GuavaCache implements Cache{

    /** The Constant DEFAULT_CAPACITY. */
    private static final int DEFAULT_CAPACITY = 16384;
	
    /**
     * The Enum Type.
     */
    private enum Type {
    	
    	/** The os. */
    	os,
    	
    	/** The user agent. */
    	userAgent,
    	
    	/** The device. */
    	device;
    	
    	/**
	     * Key.
	     *
	     * @param userAgent the user agent
	     * @return the string
	     */
	    private String key(String userAgent){
    		return this.name()+":"+userAgent;
    	}
    	
    }
    
    /** The cache. */
    private final com.google.common.cache.Cache<String, Object> cache;

    /**
     * Instantiates a new guava cache.
     */
    public GuavaCache() {
    	cache = CacheBuilder.newBuilder().maximumSize(DEFAULT_CAPACITY).build();
    }

    /**
     * Instantiates a new guava cache.
     *
     * @param maximumSize the maximum size
     */
    public GuavaCache(int maximumSize) {
    	cache = CacheBuilder.newBuilder().maximumSize(maximumSize).build();
    }
    
    /**
     * Instantiates a new guava cache.
     *
     * @param cache the cache
     */
    public GuavaCache(com.google.common.cache.Cache<String, Object> cache){
    	this.cache = cache;
    }

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getOs(java.lang.String)
	 */
	@Override
	public OS getOs(String agentString) {
		Object object = cache.getIfPresent(Type.os.key(agentString));
		if(object == null){
			return null;
		}
		return (OS)object;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putOS(java.lang.String, io.gromit.uaparser.model.OS)
	 */
	@Override
	public void putOS(String agentString, OS object) {
		cache.put(Type.os.key(agentString), object);
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getDevice(java.lang.String)
	 */
	@Override
	public Device getDevice(String agentString) {
		Object object = cache.getIfPresent(Type.device.key(agentString));
		if(object == null){
			return null;
		}
		return (Device)object;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putDevice(java.lang.String, io.gromit.uaparser.model.Device)
	 */
	@Override
	public void putDevice(String agentString, Device object) {
		cache.put(Type.device.key(agentString), object);
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getUserAgent(java.lang.String)
	 */
	@Override
	public UserAgent getUserAgent(String agentString) {
		Object object = cache.getIfPresent(Type.userAgent.key(agentString));
		if(object == null){
			return null;
		}
		return (UserAgent)object;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putUserAgent(java.lang.String, io.gromit.uaparser.model.UserAgent)
	 */
	@Override
	public void putUserAgent(String agentString, UserAgent object) {
		cache.put(Type.userAgent.key(agentString), object);
	}

	@Override
	public void clean() {
		cache.invalidateAll();
	}
    
}

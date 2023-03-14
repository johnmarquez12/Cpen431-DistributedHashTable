package com.g10.CPEN431.A9;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.ByteString;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RequestReplyCache {

    private static RequestReplyCache INSTANCE;
    private final Cache<ByteString, Application.ApplicationResponse> cache;

    private RequestReplyCache() {
        cache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.SECONDS)
            .build();
    }

    public static RequestReplyCache getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RequestReplyCache();
        }

        return INSTANCE;
    }

    public Application.ApplicationResponse get(ByteString messageID, Callable<Application.ApplicationResponse> callable)
        throws ExecutionException {
        Application.ApplicationResponse response = cache.getIfPresent(messageID);

        if (response == null) {
            try {
                response = callable.call();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
            if(response.shouldCache())
                cache.put(messageID, response);

            return response;
        }

        return response;
    }

    public void wipeout() {
        cache.invalidateAll();
        cache.cleanUp();
    }
}

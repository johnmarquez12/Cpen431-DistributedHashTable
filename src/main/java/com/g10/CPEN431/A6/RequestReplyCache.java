package com.g10.CPEN431.A6;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.ByteString;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RequestReplyCache {

    private static RequestReplyCache INSTANCE;
    private final Cache<ByteString, ByteString> cache;

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
        ByteString response = cache.getIfPresent(messageID);
        Application.ApplicationResponse appResponse;


        if (response == null) {
            try {
                appResponse = callable.call();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
            if(appResponse.shouldCache())
                cache.put(messageID, appResponse.messageData());

            return appResponse;
        }

        return new Application.ApplicationResponse(true, response, null);
    }

    public void wipeout() {
        cache.invalidateAll();
        cache.cleanUp();
    }
}

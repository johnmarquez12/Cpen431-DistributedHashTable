package com.s36906949.CPEN431.A4;

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

    public ByteString get(ByteString messageID, Callable<ByteString> callable)
        throws ExecutionException {
        ByteString response = cache.getIfPresent(messageID);

        if (response == null) {
            try {
                response = callable.call();

            } catch (Exception e) {
                throw new ExecutionException(e);
            }
            cache.put(messageID, response);
        }

        return response;
    }

    public void wipeout() {
        cache.invalidateAll();
        cache.cleanUp();
    }
}

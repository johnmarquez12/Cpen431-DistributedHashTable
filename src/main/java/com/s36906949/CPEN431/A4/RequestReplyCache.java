package com.s36906949.CPEN431.A4;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.protobuf.ByteString;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.matei.eece411.util.StringUtils.byteArrayToHexString;

public class RequestReplyCache {

    private static RequestReplyCache INSTANCE;
    private Cache<ByteString, ByteString> cache;

    private RequestReplyCache() {
        cache = CacheBuilder.newBuilder()
//            .expireAfterAccess(1, TimeUnit.SECONDS)
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
        System.out.printf("Cache: Looking... %s%n",
            byteArrayToHexString(messageID.toByteArray()));

        return cache.get(messageID, callable);
    }
}

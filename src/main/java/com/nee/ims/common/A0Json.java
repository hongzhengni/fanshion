package com.nee.ims.common;

import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;

/**
 * Created by heikki on 17/8/24.
 */
public class A0Json extends Json {

    public static <T> T decodeValue(String str, TypeReference<T> tr) throws DecodeException {
        try {
            return mapper.readValue(str, tr);
        } catch (Exception var3) {
            throw new DecodeException("Failed to decode:" + var3.getMessage());
        }
    }
}

/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.platon.wallet.network;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * @author ziv
 */
public final class ApiFastjsonConverterFactory extends Retrofit2ConverterFactory {


    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private FastJsonConfig fastJsonConfig;

    @Deprecated
    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];
    @Deprecated
    private ParserConfig parserConfig = ParserConfig.getGlobalInstance();
    @Deprecated
    private int featureValues = JSON.DEFAULT_PARSER_FEATURE;
    @Deprecated
    private Feature[] features;
    @Deprecated
    private SerializeConfig serializeConfig;
    @Deprecated
    private SerializerFeature[] serializerFeatures;

    public ApiFastjsonConverterFactory() {
        this.fastJsonConfig = new FastJsonConfig();
    }

    public ApiFastjsonConverterFactory(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    public static ApiFastjsonConverterFactory create() {
        return create(new FastJsonConfig());
    }

    public static ApiFastjsonConverterFactory create(FastJsonConfig fastJsonConfig) {
        if (fastJsonConfig == null) throw new NullPointerException("fastJsonConfig == null");
        return new ApiFastjsonConverterFactory(fastJsonConfig);
    }

    @Override
    public Converter<ResponseBody, Object> responseBodyConverter(Type type, //
                                                                 Annotation[] annotations, //
                                                                 Retrofit retrofit) {
        return new ApiFastjsonConverterFactory.ResponseBodyConverter<Object>(type);
    }

    @Override
    public Converter<Object, RequestBody> requestBodyConverter(Type type, //
                                                               Annotation[] parameterAnnotations, //
                                                               Annotation[] methodAnnotations, //
                                                               Retrofit retrofit) {
        return new ApiFastjsonConverterFactory.RequestBodyConverter<Object>();
    }

    public FastJsonConfig getFastJsonConfig() {
        return fastJsonConfig;
    }

    public Retrofit2ConverterFactory setFastJsonConfig(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
        return this;
    }

    /**
     * Gets parser config.
     *
     * @return the parser config
     * @see FastJsonConfig#getParserConfig()
     * @deprecated
     */
    @Deprecated
    public ParserConfig getParserConfig() {
        return fastJsonConfig.getParserConfig();
    }

    /**
     * Sets parser config.
     *
     * @param config the config
     * @return the parser config
     * @see FastJsonConfig#setParserConfig(ParserConfig)
     * @deprecated
     */
    @Deprecated
    public Retrofit2ConverterFactory setParserConfig(ParserConfig config) {
        fastJsonConfig.setParserConfig(config);
        return this;
    }

    /**
     * Gets parser feature values.
     *
     * @return the parser feature values
     * @see JSON#DEFAULT_PARSER_FEATURE
     * @deprecated
     */
    @Deprecated
    public int getParserFeatureValues() {
        return JSON.DEFAULT_PARSER_FEATURE;
    }

    /**
     * Sets parser feature values.
     *
     * @param featureValues the feature values
     * @return the parser feature values
     * @see JSON#DEFAULT_PARSER_FEATURE
     * @deprecated
     */
    @Deprecated
    public Retrofit2ConverterFactory setParserFeatureValues(int featureValues) {
        return this;
    }

    /**
     * Get parser features feature [].
     *
     * @return the feature []
     * @see FastJsonConfig#getFeatures()
     * @deprecated
     */
    @Deprecated
    public Feature[] getParserFeatures() {
        return fastJsonConfig.getFeatures();
    }

    /**
     * Sets parser features.
     *
     * @param features the features
     * @return the parser features
     * @see FastJsonConfig#setFeatures(Feature...)
     * @deprecated
     */
    @Deprecated
    public Retrofit2ConverterFactory setParserFeatures(Feature[] features) {
        fastJsonConfig.setFeatures(features);
        return this;
    }

    /**
     * Gets serialize config.
     *
     * @return the serialize config
     * @see FastJsonConfig#getSerializeConfig()
     * @deprecated
     */
    @Deprecated
    public SerializeConfig getSerializeConfig() {
        return fastJsonConfig.getSerializeConfig();
    }

    /**
     * Sets serialize config.
     *
     * @param serializeConfig the serialize config
     * @return the serialize config
     * @see FastJsonConfig#setSerializeConfig(SerializeConfig)
     * @deprecated
     */
    @Deprecated
    public Retrofit2ConverterFactory setSerializeConfig(SerializeConfig serializeConfig) {
        fastJsonConfig.setSerializeConfig(serializeConfig);
        return this;
    }

    /**
     * Get serializer features serializer feature [].
     *
     * @return the serializer feature []
     * @see FastJsonConfig#getSerializerFeatures()
     * @deprecated
     */
    @Deprecated
    public SerializerFeature[] getSerializerFeatures() {
        return fastJsonConfig.getSerializerFeatures();
    }

    /**
     * Sets serializer features.
     *
     * @param features the features
     * @return the serializer features
     * @see FastJsonConfig#setSerializerFeatures(SerializerFeature...)
     * @deprecated
     */
    @Deprecated
    public Retrofit2ConverterFactory setSerializerFeatures(SerializerFeature[] features) {
        fastJsonConfig.setSerializerFeatures(features);
        return this;
    }

    final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private Type type;

        ResponseBodyConverter(Type type) {
            this.type = type;
        }

        public T convert(ResponseBody value) throws IOException {
            try {
                T t = JSON.parseObject(value.bytes()
                        , fastJsonConfig.getCharset()
                        , type
                        , fastJsonConfig.getParserConfig()
                        , fastJsonConfig.getParseProcess()
                        , JSON.DEFAULT_PARSER_FEATURE
                        , fastJsonConfig.getFeatures()
                );
                System.out.print("ApiFastjsonConverterFactory success t = " + t + "\n");
                return t;

            } catch (Exception e) {
                throw new IOException("JSON parse error: " + e.getMessage(), e);
            } finally {
                value.close();
            }
        }
    }

    final class RequestBodyConverter<T> implements Converter<T, RequestBody> {
        RequestBodyConverter() {
        }

        public RequestBody convert(T value) throws IOException {
            try {
                byte[] content = JSON.toJSONBytesWithFastJsonConfig(fastJsonConfig.getCharset()
                        , value
                        , fastJsonConfig.getSerializeConfig()
                        , fastJsonConfig.getSerializeFilters()
                        , fastJsonConfig.getDateFormat()
                        , JSON.DEFAULT_GENERATE_FEATURE
                        , fastJsonConfig.getSerializerFeatures()
                );
                return RequestBody.create(MEDIA_TYPE, content);
            } catch (Exception e) {
                throw new IOException("Could not write JSON: " + e.getMessage(), e);
            }
        }
    }
}

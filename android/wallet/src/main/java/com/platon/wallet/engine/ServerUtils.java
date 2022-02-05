package com.platon.wallet.engine;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import com.platon.wallet.network.ApiFastjsonConverterFactory;
import com.platon.wallet.utils.LogUtils;
import com.platon.wallet.BuildConfig;
import com.platon.wallet.app.Constants;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author ziv
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ServerUtils {

    public final static String HEADER_UPDATE_VERSION = "updateVersion";

    private volatile static BaseApi mBaseApi;

    public static boolean IS_TRANSFER = false;
    private ServerUtils() {
    }



    public static BaseApi getCommonApi() {
        IS_TRANSFER = false;
        System.out.println("ServerUtils.IS_TRANSFER==" + IS_TRANSFER);
        try {
            System.out.println("getCommonApi111111111111111111111111");
            if (mBaseApi == null) {
                synchronized (ServerUtils.class) {
                    if (mBaseApi == null) {
                        System.out.println("getCommonApi222222222222222222222");
                        mBaseApi = createService(BaseApi.class, Constants.URL.URL_HTTP_A);
                    }
                }
            }
        } catch (Exception e) {
            ////LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return mBaseApi;
    }

    ///latoApi
    public static BaseApi getLatoApi() {

        IS_TRANSFER = true;
        System.out.println("ServerUtils.IS_TRANSFER==" + IS_TRANSFER);
        try {
            if (mBaseApi == null) {
                synchronized (ServerUtils.class) {
                    if (mBaseApi == null) {

                        mBaseApi = createService(BaseApi.class, Constants.URL.URL_HTTP_A);
                    }
                }
            }
        } catch (Exception e) {
            ////LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return mBaseApi;
    }

    private static <S> S createService(Class<S> serviceClass, String url) throws Exception {


        //System.out.println("createService11111111111  url ="+url);
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(url)
                        .client(httpClient.build())
                        .addConverterFactory(new ApiFastjsonConverterFactory())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
    private static X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(new BaseUrlInterceptor())
            .addInterceptor(new RequestInterceptor())
            .addInterceptor(getLogInterceptor())
            .addInterceptor(new StethoInterceptor())
            .sslSocketFactory(Objects.requireNonNull(getSSLSocketFactory()),trustManager);

    private static HttpLoggingInterceptor getLogInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return loggingInterceptor;
    }

    /**
     * 不验证证书
     *
     * @return
     * @throws Exception
     */
    private static SSLSocketFactory getSSLSocketFactory() {
        //创建一个不验证证书链的证书信任管理器。

         SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager},
                    new java.security.SecureRandom());
            return sslContext
                    .getSocketFactory();
        } catch (Exception e) {
            //LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return null;
    }

}

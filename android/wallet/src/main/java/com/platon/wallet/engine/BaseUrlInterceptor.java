package com.platon.wallet.engine;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String header = request.header("name");
        System.out.println("BaseUrlInterceptor1114333 request ="+request.url());

        System.out.println("ServerUtils.IS_TRANSFER = "+ServerUtils.IS_TRANSFER);
        if ( ServerUtils.HEADER_UPDATE_VERSION.equals(header) ) {
            System.out.println("BaseUrlInterceptor1111 request2323232323 ="+request);
            return chain.proceed(request);
        } else if(ServerUtils.IS_TRANSFER ){

            String pathSegments = TextUtils.join("/", request.url().pathSegments());
            System.out.println("BaseUrlInterceptor1111 request request.url()11111="+request.url());
            System.out.println("BaseUrlInterceptor1111 request pathSegments="+pathSegments);

            System.out.println("BaseUrlInterceptor1111 request NodeManager.getInstance().getCurNode().getNodeAddress()="+NodeManager.getInstance().getCurNode().getNodeAddress());
            System.out.println("BaseUrlInterceptor1111 request NodeManager.getInstance().getCurNode().getNodeAddress()="+NodeManager.getInstance().getCurNode().getNodeAddress());

            HttpUrl newHttpUrl = HttpUrl.parse("http://78.141.201.149:1080/lato").newBuilder().addPathSegments(pathSegments).build();
            System.out.println("BaseUrlInterceptor1114333 newHttpUrl1122222222222222 ="+newHttpUrl);

            return chain.proceed(request.newBuilder()
                    .url(newHttpUrl)
                    .build());

        }else{
            String pathSegments = TextUtils.join("/", request.url().pathSegments());
            System.out.println("BaseUrlInterceptor1111 request request.url()11111="+request.url());
            System.out.println("BaseUrlInterceptor1111 request pathSegments="+pathSegments);

            System.out.println("BaseUrlInterceptor1111 request NodeManager.getInstance().getCurNode().getNodeAddress()="+NodeManager.getInstance().getCurNode().getNodeAddress());
            System.out.println("BaseUrlInterceptor1111 request NodeManager.getInstance().getCurNode().getNodeAddress()="+NodeManager.getInstance().getCurNode().getNodeAddress());

            HttpUrl newHttpUrl = HttpUrl.parse(NodeManager.getInstance().getCurNode().getNodeAddress()).newBuilder().addPathSegments(pathSegments).build();
            System.out.println("BaseUrlInterceptor1114333 newHttpUrl ="+newHttpUrl);

            return chain.proceed(request.newBuilder()
                    .url(newHttpUrl)
                    .build());
        }
    }
}

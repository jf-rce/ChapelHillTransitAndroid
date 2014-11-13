package com.jforce.chapelhillnextbus;

import android.content.Context;
import android.os.Looper;

import com.loopj.android.http.*;

import org.apache.http.entity.StringEntity;

public class RestClientNextBus {
	
	private static final String BASE_URL = "http://webservices.nextbus.com/service/publicXMLFeed?command=";

    public static AsyncHttpClient syncClient = new SyncHttpClient();
    public static AsyncHttpClient asyncClient = new AsyncHttpClient();

	  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
          getClient().get(getAbsoluteUrl(url), params, responseHandler);
	  }

	  public static void post(Context context, String url, StringEntity entity, String contentType, AsyncHttpResponseHandler responseHandler) {
          getClient().post(context, getAbsoluteUrl(url), entity, contentType, responseHandler);
	  }

	  private static String getAbsoluteUrl(String relativeUrl) {
	      return BASE_URL + relativeUrl;
	  }

    /**
     * @return an async client when calling from the main thread, otherwise a sync client.
     */
    private static AsyncHttpClient getClient()
    {
        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null)
            return syncClient;
        return asyncClient;
    }

}

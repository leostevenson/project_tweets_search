package com.workday.searchframework;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


/**
 * HttpProcess handles Http Get call needed for other classes
 */
class HttpProcess {
    private static String baseUrl;
    private final HashMap<String, String> headers;
    private final HashMap<String, String> parameters;

    /**
     * Constructor with parameters be used to compose HTTP Call
     * @param baseUrl   base url, should not include any parm, eg. "http://www.test.com".
     * @param headers   header with name and value pairs, which will be used to compose header, eg. '{"Content-Type": "Text"...}'
     * @param parameters    parameters with name and value pairs, which will be used to compose parm, eg. '{"p": "reactive"...}'
     */
    public HttpProcess(final String baseUrl, final HashMap<String, String> headers, final HashMap<String, String> parameters) {
        HttpProcess.baseUrl = baseUrl;
        this.headers = headers;
        this.parameters = parameters;
    }

    /**
     * Http Get Call, which composed Uri from baseUrl and parameters, and header, then get response as JSONObject
     * @return  JSONObject contains response.
     * @throws IOException  may be threw when failed to access uri.
     * @throws URISyntaxException   may be threw if uri has syntax issue.
     */
    public JSONObject get() throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(baseUrl);

        for (String parmKey : parameters.keySet()) {
            uriBuilder.addParameter(parmKey, parameters.get(parmKey));
        }

        URI uri = uriBuilder.build();
        HttpGet httpGet = new HttpGet(uri);

        for (String headerKey : headers.keySet()) {
            httpGet.addHeader(headerKey, headers.get(headerKey));
        }

        return getResponseAsJson(httpGet);
    }

    private JSONObject getResponseAsJson(final HttpUriRequest httpUriRequest) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build()).build();

        HttpResponse httpResponse = httpClient.execute(httpUriRequest);
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status != HttpStatus.SC_OK) {
            System.err.println("Http status is not ok.");
            System.exit(1);
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        String responseString = EntityUtils.toString(httpEntity);


        return new JSONObject(responseString);
    }

}

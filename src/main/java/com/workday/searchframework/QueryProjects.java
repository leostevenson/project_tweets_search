package com.workday.searchframework;


import org.json.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Singleton Class which will handle getting projects from Github API
 */
public class QueryProjects {

    private static QueryProjects singleFetch = null;

    private static final String GITHUB_API = "https://api.github.com/search/repositories";

    private QueryProjects() {

    }

    public static QueryProjects getInstance() {
        if (singleFetch == null) {
            singleFetch = new QueryProjects();
        }

        return singleFetch;
    }

    /**
     * Use HTTP Get to retrieve projects from Github API
     * @param queryString   keyword to search
     * @param limit     maximum number of projects to return.
     * @return a JSONObject includes project name and description eg. {"project 1": {"summary": "details"}...}.
     * @throws IOException     io exception
     * @throws URISyntaxException   uri syntax exception
     */
    public JSONObject getProjectsSummary(final String queryString, final int limit) throws IOException, URISyntaxException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("q", queryString);

        HttpProcess httpProcess = new HttpProcess(GITHUB_API, new HashMap<String, String>(), parameters);
        JSONObject responseJson = httpProcess.get();

        return retrieveProjectSummary(responseJson, limit);
    }

    /**
     * (Made public due to unit tests)
     * Retrieve projectSummary from response JSONObject
     * @param responseJson  http response from Github API
     * @param limit     maximum number of projects in project summary.
     * @return  JSONObject contains project summary.
     */
    public static JSONObject retrieveProjectSummary(final JSONObject responseJson, final int limit) {
        JSONArray itemsJson = responseJson.getJSONArray("items");

        JSONObject projectsSummary = new JSONObject();

        for (int i=0 ; i<itemsJson.length() && i<limit; i++) {
            JSONObject item = (JSONObject) itemsJson.get(i);

            String name = item.getString("name");
            String description = item.getString("description");
            JSONObject summary = new JSONObject().put("summary", description);
            projectsSummary.put(name, summary);
        }

        return projectsSummary;
    }

}

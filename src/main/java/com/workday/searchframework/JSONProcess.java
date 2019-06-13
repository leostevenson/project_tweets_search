package com.workday.searchframework;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

class JSONProcess {

    /**
     * Load JSONObject from local file
     * @param path  file path in local repository
     * @return  JSONObject contains items in local file.
     * @throws IOException  May be threw when failed to read through file.
     */
    public static JSONObject loadJsonFromLocalFile(final String path) throws IOException {

        InputStream inputStream = new FileInputStream(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }

        String jsonString = stringBuilder.toString();

        bufferedReader.close();
        inputStream.close();

        return new JSONObject(jsonString);
    }

    /**
     * Merge JSONObjects which hold summary and tweets information, the key will be project name
     * @param projectsSummary a JSONObject includes project name and description, eg. {"project 1": {"summary": "details"}...}.
     * @param tweets a JSONObject includes project name and tweets related to it, eg. {"project 1": {"tweets": {"user1":"tweet1", "user2":"tweet2"...}}}
     * @return JSONObject holds both summary and tweets, eg. {"project 1": {"summary": "details", "tweets": {"user1":"tweet1", "user2":"tweet2"...}}}
     */
    public static JSONObject mergeProjectSummaryAndTweets(JSONObject projectsSummary, final JSONObject tweets) {
        for (String project : projectsSummary.keySet()) {
            JSONObject projectSummary = projectsSummary.getJSONObject(project);
            projectSummary.put("tweets", tweets.getJSONObject(project));

            projectsSummary.put(project, projectSummary);
        }

        return projectsSummary;
    }

    /**
     * Retrieve project name from project summary.
     * @param projectSummary a JSONObject includes project name and description eg. {"project 1": {"summary": "details"}...}.
     * @return string array with only project names.
     */
    public static String[] retrieveProjects(final JSONObject projectSummary) {
        ArrayList<String> projects = new ArrayList<String>();

        projects.addAll(projectSummary.keySet());

        return projects.toArray(new String[0]);
    }
}

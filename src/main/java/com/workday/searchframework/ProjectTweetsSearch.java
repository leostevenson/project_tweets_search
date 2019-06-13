package com.workday.searchframework;

import org.apache.commons.cli.*;
import org.json.JSONObject;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ProjectTweetsSearch {

    private static final Logger logger = Logger.getLogger(String.valueOf(ProjectTweetsSearch.class));

    /**
     * Main function for ProjectTweetsSearch, will read through configuration from files, then query for projects and tweets individually, then merge.
     * @param args
     * -p path: path of config file in local repository.
     *
     * Reading through parms in configuration file.
     * -limit:   # of limit of displaying projects.
     * -tweetsLimit:    # of limit of displaying tweets for each projects.
     * -oauthConsumerKey
     * -oauthSecretKey
     * -oauthAccessToken
     * -oauthAccessSecret
     *
     */
    public static void main(String[] args) {
        Options options = new Options();

        Option path = new Option("p", "path", true, "path to config file");     //Parsing arguments from command line.
        path.setRequired(true);
        options.addOption(path);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine commandLine;
        String configPath = null;
        try {
            commandLine = commandLineParser.parse(options, args);
            configPath = commandLine.getOptionValue("path");
        } catch (ParseException e) {
            helpFormatter.printHelp("Help:", options);
            System.exit(1);
        }

        JSONObject config = loadConfigFromFile(configPath);     //Loading configuration from local file.

        int limit = config.getInt("limit");     //Reading arguments from configuration
        int tweetsLimit = config.getInt("tweets_limit");
        String query = config.getString("query");
        String oauthConsumerKey = config.getString("oauth_consumer_key");
        String oauthSecretKey = config.getString("oauth_secret_key");
        String oauthAccessToken = config.getString("oauth_access_token");
        String oauthAccessSecret = config.getString("oauth_access_secret");

        if (limit > 10 || limit < 1) {
            System.err.println("Invalid project limit, should be [1, 10].");
            System.exit(0);
        }

        if (tweetsLimit >100 || tweetsLimit < 1) {
            System.err.println("Invalid Tweets limit, should be [1, 100].");
            System.exit(0);
        }

        if (query.equals("")) {
            System.err.println("query must not be empty.");
            System.exit(0);
        }

        if (oauthConsumerKey.equals("") || oauthSecretKey.equals("") || oauthAccessToken.equals("") || oauthAccessSecret.equals("")) {
            System.err.println("Missing one or more authentication: oauth_consumer_key/oauth_secret_key/oauth_access_token/oauth_access_secret.");
            System.exit(0);
        }

        JSONObject projectsSummary = queryProjectSummary(query, limit);     //Query Projects from Github.
        String[] projects = JSONProcess.retrieveProjects(projectsSummary);

        String projectTweetsString = getProjectsTweetsString(projects, tweetsLimit, oauthConsumerKey, oauthSecretKey, oauthAccessToken, oauthAccessSecret);   // Query Tweets from Twitter

        JSONObject projectSummaryAndTweets = JSONProcess.mergeProjectSummaryAndTweets(projectsSummary, new JSONObject(projectTweetsString));

        System.out.println(projectSummaryAndTweets.toString(4));
    }

    /**
     * Read through config file, then return configuration included.
     * @param path: path of config file in local repository.
     *
     * @return configuration as JSONObject
     *
     */
    private static JSONObject loadConfigFromFile(final String path) {
        if (path == null) {
            logger.log(Level.SEVERE, "config file path can not be empty.");
            System.exit(1);
        }

        JSONObject config = null;
        try {
            config = JSONProcess.loadJsonFromLocalFile(path);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);
        }

        if (config == null) {
            logger.log(Level.SEVERE, "config file is empty.");
        }

        return config;
    }

    /**
     * Query for Project Summary from Github
     * @param query keyword used to query.
     * @param limit maximum number of returned results.
     * @return JSONObject includes project name and description eg. {"project 1": {"summary": "details"}...}.
     */
    private static JSONObject queryProjectSummary(final String query, final int limit) {
        JSONObject projectsSummary = null;
        try {
            projectsSummary = QueryProjects.getInstance().getProjectsSummary(query, limit);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);
        }

        if (projectsSummary == null) {
            logger.log(Level.WARNING, "Failed to read Project Summary.");
        }

        return projectsSummary;
    }



    /**
     * Retrieve tweets related to given projects
     * @param projects      given projects, as keywords.
     * @param tweetsLimit       maximum number of tweets retrieved for each project
     * @param oauthConsumerKey  oauth consumer key
     * @param oauthSecretKey    oauth secret key
     * @param oauthAccessToken  oauth access token
     * @param oauthAccessSecret oauth access secret
     * @return  JSONObject as String, which is response from API.
     */
    private static String getProjectsTweetsString(String[] projects, int tweetsLimit, String oauthConsumerKey, String oauthSecretKey, String oauthAccessToken, String oauthAccessSecret) {
        String projectTweetsString = null;      //Query Tweets from Twitter API.
        try {
            projectTweetsString = QueryTweets.getInstance(oauthConsumerKey, oauthSecretKey, oauthAccessToken, oauthAccessSecret).query(projects, tweetsLimit);
        } catch (TwitterException e) {
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);
        }
        if (projectTweetsString == null) {
            logger.log(Level.WARNING, "Failed to get tweets.");
        }

        return projectTweetsString;
    }


}

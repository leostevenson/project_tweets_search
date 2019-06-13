package com.workday.searchframework;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

/**
 * Get Tweets for given projects
 */
public class QueryTweets {

    private static QueryTweets singleQuery = null;

    private static String oauthConsumerKey;
    private static String oauthSecretKey;
    private static String oauthAccessToken;
    private static String oauthAccessSecret;

    private QueryTweets(String oauth_consumer_key, String oauth_secret_key, String oauth_access_token, String oauth_access_secret) {
        oauthConsumerKey = oauth_consumer_key;
        oauthSecretKey = oauth_secret_key;
        oauthAccessToken = oauth_access_token;
        oauthAccessSecret = oauth_access_secret;
    }

    public static QueryTweets getInstance(final String oauthConsumerKey, final String oauthSecretKey, final String oauthAccessToken, final String oauthAccessSecret) {
        if (singleQuery == null) {
            singleQuery = new QueryTweets(oauthConsumerKey, oauthSecretKey, oauthAccessToken, oauthAccessSecret);
        }

        return singleQuery;
    }

    /**
     * Query tweets related to given projects
     * @param projects   list of projects needed to be queried.
     * @param tweetsLimit      maximum number of tweets will be returned for each project.
     * @return  String converted from JSONObject, which contains summary and tweets for each project, eg. {"project 1": {"summary": "details", "tweets": {"user1":"tweet1", "user2":"tweet2"...}}}
     */
    public String query(final String[] projects, final int tweetsLimit) throws TwitterException {
        Twitter twitter = prepareTwitterAPI();
        JSONObject projectTweets = new JSONObject();

        for (String project : projects) {
            JSONObject tweets = new JSONObject();
            Query query = new Query(project);
            query.setCount(tweetsLimit);
            query.setResultType(Query.RECENT);

            List<Status> statuses = twitter.search(query).getTweets();
            for (Status status : statuses) {
                tweets.put(status.getUser().getName(), status.getText());
            }

            projectTweets.put(project, tweets);
        }

        return projectTweets.toString();
    }

    private Twitter prepareTwitterAPI() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(oauthConsumerKey);
        configurationBuilder.setOAuthConsumerSecret(oauthSecretKey);
        configurationBuilder.setOAuthAccessToken(oauthAccessToken);
        configurationBuilder.setOAuthAccessTokenSecret(oauthAccessSecret);

        return new TwitterFactory(configurationBuilder.build()).getInstance();
    }

}

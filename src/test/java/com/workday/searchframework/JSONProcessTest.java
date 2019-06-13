package com.workday.searchframework;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JSONProcessTest {
    private JSONObject projectsSummary;
    private JSONObject tweets;

    @BeforeEach
    void setup() {
        this.projectsSummary = new JSONObject();
        JSONObject summary1 = new JSONObject();
        summary1.put("summary", "summary1");
        JSONObject summary2 = new JSONObject();
        summary2.put("summary", "summary2");
        this.projectsSummary.put("project1", summary1).put("project2", summary2);

        this.tweets = new JSONObject();
        JSONObject tweet1 = new JSONObject();
        tweet1.put("user_1_1", "tweet_1_1");
        tweet1.put("user_1_2", "tweet_1_2");
        JSONObject tweet2 = new JSONObject();
        tweet2.put("user_2_1", "tweet_2_1");
        tweet2.put("user_2_2", "tweet_2_2");
        this.tweets.put("project1", tweet1).put("project2", tweet2);
    }

    @Test
    void loadJsonFromLocalFile() throws IOException {
        String path = "src/main/resources/test.property";
        JSONObject result = JSONProcess.loadJsonFromLocalFile(path);

        assertEquals("value_1", result.getString("key_1"));
        assertEquals(10, result.getInt("key_2"));
        assertEquals("", result.getString("key_3"));
    }

    @Test
    void mergeProjectSummaryAndTweets() {
        JSONObject results = JSONProcess.mergeProjectSummaryAndTweets(this.projectsSummary, this.tweets);
        JSONObject result1 = results.getJSONObject("project1");
        JSONObject result2 = results.getJSONObject("project2");

        assertEquals("summary1", result1.getString("summary"));
        assertEquals("summary2", result2.getString("summary"));
        assertEquals("tweet_1_1", result1.getJSONObject("tweets").getString("user_1_1"));
        assertEquals("tweet_1_2", result1.getJSONObject("tweets").getString("user_1_2"));
        assertEquals("tweet_2_1", result2.getJSONObject("tweets").getString("user_2_1"));
        assertEquals("tweet_2_2", result2.getJSONObject("tweets").getString("user_2_2"));
    }

    @Test
    void retrieveProjects() {
        String[] result = JSONProcess.retrieveProjects(this.projectsSummary);

        assertEquals(2, result.length);
        assertEquals("project2", result[0]);
        assertEquals("project1", result[1]);
    }
}
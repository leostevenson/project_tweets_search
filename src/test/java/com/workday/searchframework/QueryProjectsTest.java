package com.workday.searchframework;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryProjectsTest {
    private JSONObject response;

    @BeforeEach
    void setUp() {
        this.response = new JSONObject();
        this.response.put("total_count", 10);
        this.response.put("incomplete_results", false);
        JSONObject item1 = new JSONObject();
        item1.put("id", 1);
        item1.put("name", "project1");
        item1.put("description", "description1");
        JSONObject item2 = new JSONObject();
        item2.put("id", 2);
        item2.put("name", "project2");
        item2.put("description", "description2");
        JSONObject item3 = new JSONObject();
        item3.put("id", 3);
        item3.put("name", "project3");
        item3.put("description", "description3");
        JSONArray items = new JSONArray();
        items.put(item1).put(item2).put(item3);
        this.response.put("items", items);

    }

    @Test
    void retrieveProjectSummary() {
        JSONObject result1 = QueryProjects.retrieveProjectSummary(this.response, 3);

        assertEquals(3, result1.length());
        assertEquals("description1", result1.getJSONObject("project1").getString("summary"));
        assertEquals("description2", result1.getJSONObject("project2").getString("summary"));
        assertEquals("description3", result1.getJSONObject("project3").getString("summary"));

        JSONObject result2 = QueryProjects.retrieveProjectSummary(this.response, 2);
        assertEquals(2, result2.length());
    }

}
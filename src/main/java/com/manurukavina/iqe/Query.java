package com.manurukavina.iqe;

import org.json.JSONArray;
import org.json.JSONObject;

public class Query {
    public static String processQuery(String request) {
        JSONObject json = new JSONObject();
        try {
            JSONObject input = new JSONObject(request);
            String query = input.getString("query");
            JSONArray params = input.optJSONArray("params");

            if (query.toLowerCase().startsWith("select")) {
                JSONObject row = new JSONObject();
                row.put("id", params != null ? params.getInt(0) : 1);
                row.put("name", "John Doe");

                json.put("status", "success");
                json.put("data", new JSONArray().put(row));
                json.put("rows", 1);
                json.put("query_time", 0.05);
            } else {
                json.put("status", "error");
                json.put("code", 1001);
                json.put("message", "Unsupported query: " + query);
            }
        } catch (Exception e) {
            json.put("status", "error");
            json.put("code", 1000);
            json.put("message", "Invalid request: " + e.getMessage());
        }
        return json.toString();
    }
}

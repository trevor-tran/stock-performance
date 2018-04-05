package com.phuongdtran.util;

import spark.*;

public class RequestUtil {

    public static String getQueryUsername(Request request) {
        return request.queryParams("username");
    }

    public static String getQueryPassword(Request request) {
        return request.queryParams("password");
    }
    
    public static String getQueryFirstName(Request request) {
    	return request.session().attribute("firstName");
    }

    public static String getSessionUserId(Request request) {
        if(request.session().attribute("currentUserId") != null){
        	int userId = request.session().attribute("currentUserId");
        	return String.valueOf(userId);
        }
        return null;
    }
    
    public static String getQueryGoogleToken(Request request){
    	return request.queryParams("idtoken");
    }

    public static boolean clientAcceptsHtml(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("text/html");
    }

    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

}
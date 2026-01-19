package com.example.S2S.common.Util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

/**
 * Utility class for handling HTTP cookies.
 */
public class CookieUtils {

    /**
     * Retrieves the value of a cookie from the request by its name.
     *
     * @param request The {@link HttpServletRequest} object containing the cookies.
     * @param name    The name of the cookie to retrieve.
     * @return The value of the cookie if found, otherwise {@code null}.
     */
    public static String getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            String tokenCookie =  Arrays.stream(request.getCookies())
                    .filter(cookie -> name.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
            return EncrDecrUtils.decrypt(tokenCookie);
        }

        return null;
    }

    /**
     * Sets a cookie in the HTTP response.
     *
     * @param response The {@link HttpServletResponse} object to add the cookie to.
     * @param name     The name of the cookie.
     * @param value    The value of the cookie.
     * @param maxAge   The maximum age of the cookie in seconds. A value of 0 deletes the cookie.
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String encryptVal = EncrDecrUtils.encrypt(value);
        Cookie cookie = new Cookie(name, encryptVal);
        cookie.setPath("/"); // Apply cookie for the entire domain
        cookie.setHttpOnly(true); // Make the cookie inaccessible to JavaScript for security
        cookie.setMaxAge(maxAge); // Set cookie lifetime in seconds
        response.addCookie(cookie);
    }

    /**
     * Deletes the authentication cookie by setting its expiration time to 0.
     * @param response HttpServletResponse
     * @param name The name of the cookie to be deleted
     */
    public static void clearAuthCookie( HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

package com.nailsaas.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentUserCode() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }
}
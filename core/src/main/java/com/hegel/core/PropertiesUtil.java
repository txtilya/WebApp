package com.hegel.core;

import java.util.Properties;

public interface PropertiesUtil {

    static String getValueAndRemoveKey(Properties properties, String key) {
        return (String) properties.remove(key);
    }
}

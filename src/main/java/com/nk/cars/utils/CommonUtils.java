package com.nk.cars.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


/**
 * CommonUtils.java
 *
 * @author Nandhakumar N
 * @module com.nk.cars.utils
 * @created Nov 24, 2024
 */
public class CommonUtils
{
    private static final Logger logger = LogManager.getLogger();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> boolean nullOrEmpty(Collection<T> collection)
    {
        return collection==null || collection.isEmpty();
    }

    public static boolean nullOrEmpty(String s)
    {
        return s == null || s.isBlank();
    }

    public static <T> boolean hasElement(Collection<T> collection)
    {
        return collection!=null && !collection.isEmpty();
    }

    public static String convertToJson(Object object)
    {
        try {

            if(object instanceof String) {
                return (String) object;
            }
            return OBJECT_MAPPER.writeValueAsString(object);
        }
        catch (Exception e)
        {
            logger.error("Error while converting json ",e);
            return null;
        }
    }

}

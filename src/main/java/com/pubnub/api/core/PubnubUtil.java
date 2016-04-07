package com.pubnub.api.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Frederick on 3/30/16.
 */
public class PubnubUtil {


    public static String joinString(List<String> val, String delim){
        StringBuilder builder = new StringBuilder();
        for(String l: val){
            builder.append(l);
            builder.append(",");
        }

        return builder.toString().substring(0,builder.toString().length() - 1);

    }

    /**
     * Returns encoded String
     *
     * @param sUrl
     *            , input string
     * @return , encoded string
     */
    public static String pamEncode(String sUrl) {
        /* !'()*~ */

        String encoded = urlEncode(sUrl);
        if (encoded != null) {
            encoded = encoded.
                    replace("*", "%2A")
                    .replace("!", "%21")
                    .replace("'", "%27")
                    .replace("(", "%28")
                    .replace(")", "%29")
                    .replace("[", "%5B")
                    .replace("]", "%5D")
                    .replace("~", "%7E");
        }
        return encoded;
    }


    /**
     * Returns encoded String
     *
     * @param sUrl
     *            , input string
     * @return , encoded string
     */
    public static String urlEncode(String sUrl) {
        try {
            return URLEncoder.encode(sUrl, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String preparePamArguments(Map<String, String> pamArgs){
        Set<String> pamKeys = new TreeSet(pamArgs.keySet());
        String stringifiedArguments = "";
        int i = 0;

        for (String pamKey : pamKeys) {
            if (i != 0){
                stringifiedArguments += "&";
            }


            stringifiedArguments += pamEncode(pamKey);
            stringifiedArguments += "=";
            stringifiedArguments += pamEncode(pamArgs.get(pamKey));

            i += 1;
        }

        return stringifiedArguments;
    }

}

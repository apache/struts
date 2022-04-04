/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util.logging;

import java.util.LinkedList;
import java.util.List;

/**
 * Logging utility methods
 *
 * @deprecated since 2.5
 */
@Deprecated
public class LoggerUtils {

    /**
     * Formats messages using parameters. For example, the call:
     * 
     * <pre>
     * format("foo #0 #1", "bob", "joe");
     * </pre>
     * 
     * will return:
     * <pre>
     * foo bob joe
     * </pre>
     * 
     * @param msg The message
     * @param args A list of arguments.  A maximum of 10 are supported.
     * @return The formatted string
     */
    public static String format(String msg, String... args) {
        if (msg != null && msg.length() > 0 && msg.indexOf('#') > -1) {
            StringBuilder sb = new StringBuilder();
            boolean isArg = false;
            for (int x = 0; x < msg.length(); x++) {
                char c = msg.charAt(x);
                if (isArg) {
                    isArg = false;
                    if (Character.isDigit(c)) {
                        int val = Character.getNumericValue(c);
                        if (val >= 0 && val < args.length) {
                            sb.append(args[val]);
                            continue;
                        }
                    }
                    sb.append('#');
                }
                if (c == '#') {
                    isArg = true;
                    continue;
                }
                sb.append(c);
            }
            
            if (isArg) {
                sb.append('#');
            }
            return sb.toString();
        }
        return msg;
        
    }

    public static String format(String msg, Object[] args) {
        List<String> strArgs = new LinkedList<String>();
        for (Object arg : args) {
            strArgs.add(arg != null ? arg.toString() : "(null)");
        }
        return format(msg, strArgs.toArray(new String[strArgs.size()]));
    }

}

package com.mlx.accounts.support;

import org.json.JSONObject;

/**
 * 3/4/15.
 */
public class JSONUtils {
    public static String safeString(JSONObject object, String attr) {
        try {
            return object.getString(attr);
        } catch (Exception e) {
            return "";
        }
    }

    public static String safePathValue(JSONObject object, String... path) {
        try {
            JSONObject obj = object;
            if (path != null && path.length > 0) {
                for (int i = 0; i < path.length; i++) {
                    if (obj != null) {
                        if (i != path.length - 1) {
                            obj = obj.getJSONObject(path[i]);
                        } else {
                            return safeString(obj, path[i]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            //
        }
        return "";
    }

}

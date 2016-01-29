package com.mlizhi.modules.spec.util;

import com.mlizhi.base.Utils;
import com.mlizhi.utils.Constants;
import com.tencent.open.SocialConstants;
import org.json.JSONException;
import org.json.JSONObject;
import p016u.aly.bq;

public class JsonUtil {
    private static JSONObject getHeaderJsonObject(String jsonObjectString) {
        String code = bq.f888b;
        try {
            return new JSONObject(jsonObjectString).getJSONObject(Constants.RESULT_JSON_HEADER);
        } catch (JSONException e) {
            Utils.m13E("parse jsonObject error!!!", e);
            return null;
        }
    }

    public static JSONObject getBodyJsonObject(String jsonObjectString) {
        try {
            return new JSONObject(jsonObjectString).getJSONObject(Constants.RESULT_JSON_BODY);
        } catch (JSONException e) {
            Utils.m13E("parse jsonObject error!!!", e);
            return null;
        }
    }

    public static String getHeaderCode(String jsonObjectString) {
        String code = bq.f888b;
        try {
            return getHeaderJsonObject(jsonObjectString).getString(Constants.RESULT_JSON_HEADER_CODE);
        } catch (Exception e) {
            Utils.m13E("parse jsonObject error!!!", e);
            return bq.f888b;
        }
    }

    public static String getHeaderErrorInfo(String jsonObjectString) {
        String description = bq.f888b;
        try {
            return getHeaderJsonObject(jsonObjectString).getString(SocialConstants.PARAM_COMMENT);
        } catch (Exception e) {
            Utils.m13E("parse jsonObject error!!!", e);
            return "\u670d\u52a1\u5668\u53d1\u751f\u672a\u77e5\u9519\u8bef\uff01\uff01\uff01";
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.util.HashMap<String, String>> getListContentMap(JSONObject r10) {
        /*
        r7 = 0;
        r3 = new java.util.ArrayList;
        r3.<init>();
        r1 = 0;
        if (r10 != 0) goto L_0x000b;
    L_0x0009:
        r3 = r7;
    L_0x000a:
        return r3;
    L_0x000b:
        r8 = "info";
        r0 = r10.getJSONArray(r8);	 Catch:{ Exception -> 0x007d }
        if (r0 == 0) goto L_0x0083;
    L_0x0013:
        r4 = 0;
        r6 = 0;
        r2 = r1;
    L_0x0016:
        r8 = r0.length();	 Catch:{ Exception -> 0x0085 }
        if (r6 < r8) goto L_0x001e;
    L_0x001c:
        r1 = r2;
        goto L_0x000a;
    L_0x001e:
        r4 = r0.get(r6);	 Catch:{ Exception -> 0x0085 }
        r4 = (org.json.JSONObject) r4;	 Catch:{ Exception -> 0x0085 }
        r1 = new java.util.HashMap;	 Catch:{ Exception -> 0x0085 }
        r1.<init>();	 Catch:{ Exception -> 0x0085 }
        r8 = "content_item_id";
        r9 = "id";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r8 = "content_item_image";
        r9 = "imageUrl";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r8 = "content_item_praise_num";
        r9 = "likeCount";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r8 = "content_item_title";
        r9 = "title";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r8 = "content_item_view_num";
        r9 = "lookCount";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r8 = "content_item_type";
        r9 = "infoType";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r8 = "content_item_module_type";
        r9 = "moduleType";
        r9 = r4.getString(r9);	 Catch:{ Exception -> 0x007d }
        r1.put(r8, r9);	 Catch:{ Exception -> 0x007d }
        r3.add(r1);	 Catch:{ Exception -> 0x007d }
        r6 = r6 + 1;
        r2 = r1;
        goto L_0x0016;
    L_0x007d:
        r5 = move-exception;
    L_0x007e:
        r8 = "parse jsonObject error!!!";
        com.mlizhi.base.Utils.m13E(r8, r5);
    L_0x0083:
        r3 = r7;
        goto L_0x000a;
    L_0x0085:
        r5 = move-exception;
        r1 = r2;
        goto L_0x007e;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mlizhi.modules.spec.util.JsonUtil.getListContentMap(org.json.JSONObject):java.util.List<java.util.HashMap<java.lang.String, java.lang.String>>");
    }
}

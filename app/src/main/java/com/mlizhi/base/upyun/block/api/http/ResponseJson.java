package com.mlizhi.base.upyun.block.api.http;

import com.mlizhi.base.upyun.block.api.common.Params;
import com.mlizhi.utils.Constants;
import com.umeng.socialize.bean.StatusCode;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import p016u.aly.bq;

public class ResponseJson {
    public static String errorResponseJsonFormat(int statusCode, Header[] headers, byte[] responseBody) {
        String result = bq.f888b;
        JSONObject obj;
        if (responseBody != null) {
            try {
                obj = new JSONObject(new String(responseBody));
                obj.put(Constants.RESULT_JSON_HEADER_CODE, statusCode);
                for (Header header : headers) {
                    if (Params.X_Request_Id.equals(header.getName())) {
                        obj.put(Params.X_Request_Id, header.getValue());
                    }
                }
                return obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return result;
            }
        }
        obj = new JSONObject();
        obj.put(Constants.RESULT_JSON_HEADER_CODE, 408);
        obj.put(Params.ERROR_CODE, 40800);
        obj.put(Params.X_Request_Id, (Object) "NONE");
        obj.put(Constants.WEB_SOCKET_CUSTOMER_MESSAGE, (Object) "There is nothing responsed, mybe timeout happend.");
        return obj.toString();
    }

    public static String okResposneJsonFormat(int statusCode, Header[] headers, byte[] responseBody) {
        String result = bq.f888b;
        if (responseBody != null) {
            try {
                JSONObject obj = new JSONObject(new String(responseBody));
                obj.put(Constants.RESULT_JSON_HEADER_CODE, statusCode);
                return obj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return result;
            }
        }
        obj = new JSONObject();
        obj.put(Constants.RESULT_JSON_HEADER_CODE, (int) StatusCode.ST_CODE_SUCCESSED);
        return obj.toString();
    }

    public static String exceptionJsonFormat(int error_code, String errorMsg) {
        String result = bq.f888b;
        try {
            JSONObject obj = new JSONObject();
            obj.put(Constants.RESULT_JSON_HEADER_CODE, 500);
            obj.put(Params.ERROR_CODE, error_code);
            obj.put(Constants.WEB_SOCKET_CUSTOMER_MESSAGE, (Object) errorMsg);
            obj.put(Params.X_Request_Id, (Object) "NONE");
            result = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}

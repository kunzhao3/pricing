package com.parses.server.csc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.parses.server.csc.request.QueryMerchantMemberReq;
import com.parses.server.csc.response.QueryMerchantMemberResp;
import com.parses.server.bean.dto.DataTransDTO;
import com.parses.server.util.HttpCallEnum;
import com.parses.server.util.HttpPostUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CscHttpServer {
    @Autowired
    private HttpPostUtil httpPostUtil;
    @Value("${URL}")
    private String URL;

    public QueryMerchantMemberResp queryMerchantMember(QueryMerchantMemberReq request){
        QueryMerchantMemberResp response = null;
        try {
            response = doProcess("/queryMerchantMember", request, QueryMerchantMemberResp.class);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return response;
    }
    /**
     * 业务处理
     */
    private  <T> T doProcess(String path, Object reqObject, Class<T> resultType) {
        String serviceUrl = URL + path;
        String jsonObj = this.serialize(reqObject);
        DataTransDTO dataTransDTO =  httpPostUtil.callPayOfHttp(jsonObj, serviceUrl);
        if (!HttpCallEnum.SEND_SUCCESS.getCode().equals(dataTransDTO.getRespCode())) {
            throw new RuntimeException(dataTransDTO.getResultJson());
        }
        return this.deserialize(dataTransDTO.getResultJson(), resultType);
    }

    /**
     * 序列化
     */
    private String serialize(Object object) {
        if (object == null || object instanceof String) {
            return (String) object;
        }
        JSONObject jsonObj = (JSONObject) JSON.toJSON(object);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObj);
        return JSON.toJSONString(jsonArray, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 反序列化
     */
    private <T> T deserialize(String value, Class<T> resultType) {
        if (resultType == null || resultType == String.class || resultType == Object.class) {
            return (T) value;
        }
        return JSON.parseObject(value, resultType);
    }
}

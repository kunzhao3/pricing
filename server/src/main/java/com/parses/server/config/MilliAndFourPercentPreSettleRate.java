package com.parses.server.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.parses.server.constant.MilliAndFourPercent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MilliAndFourPercentPreSettleRate {
    private List<PreSettleRateBean> milliPreSettleRate;
    private List<PreSettleRateBean> fourPercentPreSettleRate;

    public static MilliAndFourPercentPreSettleRate parseConfig(String jsonStr) {
        List<Map<String, List<PreSettleRateBean>>> rawList =
                JSON.parseObject(jsonStr, new TypeReference<List<Map<String, List<PreSettleRateBean>>>>(){});

        MilliAndFourPercentPreSettleRate config = new MilliAndFourPercentPreSettleRate();
        config.milliPreSettleRate = new ArrayList<>();
        config.fourPercentPreSettleRate = new ArrayList<>();
        rawList.forEach(map -> {
            if(map.containsKey(MilliAndFourPercent.MILLI.getFeeParam())) {
                config.milliPreSettleRate.addAll(map.get(MilliAndFourPercent.MILLI.getFeeParam()));
            }
            if(map.containsKey(MilliAndFourPercent.FOUR_PERCENT.getFeeParam())) {
                config.fourPercentPreSettleRate.addAll(map.get(MilliAndFourPercent.FOUR_PERCENT.getFeeParam()));
            }
        });
        return config;
    }

    public static void main(String[] args) {
        //String json="[{\"milliPreSettleRate\":[{\"name\":\"5551-A\",\"value\":\"SERVICE_FEE\"}]},{\"fourPercentPreSettleRate\":[{\"name\":\"5551-B,C,D\",\"value\":\"SERVICE_FEE_TWO\"}]}]";
        String json="[{\"fourPercentPreSettleRate\":[{\"name\":\"5551-A\",\"value\":\"SERVICE_FEE\"},{\"name\":\"5551-B,C,D\",\"value\":\"SERVICE_FEE_TWO\"}]}]";
        MilliAndFourPercentPreSettleRate config = parseConfig(json);
        for (PreSettleRateBean preSettleRateBean : config.getMilliPreSettleRate()) {
            System.out.println(preSettleRateBean.getName());
            System.out.println(preSettleRateBean.getValue());
        }
        for (PreSettleRateBean preSettleRateBean : config.getFourPercentPreSettleRate()) {
            System.out.println(preSettleRateBean.getName());
            System.out.println(preSettleRateBean.getValue());
        }
    }
}

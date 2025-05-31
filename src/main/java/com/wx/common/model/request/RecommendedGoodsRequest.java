package com.wx.common.model.request;

import lombok.Data;

/**
 * {
 *     "token": "06f0b352-0230-4636-8a8d-25f0e5e75f17",
 *     "fromMall": "mallapp",
 *     "recommendedGoodsIds": [
 *         {  "commend": [ 15, 16 ] },
 *         { "commend": [ 17,  19  ] }
 *     ]
 * }
 */
@Data
public class RecommendedGoodsRequest {
    private String token;
    private Long id;
    private String fromMall;
    private RecommendedGoods[] recommendedGoodsIds;

    @Data
    public static class RecommendedGoods {
        private String name;
        private Long[] commend;
    }
}

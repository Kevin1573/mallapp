package com.wx.common.model.request;

import lombok.Data;

/**
 * {
 *     "token": "06f0b352-0230-4636-8a8d-25f0e5e75f17",
 *     "id": 1,
 *     "bestSellingGoodsIds": [1, 2, 3] // 热销商品列表
 * }
 */
@Data
public class BestSellingGoodsRequest {
    private String token;
    private Long id;
    private Long[] bestSellingGoodsIds;
}

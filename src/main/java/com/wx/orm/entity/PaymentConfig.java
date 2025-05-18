package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@TableName("payment_config")
@Accessors(chain = true)
public class PaymentConfig {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("app_id")
    private String appId;

    @TableField("app_private_key")
    private String appPrivateKey;

    @TableField("alipay_public_key")
    private String alipayPublicKey;

    @TableField("gateway_url")
    private String gatewayUrl;

    @TableField("pay_channel")
    private String payChannel;

    @TableField("mch_id")
    private String mchId;

    @TableField("wx_private_key_path")
    private String wxPrivateKeyPath;

    @TableField("wx_mch_serial_no")
    private String wxMchSerialNo;

    @TableField("channel_public_key")
    private String channelPublicKey;

    @TableField("api_v3_key")
    private String apiV3Key;

    @TableField("notify_url")
    private String notifyUrl;

    private Integer status;

    // 状态枚举（可选）
    @Getter
    public enum Status {
        DISABLED(0), ENABLED(1);
        private final int code;

        Status(int code) {
            this.code = code;
        }
    }
}

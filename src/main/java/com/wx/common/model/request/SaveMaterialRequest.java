package com.wx.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SaveMaterialRequest {

    private Long id;

    /**
     * 详情
     */
    private String content;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String cover;

    private String type;

    private String fileType;

}

package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@TableName("material_library")
@Data
@Accessors(chain = true)
public class MaterialLibraryDO {
    @TableId(value = "id", type = IdType.AUTO)
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

    private Date createTime;

    private Date modifyTime;

    /**
     * 文件分类，1图片，2视频，3录音
     */
    private String fileType;
}

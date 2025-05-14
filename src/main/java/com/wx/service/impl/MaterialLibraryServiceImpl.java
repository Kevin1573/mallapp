package com.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wx.common.model.request.SaveMaterialRequest;
import com.wx.common.model.response.QueryMaterialResponse;
import com.wx.orm.entity.MaterialLibraryDO;
import com.wx.orm.mapper.MaterialLibraryMapper;
import com.wx.service.MaterialLibraryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MaterialLibraryServiceImpl implements MaterialLibraryService {

    @Autowired
    private MaterialLibraryMapper materialLibraryMapper;

    @Override
    public void saveOrUpdateMaterial(SaveMaterialRequest request) {
        MaterialLibraryDO materialLibraryDO = materialLibraryMapper.selectById(request.getId());
        if (Objects.isNull(materialLibraryDO)) {
            MaterialLibraryDO newMaterialLibraryDO = new MaterialLibraryDO();
            newMaterialLibraryDO.setTitle(request.getTitle())
                    .setCover(request.getCover())
                    .setContent(request.getContent())
                    .setType(request.getType())
                    .setCreateTime(new Date())
                    .setModifyTime(new Date())
                    .setFileType(request.getFileType());
            materialLibraryMapper.insert(newMaterialLibraryDO);
        } else {
            materialLibraryDO.setModifyTime(new Date())
                    .setTitle(request.getTitle())
                    .setCover(request.getCover())
                    .setContent(request.getContent())
                    .setFileType(request.getFileType());
            materialLibraryMapper.updateById(materialLibraryDO);
        }
    }

    @Override
    public List<QueryMaterialResponse> queryMaterialByType(SaveMaterialRequest request) {
        LambdaQueryWrapper<MaterialLibraryDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MaterialLibraryDO::getType, request.getType());
        queryWrapper.orderByAsc(MaterialLibraryDO::getCreateTime);
        List<MaterialLibraryDO> materialLibraryDOS = materialLibraryMapper.selectList(queryWrapper);
        List<QueryMaterialResponse> responseList = new ArrayList<>();
        for (MaterialLibraryDO materialLibraryDO : materialLibraryDOS) {
            QueryMaterialResponse response = new QueryMaterialResponse();
            BeanUtils.copyProperties(materialLibraryDO, response);
            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public void deleteMaterialById(SaveMaterialRequest request) {
        materialLibraryMapper.deleteById(request.getId());
    }
}

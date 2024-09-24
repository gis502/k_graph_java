package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.entity.ExcelFile;
import com.ruoyi.system.mapper.ExcelFileMapper;
import com.ruoyi.system.service.ExcelFileService;
import com.ruoyi.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ExcelFileServiceImpl extends ServiceImpl<ExcelFileMapper, ExcelFile> implements ExcelFileService{

    private final ISysUserService userService;

    @Override
    public List<ExcelFile> selectFilesAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<ExcelFile> selectFilesByUserId() {
        Long userId = SecurityUtils.getUserId();
        //如果用户id为空或者不存在
        if (userId == null) {
            throw new ServiceException("用户id为空或者不存在");
        }
        //查询当前用户的文件
        SysUser sysUser = userService.selectUserById(userId);
        List<Integer> userFileIds = JSON.parseArray(sysUser.getFileIds(), Integer.class);
        if (userFileIds == null || userFileIds.size() == 0) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(Wrappers.<ExcelFile>lambdaQuery().in(ExcelFile::getId, userFileIds));
    }


}

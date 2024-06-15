package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.mapper.TagMapper;
import com.example.usercenter.model.domain.Tag;
import com.example.usercenter.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 25006
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2024-06-15 21:08:37
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}





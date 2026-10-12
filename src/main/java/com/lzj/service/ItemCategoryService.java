package com.lzj.service;

import com.lzj.dto.ItemCategoryInsertDTO;
import com.lzj.dto.ItemCategoryPageDTO;
import com.lzj.dto.ItemCategoryUpdateDTO;
import com.lzj.entity.ItemCategory;
import com.lzj.vo.ItemCategoryListVO;
import com.lzj.vo.ItemCategoryPageVO;
import com.lzj.vo.PageVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 物品分类表 服务层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public interface ItemCategoryService extends IService<ItemCategory> {
    void insert(Long userId, ItemCategoryInsertDTO dto);
    ItemCategory select(Long id);
    List<ItemCategoryListVO> list(Long userId);
    PageVO<ItemCategoryPageVO> page(Long userId, ItemCategoryPageDTO dto);
    long count(Long userId);
    void update(Long userId, ItemCategoryUpdateDTO dto);
    void delete(Long id);
}

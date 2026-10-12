package com.lzj.service;

import com.lzj.dto.ItemInsertDTO;
import com.lzj.dto.ItemPageDTO;
import com.lzj.dto.ItemUpdateDTO;
import com.lzj.entity.Item;
import com.lzj.vo.ItemPageVO;
import com.lzj.vo.ItemStatisticsVO;
import com.lzj.vo.ItemVO;
import com.lzj.vo.PageVO;
import com.mybatisflex.core.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物品表 服务层。
 *
 * @author 李兆基
 * @since v1.0.0
 */
public interface ItemService extends IService<Item> {
    void insert(Long userId, ItemInsertDTO dto);
    ItemVO select(Long id);
    PageVO<ItemPageVO> page(Long userId, ItemPageDTO dto);
    ItemStatisticsVO statistics(Long userId);
    void update(Long userId, ItemUpdateDTO dto);
    String uploadItemPicture(Long id, MultipartFile itemPicture);
    void delete(Long id);

}

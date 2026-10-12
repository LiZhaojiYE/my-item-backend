package com.lzj.component;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.text.DateFormat;
import java.util.Date;

/** @author 李兆基 */
public class TimestampConverter implements Converter<Long> {

    @Override
    public WriteCellData<String> convertToExcelData(Long value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration){
        // 将时间戳转为日期类型
        Date date = new Date(value);
        // 将日期转为字符串类型
        String result = DateFormat.getDateTimeInstance().format(date);
        // 将结果写入单元格
        return new WriteCellData<>(result);
    }
}

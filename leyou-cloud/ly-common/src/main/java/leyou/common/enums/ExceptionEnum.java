package leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 返回提示枚举
 */
//只提供get方法
@Getter
//提供构造函数
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(400,"价格不能为空"),
    CATEGORY_NOT_FIND(404,"商品分类没有查到"),
    ;
    private int code;
    private String msg;


}

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
    BRAND_NOT_FOUND(400,"品牌不存在"),
    CATEGORY_NOT_FOUND(404,"商品分类没有查到"),
    SPEC_GROUP_NOT_FOUND(404,"商品规格组没有查到"),
    SPEC_PARAM_NOT_FOUND(404,"商品规格参数没有查到"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    BRAND_SAVE_ERROR(500,"新增商品失败"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    UPLOAD_TYPE_ERROR(500,"无效文件类型"),
    ;
    private int code;
    private String msg;


}

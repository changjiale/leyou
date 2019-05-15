package leyou.common.vo;

import leyou.common.enums.ExceptionEnum;
import lombok.Data;

/**
 * 返回统一的异常处理格式
 */
@Data
public class ExceptionResult {
    private int status;
    private String message;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum em) {
        this.status = em.getCode();
        this.message = em.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}

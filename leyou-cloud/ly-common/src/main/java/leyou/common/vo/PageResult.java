package leyou.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装分页对象
 * @param <T>
 */
@Data
public class PageResult<T> {

    private Long total;
    private Integer totalPage;
    private List<T> items; //当前页数据

    public PageResult(){

    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }


}
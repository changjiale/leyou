package leyou.item.api;

import leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {

    @GetMapping("category/list/ids")
    List<Category> queryCategoryListByIds(@RequestParam("ids") List<Long> ids);
}

package leyou.item.api;

import leyou.item.pojo.SpecGroup;
import leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.beans.Transient;
import java.util.List;

public interface SpecificationApi {
    /**
     * 根据不同条件查询规格参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */

    @GetMapping("spec/params")
    List<SpecParam> queryParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    /**
     * 根据分类查询规格组及组内属性
     * @param cid
     * @return
     */
    @GetMapping("spec/group")
    List<SpecGroup> queryListByCid(@RequestParam("cid") Long cid);
}

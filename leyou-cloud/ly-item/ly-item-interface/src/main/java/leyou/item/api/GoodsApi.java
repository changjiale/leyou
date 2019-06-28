package leyou.item.api;

import leyou.common.dto.CartDto;
import leyou.common.vo.PageResult;
import leyou.item.pojo.Sku;
import leyou.item.pojo.Spu;
import leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    /**
     * 分页查询spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @RequestMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value = "key", required = false) String key
    );
    /**
     * 根据spu的id查询详情的detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id") Long spuId);

    /**
     *根据spu的sku
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId );

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuBuId(@PathVariable("id") Long id);

    /**
     *根据sku的id的集合查询所有sku
     * @param ids
     * @return
     */
    @GetMapping("/sku/list/ids")
    List<Sku> querySkuByIds(@RequestParam("ids") List<Long> ids );

    @PostMapping("stock/decrease")
    Void decreaseStock(@RequestBody List<CartDto> carts);
}

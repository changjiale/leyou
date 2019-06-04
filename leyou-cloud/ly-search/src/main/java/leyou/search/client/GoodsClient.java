package leyou.search.client;

import leyou.common.vo.PageResult;
import leyou.item.api.GoodsApi;
import leyou.item.pojo.Sku;
import leyou.item.pojo.Spu;
import leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

}

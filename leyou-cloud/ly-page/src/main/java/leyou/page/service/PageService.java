package leyou.page.service;

import leyou.item.pojo.*;
import leyou.page.client.BrandClient;
import leyou.page.client.CategoryClient;
import leyou.page.client.GoodsClient;
import leyou.page.client.SpecificationClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specsClient;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuBuId(spuId);
        //查询sku
        List<Sku> skus = spu.getSkus();
        //查询详情s
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryListByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格参数
        List<SpecGroup> specs = specsClient.queryListByCid(spu.getCid3());
        model.put("title", spu.getTitle());
        model.put("subTitle", spu.getSubTitle());
        //model.put("spu", spu);
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("specs", specs);
        return model;
    }
}

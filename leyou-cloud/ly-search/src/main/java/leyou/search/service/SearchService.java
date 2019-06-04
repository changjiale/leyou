package leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import leyou.common.utils.JsonUtils;
import leyou.item.pojo.*;
import leyou.search.client.BrandClient;
import leyou.search.client.CategoryClient;
import leyou.search.client.GoodsClient;
import leyou.search.client.SpecificationClient;
import leyou.search.pojo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SearchService {

    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specClient;

    public Goods buildGoods(Spu spu){
        Long spuId = spu.getId();

        //查询分类
        List<Category> categories = categoryClient.queryCategoryListByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //搜索字段
        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //对sku进行处理
        List<Map<String,Object>> skus = new ArrayList<>();
        //价格集合
        Set<Long> priceList = new HashSet<>();
        for (Sku sku : skuList) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));
            skus.add(map);

            //处理价格
            priceList.add(sku.getPrice());
        }
        //Set<Long> priceList = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());


        //规格参数
        Map<String, Object> spec = new HashMap<>();
        //查询规格参数
        List<SpecParam> params = specClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);

        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils
                .nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {});


        //规格参数,key是规格参数名字， 值是规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            //规格参数名字
            String key = param.getName();
            Object value = "";
            //判断是否是通用规格参数
            if (param.getGeneric()){
                value = genericSpec.get(param.getId());
            }else {
                value =specialSpec.get(param.getId());
            }
            //存入map
            specs.put(key, value);
        }


        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);  //搜索字段 ，包含标题，分类，品牌，规格
        goods.setPrice(priceList); // 所有sku的价格集合
        goods.setSkus(JsonUtils.toString(skus)); //所有sku的集合的json
        goods.setSpecs(specs);  //todo 所有可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());

        return goods;
    }
}

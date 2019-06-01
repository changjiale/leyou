package leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Table(name = "tb_spu")
@Data
public class Spu {

    @Id
    @KeySql(useGeneratedKeys = true)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String subTitle;
    private Long cid1; //一级类目
    private Long cid2;
    private Long cid3;
    private Long brandId;
    private Boolean saleable;//是否上架
    private Boolean valid;  //是否有效，逻辑删除用
    private Date createTime;

    @JsonIgnore
    private Date lastUpdateTime;

    //spu所属的分类名称
    @Transient
    private String cname;

    //spu所属品牌名
    @Transient
    private String bname;

    //spu详情
    @Transient
    private SpuDetail spuDetail;

    //sku集合
    @Transient
    private List<Sku> skus;



}
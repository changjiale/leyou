package leyou.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_category")
@Data
public class Category{
	@Id
	//@KeySql(useGeneratedKeys = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long parentId;
	private Boolean isParent; // 注意isParent生成的getter和setter方法需要手动加上Is
	private Integer sort;

}
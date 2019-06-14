package com.hyzs.esdemo.bo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Document(indexName = "regulation",type = "docs")
@Data
public class Regulation {

    /**
     *id
     */
    @Id
    private Long id;


    /**
     *法律法规类型id
     */
    @Field(type = FieldType.Integer)
    private Integer regulationTypeId;


    /**
     *违法行为
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer="ik_smart")
    private String illegalAction;


    /**
     *层级关系路径
     */
    @Field(type = FieldType.Keyword)
    private String levelPath;

    //regulation 法律法规

    /**
     *处罚依据
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer="ik_smart")
    private String punishmentBasis;

    /**
     *适用依据
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer="ik_smart")
    private String applicableBasis;

    /**
     *记录创建时间，插入时写入，之后不会再更改
     */
    @Field(type = FieldType.Date)
    private java.util.Date gmtCreate;

    /**
     *更新时间，每次记录更新时都会刷新
     */
    @Field(type = FieldType.Date)
    private java.util.Date gmtModified;

    /**
     * 层级路径
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer="ik_smart")
    private String regulationTypeNames;//"asdfa/asdfasdf/asdfasd"

    //discretionStandar 裁量标准
    /**
     *裁量标准内容
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer="ik_smart")
    private String discretionStandardContent;

}

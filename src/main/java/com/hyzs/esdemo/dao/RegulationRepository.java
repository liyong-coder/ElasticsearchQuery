package com.hyzs.esdemo.dao;

/**
 * Created by 19130 on 2018/5/16.
 */
import com.hyzs.esdemo.bo.Regulation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
/**
 * @author liyong
 */
@Component
public interface RegulationRepository extends ElasticsearchRepository<Regulation,String>{

    /**
     * 查询雇员信息
     * @param id
     * @return
     */
    Regulation queryRegulationById(String id);
}
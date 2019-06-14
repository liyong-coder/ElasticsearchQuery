package com.hyzs.esdemo.contraller;
import com.google.gson.Gson;
import com.hyzs.esdemo.bo.Regulation;
import com.hyzs.esdemo.dao.RegulationRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.ArrayList;


/**
 * @author liyong
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegulationController {

    @Autowired
    private RegulationRepository regulationRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 查询所有es数据,并进行分页
     */
    @Test
    public void queryAll(){
        //设置分页参数
        Pageable pageable = PageRequest.of(0,10);
        //构建查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(pageable)
                .build();
        Page<Regulation> page =  regulationRepository.search(searchQuery);
        System.out.println(new Gson().toJson(page));
    }

    /**
     * 根据多字段匹配，并高亮
     */
    @Test
    public void queryByPropertiesByHighligh(){
        //设置可能出现高亮的字段
        HighlightBuilder.Field[] highLigh = {
                //注意，一定要设置requireFieldMatch为false
                new HighlightBuilder.Field("illegalAction").requireFieldMatch(false),
                new HighlightBuilder.Field("punishmentBasis").requireFieldMatch(false)
        };
        //构建查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("城市","illegalAction","punishmentBasis"))
                .withHighlightFields(highLigh)
                .build();

        AggregatedPage<Regulation> aggregatedPage = elasticsearchTemplate.queryForPage(searchQuery, Regulation.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Regulation> list = new ArrayList<Regulation>();

                SearchHits hits = searchResponse.getHits();

                for (SearchHit searchHit:hits){
                    if(hits.getHits().length <= 0){
                        return null;
                    }
                    Regulation regulation = new Regulation();
                    Map<String,Object> sourceMap =searchHit.getSourceAsMap();
                    //对没有出现高亮的字段赋值
                    regulation.setId(new Long((Integer)sourceMap.get("id")));

                    //对有可能出现高亮的字段进行赋值
                    HighlightField illegal =searchHit.getHighlightFields().get("illegalAction");
                    HighlightField punishment =searchHit.getHighlightFields().get("punishmentBasis");

                    if(illegal==null){
                        regulation.setIllegalAction((String) sourceMap.get("illegalAction"));
                    }else{
                        regulation.setIllegalAction(illegal.fragments()[0].toString());
                    }

                    if(punishment==null){
                        regulation.setPunishmentBasis((String) sourceMap.get("punishmentBasis"));
                    }else{
                        regulation.setPunishmentBasis(punishment.fragments()[0].toString());
                    }
                    list.add(regulation);

                }
                if (list.size() > 0) {
                    return new AggregatedPageImpl<T>((List<T>) list);
                }
                return null;
            }
        });
        System.out.println(new Gson().toJson(aggregatedPage));

    }

    /**
     * 通配符查询
     */
    @Test
    public void queryByWildCard(){
        //构建查询条件
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.wildcardQuery("levelPath","/1/9/1/?"))
                .build();
        Page<Regulation> page = elasticsearchTemplate.queryForPage(searchQuery,Regulation.class);
        System.out.println(new Gson().toJson(page));
    }

    /**
     * 根据Id查询
     * @return
     */
    @Test
    public void queryById() {
        Regulation accountInfo = regulationRepository.queryRegulationById("1");
        System.err.println(new Gson().toJson(accountInfo));
    }
}
package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.ESManagerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ESManagerServiceImpl implements ESManagerService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private ESManagerMapper esManagerMapper;

    //创建索引库结构
    @Override
    public void createMappingAndIndex() {
        //创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);
        //创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    //导入全部sku集合进入到索引库
    @Override
    public void importAll() {
        int page = 1;
        int pageSize = 1000;
        HashMap hashMap = new HashMap();

//        Result<PageInfo<Sku>> pageResult = skuFeign.searchPage(hashMap, page, pageSize);
//        if (pageResult != null && pageResult.getData() != null && !CollectionUtils.isEmpty(pageResult.getData().getList())) {
//            PageInfo<Sku> data = pageResult.getData();
//            int pages = data.getPages();
//            for (; page <= pages; page++) {
//                Result<PageInfo<Sku>> result = skuFeign.searchPage(hashMap, page, pageSize);
//                //将集合转换为json
//                save(result.getData().getList());
//            }
//        }

        Result<PageInfo<Sku>> result = skuFeign.searchPage(hashMap, page, pageSize);
        if (result != null && result.getData() != null && !CollectionUtils.isEmpty(result.getData().getList())) {
            PageInfo<Sku> data = result.getData();
            if (!CollectionUtils.isEmpty(data.getList())) {
                save(data.getList());
            }
            if (data.getPages() > 1) {
                page = 2;
                for (; page <= data.getPages(); page++) {
                    result = skuFeign.searchPage(hashMap, page, pageSize);
                    //将集合转换为json
                    save(result.getData().getList());
                }
            }
        }
    }

    //根据spuid查询skuList,添加到索引库
    @Override
    public void importDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size() <= 0) {
            throw new RuntimeException("当前没有数据被查询到,无法导入索引库");
        }
        //将集合转换为json
        save(skuList);
    }


    private void save(List<Sku> list) {
        //将集合转换为json
        String jsonSkuList = JSON.toJSONString(list);
        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            //将规格信息进行转换
            Map specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(specMap);
        }

        //添加索引库
        esManagerMapper.saveAll(skuInfoList);
    }

    @Override
    public void delDataBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size()<=0){
            throw new RuntimeException("当前没有数据被查询到,无法导入索引库");
        }
        for (Sku sku : skuList) {
            esManagerMapper.deleteById(Long.parseLong(sku.getId()));
        }
    }

}

package com.changgou.goods.feign;

import com.changgou.entity.Result;
import com.changgou.goods.pojo.Sku;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "goods")
public interface SkuFeign {

    @GetMapping("/sku/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping("/sku/{id}")
    public Result<Sku> findById(@PathVariable("id") String id);

    @PostMapping("/sku/decr/count")
    public Result decrCount(@RequestParam("username") String username);

    @RequestMapping("/sku/resumeStockNum")
    public Result resumeStockNum(@RequestParam("skuId") String skuId,@RequestParam("num")Integer num);

    @GetMapping(value = "/sku/searchPage/{page}/{size}")
    public Result<PageInfo<Sku>> searchPage(@RequestParam Map searchMap, @PathVariable int page, @PathVariable int size);

}

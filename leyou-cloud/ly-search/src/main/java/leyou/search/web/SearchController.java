package leyou.search.web;

import leyou.common.vo.PageResult;
import leyou.search.pojo.Goods;
import leyou.search.pojo.SearchRequest;
import leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> Search(@RequestBody SearchRequest request){
        return ResponseEntity.ok(searchService.search(request));
    }

}

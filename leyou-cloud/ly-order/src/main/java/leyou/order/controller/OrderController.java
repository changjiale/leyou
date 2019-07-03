package leyou.order.controller;

import leyou.order.dto.OrderDto;
import leyou.order.pojo.Order;
import leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 新增订单
     * @param orderDto
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto){
        //创建订单
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }

    /**
     * 查询订单
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderByid(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderByid(id));
    }
}

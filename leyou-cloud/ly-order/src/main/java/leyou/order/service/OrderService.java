package leyou.order.service;

import leyou.auth.entity.UserInfo;
import leyou.common.dto.CartDto;
import leyou.common.enums.ExceptionEnum;
import leyou.common.exception.LyException;
import leyou.common.utils.IdWorker;
import leyou.item.pojo.Sku;
import leyou.order.client.AddressClient;
import leyou.order.client.GoodsClient;
import leyou.order.dto.AddressDTO;
import leyou.order.dto.OrderDto;
import leyou.order.dto.OrderStatusEnum;
import leyou.order.interceptor.UserInterceptor;
import leyou.order.mapper.OrderDetailMapper;
import leyou.order.mapper.OrderMapper;
import leyou.order.mapper.OrderStatusMapper;
import leyou.order.pojo.Order;
import leyou.order.pojo.OrderDetail;
import leyou.order.pojo.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private GoodsClient goodsClient;

    @Transactional
    public Long createOrder(OrderDto orderDto) {
        //1新增订单
        Order order = new Order();
        //1.1新增订单编号,基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDto.getPaymentType());

        //1.2 用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);

        //1.3收获人信息
        AddressDTO addr = AddressClient.findById(orderDto.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());

        //1.4金额
        // 把cartDTO为一个map, key是sku的id， 值是num
        Map<Long, Integer> numMap = orderDto.getCarts().stream().collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        //获取所有的sku的id
        Set<Long> ids = numMap.keySet();
        //根据id来查询sku
        List<Sku> skus = goodsClient.querySkuByIds(new ArrayList<>(ids));

        //准备一个orderDetail集合
        List<OrderDetail> details = new ArrayList<>();
        long totoPay = 0L;

        for (Sku sku : skus) {
            totoPay += sku.getPrice() * numMap.get(sku.getId());
            //封装orderDetail
            OrderDetail detail = new OrderDetail();
            detail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            detail.setNum(numMap.get(sku.getId()));
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            details.add(detail);
        }
        order.setTotalPay(totoPay);
        //实付金额， 总金额 + 邮费 - 优惠金额
        order.setActualPay(totoPay + order.getPostFee() - 0);

        //1.5 order写入数据库
        int count = orderMapper.insertSelective(order);
        if (count != 1){
            log.error("[创建订单]创建订单失败,orderId:{}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //2新增订单详情
        count = orderDetailMapper.insertList(details);
        if (count != details.size()){
            log.error("[创建订单]创建订单失败,orderId:{}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }
        //3新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        count = orderStatusMapper.insertSelective(orderStatus);
        if (count != 1){
            log.error("[创建订单]创建订单失败,orderId:{}",orderId);
            throw new LyException(ExceptionEnum.CREATE_ORDER_ERROR);
        }

        //4减库存
        List<CartDto> carts = orderDto.getCarts();
        goodsClient.decreaseStock(carts);
        return orderId;
    }

    public Order queryOrderByid(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null){
            //不存在
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(detail);
       if (CollectionUtils.isEmpty(details)){
           throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
       }
       order.setOrderDetails(details);
       //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
       if (orderStatus == null){
           //不存在
           throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
       }
       order.setOrderStatus(orderStatus);
        return order;
    }
}

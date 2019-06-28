package leyou.order.enums;

public enum  OrderStatusEnum {
    UN_PAY(1, "未付款"),
    PAYED(2, "已付款，未发货"),
    DELIVERED(3, "已发货，未确认"),
    SUCCEDD(4, "已确认,交易成功"),
    CLOSE(5, "已关闭，交易失败"),
    RATED(6, "已评价"),

    ;
    private int code;
    private String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public int value(){
        return this.code;
    }
}

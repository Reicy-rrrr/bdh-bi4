package com.deloitte.bdh.data.analyse.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author bo.wang
 * @since 2020-10-19
 */
@TableName("BI_UI_ANALYSE_DEMO_SALE_DETAIL")
public class BiUiAnalyseDemoSaleDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    /**
     * 行 ID
     */
    @TableField("ROW_ID")
    private String rowId;

    /**
     * 发运天数
     */
    @TableField("SHIP_DAY")
    private String shipDay;

    /**
     * 销售年份
     */
    @TableField("ORDER_DATE_YEAR")
    private String orderDateYear;

    /**
     * 已退货？
     */
    @TableField("IS_RETURN")
    private String isReturn;

    /**
     * 退货注释
     */
    @TableField("RETURN_NOTE")
    private String returnNote;

    /**
     * 审批人
     */
    @TableField("APPROVER")
    private String approver;

    /**
     * 国家/地区
     */
    @TableField("COUNTRY")
    private String country;

    /**
     * 销售区域
     */
    @TableField("REGION")
    private String region;

    /**
     * 省/自治区
     */
    @TableField("PROVINCE")
    private String province;

    /**
     * 城市
     */
    @TableField("CITY")
    private String city;

    /**
     * 折扣
     */
    @TableField("DISCOUNT")
    private String discount;

    /**
     * 订单日期
     */
    @TableField("ORDER_DATE")
    private LocalDateTime orderDate;

    /**
     * 邮寄方式
     */
    @TableField("DELIVERY_METHOD")
    private String deliveryMethod;

    /**
     * 客户ID
     */
    @TableField("CUSTOMER_ID")
    private String customerId;

    /**
     * 客户名称
     */
    @TableField("CUSTOMER_NAME")
    private String customerName;

    /**
     * 细分
     */
    @TableField("CUSTOMER_")
    private String customer;

    /**
     * 产品 ID
     */
    @TableField("PRODUCT_ID")
    private String productId;

    /**
     * 类别
     */
    @TableField("PRODUCT_CLASSIFY")
    private String productClassify;

    /**
     * 子类别
     */
    @TableField("PRODUCT_SUBCLASS")
    private String productSubclass;

    /**
     * 产品名称
     */
    @TableField("PRODUCT_NAME")
    private String productName;

    /**
     * 数量
     */
    @TableField("QUANTITY")
    private String quantity;

    /**
     * 利润
     */
    @TableField("PROFIT")
    private String profit;

    /**
     * 销售额
     */
    @TableField("SALES")
    private String sales;

    /**
     * 订单 ID
     */
    @TableField("ORDER_ID")
    private String orderId;

    /**
     * 退货原因
     */
    @TableField("RETURN_REASON")
    private String returnReason;

    /**
     * 发货日期
     */
    @TableField("DELIVERY_DATE")
    private LocalDateTime deliveryDate;

    @TableField("CREATE_DATE")
    private LocalDateTime createDate;

    @TableField("CREATE_USER")
    private String createUser;

    @TableField("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @TableField("MODIFIED_USER")
    private String modifiedUser;

    @TableField("IP")
    private String ip;

    @TableField("TENANT_ID")
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }
    public String getShipDay() {
        return shipDay;
    }

    public void setShipDay(String shipDay) {
        this.shipDay = shipDay;
    }
    public String getOrderDateYear() {
        return orderDateYear;
    }

    public void setOrderDateYear(String orderDateYear) {
        this.orderDateYear = orderDateYear;
    }
    public String getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(String isReturn) {
        this.isReturn = isReturn;
    }
    public String getReturnNote() {
        return returnNote;
    }

    public void setReturnNote(String returnNote) {
        this.returnNote = returnNote;
    }
    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getProductClassify() {
        return productClassify;
    }

    public void setProductClassify(String productClassify) {
        this.productClassify = productClassify;
    }
    public String getProductSubclass() {
        return productSubclass;
    }

    public void setProductSubclass(String productSubclass) {
        this.productSubclass = productSubclass;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    public String getModifiedUser() {
        return modifiedUser;
    }

    public void setModifiedUser(String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "BiUiReportDemoSaleDetail{" +
        "id=" + id +
        ", rowId=" + rowId +
        ", shipDay=" + shipDay +
        ", orderDateYear=" + orderDateYear +
        ", isReturn=" + isReturn +
        ", returnNote=" + returnNote +
        ", approver=" + approver +
        ", country=" + country +
        ", region=" + region +
        ", province=" + province +
        ", city=" + city +
        ", discount=" + discount +
        ", orderDate=" + orderDate +
        ", deliveryMethod=" + deliveryMethod +
        ", customerId=" + customerId +
        ", customerName=" + customerName +
        ", customer=" + customer +
        ", productId=" + productId +
        ", productClassify=" + productClassify +
        ", productSubclass=" + productSubclass +
        ", productName=" + productName +
        ", quantity=" + quantity +
        ", profit=" + profit +
        ", sales=" + sales +
        ", orderId=" + orderId +
        ", returnReason=" + returnReason +
        ", deliveryDate=" + deliveryDate +
        ", createDate=" + createDate +
        ", createUser=" + createUser +
        ", modifiedDate=" + modifiedDate +
        ", modifiedUser=" + modifiedUser +
        ", ip=" + ip +
        ", tenantId=" + tenantId +
        "}";
    }
}

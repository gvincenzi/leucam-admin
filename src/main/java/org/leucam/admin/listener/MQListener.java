package org.leucam.admin.listener;

import com.vaadin.flow.component.UI;
import org.leucam.admin.binding.MQBinding;
import org.leucam.admin.dto.OrderDTO;
import org.leucam.admin.dto.UserDTO;
import org.leucam.admin.view.product.ProductsView;
import org.leucam.admin.view.order.OrdersView;
import org.leucam.admin.view.user.UsersView;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(MQBinding.class)
public class MQListener {
    UI ui;
    UsersView usersView;
    ProductsView productsView;
    OrdersView ordersView;

    public void setUIAndUsersViewToUpdate(UI ui, UsersView usersView){
        this.ui = ui;
        this.usersView = usersView;
    }

    public void setUIAndOrdersViewToUpdate(UI ui, OrdersView ordersView){
        this.ui = ui;
        this.ordersView = ordersView;
    }

    @StreamListener(target = MQBinding.USER_REGISTRATION)
    public void processUserRegistration(UserDTO msg) {
        if(usersView != null) {
            ui.access(() -> usersView.refreshUserGrid());
        }
    }

    @StreamListener(target = MQBinding.USER_ORDER)
    public void processUserOrderRegistration(OrderDTO msg) {
        if(productsView != null) {
            ui.access(() -> productsView.refreshProductGrid());
            ui.access(() -> productsView.refreshProductOrdersGrid(msg.getProduct().getProductId()));
        }
        if(ordersView != null){
            ui.access(()-> ordersView.refreshOrdersGrid());
        }
    }

    @StreamListener(target = MQBinding.ORDER_PAYMENT_CONFIRMATION)
    public void processOrderPaymentConfirmation(OrderDTO msg) {
        if(usersView != null){
            ui.access(()->usersView.refreshUserGrid());
        }
        if(productsView != null){
            ui.access(()->productsView.refreshProductOrdersGrid(msg.getProduct().getProductId()));
        }
        if(ordersView != null){
            ui.access(()-> ordersView.refreshOrdersGrid());
        }
    }

    @StreamListener(target = MQBinding.USER_CANCELLATION)
    public void processUserCancellation(UserDTO msg) {
        if(usersView != null){
            ui.access(()->usersView.refreshUserGrid());
        }
    }

    @StreamListener(target = MQBinding.RECHARGE_USER_CREDIT)
    public void processRechargeUserCredit(UserDTO msg) {
        if(usersView != null){
            ui.access(()->usersView.refreshUserGrid());
        }
    }

    @StreamListener(target = MQBinding.ORDER_CANCELLATION)
    public void processOrderCancellation(OrderDTO msg) {
        if(productsView != null) {
            ui.access(() -> productsView.refreshProductGrid());
            ui.access(() -> productsView.refreshProductOrdersGrid(msg.getProduct().getProductId()));
        }
        if(ordersView != null){
            ui.access(()-> ordersView.refreshOrdersGrid());
        }
        if(usersView != null){
            ui.access(()->usersView.refreshUserGrid());
        }
    }

    public void setUIAndProductsViewToUpdate(UI ui, ProductsView productsView) {
        this.ui = ui;
        this.productsView = productsView;
    }
}

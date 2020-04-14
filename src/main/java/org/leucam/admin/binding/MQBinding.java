package org.leucam.admin.binding;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MQBinding {
    String USER_REGISTRATION = "userRegistrationChannel";
    String USER_ORDER = "userOrderChannel";
    String ORDER_PAYMENT_CONFIRMATION = "orderPaymentConfirmationChannel";
    String USER_CANCELLATION = "userCancellationChannel";
    String RECHARGE_USER_CREDIT = "rechargeUserCreditChannel";
    String ORDER_CANCELLATION = "orderCancellationChannel";

    @Input(USER_REGISTRATION)
    SubscribableChannel userRegistrationChannel();

    @Input(USER_ORDER)
    SubscribableChannel userOrderChannel();

    @Input(ORDER_PAYMENT_CONFIRMATION)
    SubscribableChannel userOrderPaymentConfirmationChannel();

    @Input(USER_CANCELLATION)
    SubscribableChannel userCancellationChannel();

    @Input(RECHARGE_USER_CREDIT)
    SubscribableChannel rechargeUserCreditChannel();

    @Input(ORDER_CANCELLATION)
    SubscribableChannel orderCancellationChannel();
}

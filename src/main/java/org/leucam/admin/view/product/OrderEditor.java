package org.leucam.admin.view.product;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.leucam.admin.client.OrderResourceClient;
import org.leucam.admin.dto.OrderDTO;
import org.leucam.admin.view.ButtonLabelConfig;

import java.time.LocalDateTime;

@SpringComponent
@UIScope
public class OrderEditor extends VerticalLayout implements KeyNotifier {
    private final OrderResourceClient orderResourceClient;
    private final OrderLabelConfig orderLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;
    private OrderDTO orderDTO;

    /* Fields to edit properties in Order entity */
    Checkbox paid;

    /* Action buttons */
    private final Button save, delete, prepared, notPrepared, delivered, notDelivered;

    Binder<OrderDTO> binder = new Binder<>(OrderDTO.class);
    private ChangeHandler changeHandler;

    public OrderEditor(OrderResourceClient orderResourceClient, OrderLabelConfig orderLabelConfig, ButtonLabelConfig buttonLabelConfig) {
        this.orderResourceClient = orderResourceClient;
        this.orderLabelConfig = orderLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;

        paid = new Checkbox(orderLabelConfig.getPaid());
        save = new Button(buttonLabelConfig.getSave(), VaadinIcon.CHECK.create());
        delete = new Button(buttonLabelConfig.getDelete(), VaadinIcon.TRASH.create());

        //HorizontalLayout actions = new HorizontalLayout(save, delete);

        /* Item Action buttons */
        prepared = new Button(buttonLabelConfig.getPrepared(), VaadinIcon.CHECK.create());
        notPrepared = new Button(buttonLabelConfig.getNotPrepared(), VaadinIcon.BAN.create());
        delivered = new Button(buttonLabelConfig.getDelivered(), VaadinIcon.PACKAGE.create());
        notDelivered = new Button(buttonLabelConfig.getNotDelivered(), VaadinIcon.BAN.create());
        prepared.getElement().getThemeList().add("success");
        notPrepared.getElement().getThemeList().add("error");
        delivered.getElement().getThemeList().add("success");
        notDelivered.getElement().getThemeList().add("error");

        prepared.setVisible(false);
        notPrepared.setVisible(false);
        delivered.setVisible(false);
        notDelivered.setVisible(false);

        this.prepared.addClickListener(e -> {
            orderDTO.setOrderPreparationDate(LocalDateTime.now());
            orderResourceClient.updateOrder(orderDTO.getOrderId(), orderDTO);
            updateActionsVisibility(orderDTO);
        });

        this.notPrepared.addClickListener(e -> {
            orderDTO.setOrderPreparationDate(null);
            orderResourceClient.updateOrder(orderDTO.getOrderId(), orderDTO);
            updateActionsVisibility(orderDTO);
        });

        this.delivered.addClickListener(e -> {
            orderDTO.setOrderDeliveryDate(LocalDateTime.now());
            orderResourceClient.updateOrder(orderDTO.getOrderId(), orderDTO);
            updateActionsVisibility(orderDTO);
        });

        this.notDelivered.addClickListener(e -> {
            orderDTO.setOrderDeliveryDate(null);
            orderResourceClient.updateOrder(orderDTO.getOrderId(), orderDTO);
            updateActionsVisibility(orderDTO);
        });

        // build layout
        HorizontalLayout preparationActions = new HorizontalLayout(prepared, notPrepared);
        HorizontalLayout deliveryActions = new HorizontalLayout(delivered, notDelivered);
        VerticalLayout itemActions = new VerticalLayout(preparationActions, deliveryActions, delete);

        //add(paid, actions);
        add(itemActions);

        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        save.getElement().

                getThemeList().

                add("primary");
        delete.getElement().

                getThemeList().

                add("error");

        addKeyPressListener(Key.ENTER, e ->

                save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e ->

                save());
        delete.addClickListener(e ->

                delete());

        setVisible(false);
    }

    void delete() {
        orderResourceClient.deleteOrder(orderDTO.getOrderId());
        changeHandler.onChange();
    }

    void save() {
        orderResourceClient.updateOrder(orderDTO.getOrderId(), orderDTO);
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();

    }

    public final void editOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            setVisible(false);
            prepared.setVisible(false);
            notPrepared.setVisible(false);
            delivered.setVisible(false);
            notDelivered.setVisible(false);
            return;
        }
        final boolean persisted = orderDTO.getOrderId() != null;
        this.orderDTO = orderDTO;

        // Bind order properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.orderDTO);

        setVisible(true);

        updateActionsVisibility(orderDTO);

        // Focus first name initially
        paid.focus();
    }

    private void updateActionsVisibility(OrderDTO orderDTO) {
        prepared.setVisible(false);
        notPrepared.setVisible(false);
        delivered.setVisible(false);
        notDelivered.setVisible(false);

        if(orderDTO != null) {
            if (orderDTO.getOrderPreparationDate() != null) {
                prepared.setVisible(false);
                notPrepared.setVisible(true);

                if (orderDTO.getOrderDeliveryDate() != null) {
                    delivered.setVisible(false);
                    notDelivered.setVisible(true);
                } else {
                    delivered.setVisible(true);
                    notDelivered.setVisible(false);
                }

            } else {
                prepared.setVisible(true);
                notPrepared.setVisible(false);
            }
        }
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }
}

package org.leucam.admin.view.order;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.leucam.admin.client.OrderResourceClient;
import org.leucam.admin.client.ProductResourceClient;
import org.leucam.admin.dto.OrderDTO;
import org.leucam.admin.dto.ProductDTO;
import org.leucam.admin.dto.type.ActionType;
import org.leucam.admin.listener.MQListener;
import org.leucam.admin.view.ButtonLabelConfig;
import org.leucam.admin.view.product.OrderLabelConfig;
import org.leucam.admin.view.product.ProductLabelConfig;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Push
@Route
@PageTitle("Leucam - Print orders")
public class OrdersView extends VerticalLayout implements KeyNotifier {
    private final ProductResourceClient productResourceClient;
    private final OrderResourceClient orderResourceClient;
    private final ProductLabelConfig productLabelConfig;
    private final OrderLabelConfig orderLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;
    private final Checkbox showDone;

    private final MQListener mqListener;

    final Grid<OrderDTO> grid;
    private final Button usersBtn, productBtn, logoutBtn;
    private final Button openDocument, putInCatalog, removeFromCatalog, prepared, notPrepared, delivered, notDelivered, delete;

    public OrdersView(ProductResourceClient productResourceClient, OrderResourceClient orderResourceClient, ProductLabelConfig productLabelConfig, OrderLabelConfig orderLabelConfig, ButtonLabelConfig buttonLabelConfig, MQListener mqListener) {
        this.productResourceClient = productResourceClient;
        this.orderResourceClient = orderResourceClient;
        this.productLabelConfig = productLabelConfig;
        this.orderLabelConfig = orderLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;
        this.mqListener = mqListener;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () -> inputStream);
        Image logo = new Image(resource, "Leucam Logo");
        logo.setMaxWidth("370px");
        add(logo);

        this.showDone = new Checkbox(orderLabelConfig.getShowDone());
        this.showDone.addClickListener(e -> {
            refreshOrdersGrid(orderResourceClient);
        });

        this.grid = new Grid<>(OrderDTO.class);

        this.usersBtn = new Button(buttonLabelConfig.getUserManagement(), VaadinIcon.USERS.create());
        usersBtn.addClickListener(e ->
                usersBtn.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        this.productBtn = new Button(buttonLabelConfig.getProductManagement(), VaadinIcon.BOOK_DOLLAR.create());
        productBtn.addClickListener(e ->
                productBtn.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );

        this.logoutBtn = new Button("Logout", VaadinIcon.EXIT.create());
        this.logoutBtn.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().getPage().setLocation("logout");
        });

        /* Item Action buttons */
        openDocument = new Button(buttonLabelConfig.getOpenDocument(), VaadinIcon.BULLSEYE.create());
        removeFromCatalog = new Button(buttonLabelConfig.getRemoveFromCatalog(), VaadinIcon.MINUS.create());
        putInCatalog = new Button(buttonLabelConfig.getPutInCatalog(), VaadinIcon.PLUS.create());
        prepared = new Button(buttonLabelConfig.getPrepared(), VaadinIcon.CHECK.create());
        notPrepared = new Button(buttonLabelConfig.getNotPrepared(), VaadinIcon.BAN.create());
        delivered = new Button(buttonLabelConfig.getDelivered(), VaadinIcon.PACKAGE.create());
        notDelivered = new Button(buttonLabelConfig.getNotDelivered(), VaadinIcon.BAN.create());
        delete = new Button(buttonLabelConfig.getDelete(), VaadinIcon.TRASH.create());
        openDocument.getElement().getThemeList().add("success");
        putInCatalog.getElement().getThemeList().add("success");
        removeFromCatalog.getElement().getThemeList().add("error");
        prepared.getElement().getThemeList().add("success");
        notPrepared.getElement().getThemeList().add("error");
        delivered.getElement().getThemeList().add("success");
        notDelivered.getElement().getThemeList().add("error");
        delete.getElement().getThemeList().add("error");

        openDocument.setVisible(false);
        delete.setVisible(false);
        putInCatalog.setVisible(false);
        removeFromCatalog.setVisible(false);
        prepared.setVisible(false);
        notPrepared.setVisible(false);
        delivered.setVisible(false);
        notDelivered.setVisible(false);

        this.putInCatalog.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                ProductDTO productDTO = orderDTOSelected.getProduct();
                if (!productDTO.getActive()) {
                    productDTO.setActive(true);
                    productResourceClient.updateProduct(productDTO.getProductId(), productDTO);
                }
                refreshOrdersGrid();
            }
        });

        this.removeFromCatalog.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                ProductDTO productDTO = orderDTOSelected.getProduct();
                if (productDTO.getActive()) {
                    productDTO.setActive(false);
                    productResourceClient.updateProduct(productDTO.getProductId(), productDTO);
                }
                refreshOrdersGrid();
            }
        });

        this.prepared.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                orderDTOSelected.setOrderPreparationDate(LocalDateTime.now());
                orderResourceClient.updateOrder(orderDTOSelected.getOrderId(), orderDTOSelected);
                refreshOrdersGrid();
            }
        });

        this.notPrepared.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                orderDTOSelected.setOrderPreparationDate(null);
                orderResourceClient.updateOrder(orderDTOSelected.getOrderId(),orderDTOSelected);
                refreshOrdersGrid();
            }
        });

        this.delivered.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                orderDTOSelected.setOrderDeliveryDate(LocalDateTime.now());
                orderResourceClient.updateOrder(orderDTOSelected.getOrderId(),orderDTOSelected);
                refreshOrdersGrid();
            }
        });

        this.notDelivered.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                orderDTOSelected.setOrderDeliveryDate(null);
                orderResourceClient.updateOrder(orderDTOSelected.getOrderId(), orderDTOSelected);
                refreshOrdersGrid();
            }
        });

        this.openDocument.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                this.openDocument.getUI().ifPresent(ui ->
                        ui.getPage().open(orderDTOSelected.getProduct().getFilePath())
                );
            }
        });

        this.delete.addClickListener(e -> {
            if(!grid.getSelectedItems().isEmpty()) {
                OrderDTO orderDTOSelected = grid.getSelectedItems().iterator().next();
                orderResourceClient.deleteOrder(orderDTOSelected.getOrderId());
                grid.deselectAll();
                refreshOrdersGrid();
            }
        });

        // build layout
        HorizontalLayout catalogActions = new HorizontalLayout(putInCatalog, removeFromCatalog);
        HorizontalLayout preparationActions = new HorizontalLayout(prepared, notPrepared);
        HorizontalLayout deliveryActions = new HorizontalLayout(delivered, notDelivered);
        VerticalLayout itemActions = new VerticalLayout(openDocument,catalogActions,preparationActions,deliveryActions,delete);

        // build layout
        HorizontalLayout actions = new HorizontalLayout(usersBtn, productBtn, logoutBtn);
        add(actions, showDone, grid, itemActions);

        refreshOrdersGrid(orderResourceClient);
        grid.setHeight("300px");

        grid.setColumns("product.name", "product.filePath", "user", "actionType", "frontBackType", "colorType", "numberOfCopies", "paid", "pagesPerSheet", "paymentExternalReference", "paymentExternalDateTime", "amount");
        grid.getColumnByKey("product.name").setHeader(productLabelConfig.getName());
        grid.getColumnByKey("product.filePath").setHeader(productLabelConfig.getFilePath());
        grid.getColumnByKey("user").setHeader(orderLabelConfig.getUser());
        grid.getColumnByKey("actionType").setHeader(orderLabelConfig.getActionType());
        grid.getColumnByKey("frontBackType").setHeader(orderLabelConfig.getFrontBackType());
        grid.getColumnByKey("colorType").setHeader(orderLabelConfig.getColorType());
        grid.getColumnByKey("numberOfCopies").setHeader(orderLabelConfig.getNumberOfCopies());
        grid.getColumnByKey("pagesPerSheet").setHeader(orderLabelConfig.getPagesPerSheet());
        grid.getColumnByKey("paid").setHeader(orderLabelConfig.getPaid());
        grid.getColumnByKey("paymentExternalReference").setHeader(orderLabelConfig.getPaymentExternalReference());
        grid.getColumnByKey("paymentExternalDateTime").setHeader(orderLabelConfig.getPaymentExternalDateTime());
        grid.getColumnByKey("amount").setHeader(orderLabelConfig.getAmount());

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            openDocument.setVisible(false);
            delete.setVisible(false);
            putInCatalog.setVisible(false);
            removeFromCatalog.setVisible(false);
            prepared.setVisible(false);
            notPrepared.setVisible(false);
            delivered.setVisible(false);
            notDelivered.setVisible(false);
            if (e.getValue()!=null) {

                openDocument.setVisible(true);
                delete.setVisible(true);

                if (e.getValue().getProduct().getActive()) {
                    putInCatalog.setVisible(false);
                    removeFromCatalog.setVisible(true);
                } else {
                    putInCatalog.setVisible(true);
                    removeFromCatalog.setVisible(false);
                }

                if (e.getValue().getOrderPreparationDate() != null) {
                    prepared.setVisible(false);
                    notPrepared.setVisible(true);

                    if (e.getValue().getOrderDeliveryDate() != null) {
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
        });
    }

    private void refreshOrdersGrid(OrderResourceClient orderResourceClient) {
        List<OrderDTO> orderDTOS = new ArrayList<>();
        if (this.showDone.getValue()) {
            for (ActionType actionType: ActionType.values()) {
                orderDTOS.addAll(orderResourceClient.findAllOrdersByActionType(actionType));
            }
        } else {
            for (ActionType actionType: ActionType.values()) {
                orderDTOS.addAll(orderResourceClient.findOrdersByActionType(actionType));
            }
        }

        grid.setItems(orderDTOS);
    }

    public void refreshOrdersGrid(){
        OrderDTO orderDTOSelected = null;
        if(!grid.getSelectedItems().isEmpty()) {
            orderDTOSelected = grid.getSelectedItems().iterator().next();
        }
        refreshOrdersGrid(orderResourceClient);

        if(orderDTOSelected != null){
            grid.select(orderDTOSelected);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.mqListener.setUIAndOrdersViewToUpdate(attachEvent.getUI(), this);
    }
}

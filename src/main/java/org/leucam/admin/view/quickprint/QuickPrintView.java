package org.leucam.admin.view.quickprint;

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
import org.leucam.admin.dto.OrderDTO;
import org.leucam.admin.dto.ProductDTO;
import org.leucam.admin.dto.type.ActionType;
import org.leucam.admin.listener.MQListener;
import org.leucam.admin.view.ButtonLabelConfig;
import org.leucam.admin.view.product.OrderLabelConfig;
import org.leucam.admin.view.product.ProductLabelConfig;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;

@Push
@Route
@PageTitle("Leucam - Quick print")
public class QuickPrintView extends VerticalLayout implements KeyNotifier {
    private final OrderResourceClient orderResourceClient;
    private final ProductLabelConfig productLabelConfig;
    private final OrderLabelConfig orderLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;
    private final Checkbox showDone;

    private final MQListener mqListener;

    final Grid<OrderDTO> grid;
    private final Button usersBtn, productBtn, logoutBtn;

    public QuickPrintView(OrderResourceClient orderResourceClient, ProductLabelConfig productLabelConfig, OrderLabelConfig orderLabelConfig, ButtonLabelConfig buttonLabelConfig, MQListener mqListener) {
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
            refreshQuickPrintGrid(orderResourceClient);
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

        // build layout
        HorizontalLayout actions = new HorizontalLayout(usersBtn, productBtn, logoutBtn);
        add(actions, grid);

        refreshQuickPrintGrid(orderResourceClient);
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
    }

    private void refreshQuickPrintGrid(OrderResourceClient orderResourceClient) {
        if (this.showDone.getValue()) {
            grid.setItems(orderResourceClient.findOrdersByActionType(ActionType.QUICK_PRINT));
        } else {
            grid.setItems(orderResourceClient.findOrdersByActionType(ActionType.QUICK_PRINT));
        }
    }

    public void refreshQuickPrintGrid(){
        OrderDTO orderDTOSelected = null;
        if(!grid.getSelectedItems().isEmpty()) {
            orderDTOSelected = grid.getSelectedItems().iterator().next();
        }
        refreshQuickPrintGrid(orderResourceClient);

        if(orderDTOSelected != null){
            grid.select(orderDTOSelected);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.mqListener.setUIAndQuickPrintViewToUpdate(attachEvent.getUI(), this);
    }
}

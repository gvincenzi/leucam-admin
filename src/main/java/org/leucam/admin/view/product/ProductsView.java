package org.leucam.admin.view.product;

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
import org.leucam.admin.client.ProductResourceClient;
import org.leucam.admin.dto.ProductDTO;
import org.leucam.admin.listener.MQListener;
import org.leucam.admin.view.ButtonLabelConfig;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;

@Push
@Route
@PageTitle("Leucam - Product List")
public class ProductsView extends VerticalLayout implements KeyNotifier {
    private final ProductResourceClient productResourceClient;
    private final ProductEditor productEditor;
    private final ProductLabelConfig productLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    private final MQListener mqListener;
    final Grid<ProductDTO> grid;
    private final Button addNewBtn, usersBtn, logoutBtn;
    private final Checkbox showHistory;

    public ProductsView(ProductResourceClient productResourceClient, ProductEditor productEditor, ProductLabelConfig productLabelConfig, ButtonLabelConfig buttonLabelConfig, MQListener mqListener) {
        this.productEditor = productEditor;
        this.productResourceClient = productResourceClient;
        this.buttonLabelConfig = buttonLabelConfig;
        this.productLabelConfig = productLabelConfig;
        this.mqListener = mqListener;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "Leucam Logo");
        logo.setMaxWidth("370px");
        add(logo);

        this.showHistory = new Checkbox(productLabelConfig.getShowHistory());
        this.showHistory.addClickListener(e -> {
            refreshProductGrid(productResourceClient);
        });

        this.grid = new Grid<>(ProductDTO.class);

        this.addNewBtn = new Button(buttonLabelConfig.getProductNew(), VaadinIcon.PLUS.create());
        this.usersBtn = new Button(buttonLabelConfig.getUserManagement(), VaadinIcon.USERS.create());
        usersBtn.addClickListener(e ->
                usersBtn.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        this.logoutBtn = new Button("Logout", VaadinIcon.EXIT.create());
        this.logoutBtn.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().getPage().setLocation("logout");
        });

        // build layout
        HorizontalLayout actions = new HorizontalLayout(addNewBtn, usersBtn, logoutBtn);
        add(actions, showHistory, grid, productEditor);

        refreshProductGrid(productResourceClient);
        grid.setHeight("300px");

        grid.setColumns("name","description","fileId","filePath","active");
        grid.getColumnByKey("name").setHeader(productLabelConfig.getName());
        grid.getColumnByKey("description").setHeader(productLabelConfig.getDescription());
        grid.getColumnByKey("fileId").setHeader(productLabelConfig.getFileId());
        grid.getColumnByKey("filePath").setHeader(productLabelConfig.getFilePath());
        grid.getColumnByKey("active").setHeader(productLabelConfig.getActive());

        // Connect selected Product to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            productEditor.editProduct(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> productEditor.editProduct(new ProductDTO()));

        // Listen changes made by the editor, refresh data from backend
        productEditor.setChangeHandler(() -> {
            productEditor.setVisible(false);
            refreshProductGrid(productResourceClient);
        });
    }

    private void refreshProductGrid(ProductResourceClient productResourceClient) {
        if(this.showHistory.getValue()){
            grid.setItems(productResourceClient.getHistory());
        } else {
            grid.setItems(productResourceClient.findAll());
        }
    }

    public void refreshProductGrid(){
        ProductDTO productDTOSelected = null;
        if(!grid.getSelectedItems().isEmpty()) {
            productDTOSelected = grid.getSelectedItems().iterator().next();
        }
        refreshProductGrid(productResourceClient);

        if(productDTOSelected != null){
            grid.select(productDTOSelected);
        }
    }

    public void refreshProductOrdersGrid(Long productId){
        productEditor.refreshProductOrdersGrid(productId);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.mqListener.setUIAndProductsViewToUpdate(attachEvent.getUI(), this);
    }
}

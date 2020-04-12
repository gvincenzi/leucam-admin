package org.leucam.admin.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;

@Route
@PageTitle("Leucam - Welcome page")
public class MainView extends VerticalLayout {
    private ButtonLabelConfig buttonLabelConfig;

    public MainView(ButtonLabelConfig buttonLabelConfig) {
        this.buttonLabelConfig = buttonLabelConfig;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "Leucam Logo");
        logo.setMaxWidth("370px");
        add(logo);
        Button buttonUser = new Button(
                buttonLabelConfig.getUserManagement(), VaadinIcon.USERS.create());
        buttonUser.addClickListener(e ->
                buttonUser.getUI().ifPresent(ui ->
                        ui.navigate("users"))
        );

        Button buttonProduct = new Button(
                buttonLabelConfig.getProductManagement(), VaadinIcon.BOOK_DOLLAR.create());
        buttonProduct.addClickListener(e ->
                buttonProduct.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );

        Button buttonLogout = new Button("Logout", VaadinIcon.EXIT.create());
        buttonLogout.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().getPage().setLocation("logout");
        });

        // build layout
        HorizontalLayout actions = new HorizontalLayout(buttonUser, buttonProduct, buttonLogout);
        add(actions);
    }
}

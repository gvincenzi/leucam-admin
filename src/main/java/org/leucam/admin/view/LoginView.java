package org.leucam.admin.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Leucam - Login")
public class LoginView extends VerticalLayout {
    public static final String ROUTE = "login";

    private LoginOverlay login = new LoginOverlay();

    public LoginView(){
        login.setAction("login");
        login.setOpened(true);
        login.setTitle("Leucam");
        login.setDescription("Sales Management Open Source Software");
        getElement().appendChild(login.getElement());
    }
}

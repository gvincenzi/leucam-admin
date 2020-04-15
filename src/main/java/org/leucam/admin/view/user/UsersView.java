package org.leucam.admin.view.user;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.leucam.admin.client.UserCreditResourceClient;
import org.leucam.admin.client.UserResourceClient;
import org.leucam.admin.dto.UserCreditDTO;
import org.leucam.admin.dto.UserDTO;
import org.leucam.admin.listener.MQListener;
import org.leucam.admin.view.ButtonLabelConfig;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.util.List;

@Push
@Route
@PageTitle("Leucam - User list")
public class UsersView extends VerticalLayout implements KeyNotifier {
    private final UserResourceClient userResourceClient;
    private final UserCreditResourceClient userCreditResourceClient;
    private final UserEditor userEditor;
    private final UserLabelConfig userLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;
    private final MQListener mqListener;
    final Grid<UserDTO> grid;
    private final Button addNewBtn, productBtn, ordersBtn, logoutBtn;

    public UsersView(UserResourceClient userResourceClient, UserCreditResourceClient userCreditResourceClient, UserEditor userEditor, UserLabelConfig userLabelConfig, ButtonLabelConfig buttonLabelConfig, MQListener mqListener) {
        this.userEditor = userEditor;
        this.userResourceClient = userResourceClient;
        this.userCreditResourceClient = userCreditResourceClient;
        this.userLabelConfig = userLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;
        this.mqListener = mqListener;

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("static/logo.png");
        StreamResource resource = new StreamResource("logo.png", () ->  inputStream);
        Image logo = new Image(resource, "Leucam Logo");
        logo.setMaxWidth("370px");
        add(logo);

        this.grid = new Grid<>(UserDTO.class);
        this.addNewBtn = new Button(buttonLabelConfig.getUserNew(), VaadinIcon.PLUS.create());
        this.productBtn = new Button(buttonLabelConfig.getProductManagement(), VaadinIcon.BOOK_DOLLAR.create());
        productBtn.addClickListener(e ->
                productBtn.getUI().ifPresent(ui ->
                        ui.navigate("products"))
        );
        this.ordersBtn = new Button(
                buttonLabelConfig.getOrdersManagement(), VaadinIcon.COPY.create());
        ordersBtn.addClickListener(e ->
                ordersBtn.getUI().ifPresent(ui ->
                        ui.navigate("orders"))
        );
        this.logoutBtn = new Button("Logout", VaadinIcon.EXIT.create());
        this.logoutBtn.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            UI.getCurrent().getPage().setLocation("logout");
        });


        // build layout
        HorizontalLayout actions = new HorizontalLayout(productBtn, ordersBtn, logoutBtn);

        Text text = new Text(String.format("%s : %s €",userLabelConfig.getCashFund(),userCreditResourceClient.totalUserCredit().toString()));
        add(actions, addNewBtn, grid, text, userEditor);

        grid.setItems(setUserGridItems(userResourceClient));
        grid.setHeight("300px");

        grid.setColumns("name","surname","mail","credit","administrator");
        grid.getColumnByKey("name").setHeader(userLabelConfig.getFirstname());
        grid.getColumnByKey("surname").setHeader(userLabelConfig.getLastname());
        grid.getColumnByKey("mail").setHeader(userLabelConfig.getMail());
        // grid.getColumnByKey("active").setHeader(userLabelConfig.getActive());
        grid.getColumnByKey("credit").setHeader(userLabelConfig.getCredit());
        grid.getColumnByKey("administrator").setHeader(userLabelConfig.getAdministrator());

        // Connect selected User to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> {
            userEditor.editUser(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> userEditor.editUser(new UserDTO()));

        // Listen changes made by the editor, refresh data from backend
        userEditor.setChangeHandler(() -> {
            userEditor.setVisible(false);
            grid.setItems(setUserGridItems(userResourceClient));
        });
    }

    private List<UserDTO> setUserGridItems(UserResourceClient userResourceClient) {
        List<UserDTO> userDTOS = userResourceClient.findByActiveTrue();
        for (UserDTO userDTO: userDTOS) {
            UserCreditDTO userCreditDTO = userCreditResourceClient.findById(userDTO.getId());
            if(userCreditDTO != null && userCreditDTO.getCredit() != null){
                userDTO.setCredit(userCreditDTO.getCredit());
            }
        }
        return userDTOS;
    }

    public void refreshUserGrid(){
        UserDTO userDTOSelected = null;
        if(!grid.getSelectedItems().isEmpty()) {
            userDTOSelected = grid.getSelectedItems().iterator().next();
        }

        grid.setItems(setUserGridItems(userResourceClient));

        if(userDTOSelected != null){
            grid.select(userDTOSelected);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.mqListener.setUIAndUsersViewToUpdate(attachEvent.getUI(), this);
    }
}

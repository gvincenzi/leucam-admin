package org.leucam.admin.view.user;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.leucam.admin.client.UserCreditResourceClient;
import org.leucam.admin.client.UserResourceClient;
import org.leucam.admin.dto.RechargeUserCreditLogDTO;
import org.leucam.admin.dto.UserCreditDTO;
import org.leucam.admin.dto.UserDTO;
import org.leucam.admin.view.ButtonLabelConfig;

import java.math.BigDecimal;
import java.util.ArrayList;

@SpringComponent
@UIScope
public class UserEditor extends HorizontalLayout implements KeyNotifier {
    private final UserResourceClient userResourceClient;
    private final UserCreditResourceClient userCreditResourceClient;
    private final UserLabelConfig userLabelConfig;
    private final ButtonLabelConfig buttonLabelConfig;

    private Grid<RechargeUserCreditLogDTO> rechargeUserCreditLogGrid;

    private UserDTO userDTO;

    Binder<UserDTO> binder = new Binder<>(UserDTO.class);
    private ChangeHandler changeHandler;

    private TextField name,surname,mail;
    private Checkbox administrator;
    private Button save,reset,delete;
    NumberField credit;

    public UserEditor(UserResourceClient userResourceClient, UserCreditResourceClient userCreditResourceClient, UserLabelConfig userLabelConfig, ButtonLabelConfig buttonLabelConfig) {
        this.userResourceClient = userResourceClient;
        this.userCreditResourceClient = userCreditResourceClient;
        this.userLabelConfig = userLabelConfig;
        this.buttonLabelConfig = buttonLabelConfig;

        /* Fields to edit properties in User entity */
        name = new TextField(userLabelConfig.getFirstname());
        surname = new TextField(userLabelConfig.getLastname());
        mail  = new TextField(userLabelConfig.getMail());
        administrator = new Checkbox(userLabelConfig.getAdministrator());
        credit  = new NumberField(userLabelConfig.getCredit());
        HorizontalLayout data = new HorizontalLayout(name, surname, mail, credit);

        /* Action buttons */
        save = new Button(buttonLabelConfig.getSave(), VaadinIcon.CHECK.create());
        reset = new Button(buttonLabelConfig.getReset());
        delete = new Button(buttonLabelConfig.getDelete(), VaadinIcon.TRASH.create());
        HorizontalLayout actions = new HorizontalLayout(save, reset, delete);

        VerticalLayout userEditor = new VerticalLayout(data, administrator, actions);

        this.rechargeUserCreditLogGrid = new Grid<>(RechargeUserCreditLogDTO.class);
        rechargeUserCreditLogGrid.setColumns("oldCredit","newCredit","rechargeUserCreditType","rechargeDateTime");
        rechargeUserCreditLogGrid.getColumnByKey("oldCredit").setHeader(userLabelConfig.getOldCredit());
        rechargeUserCreditLogGrid.getColumnByKey("newCredit").setHeader(userLabelConfig.getNewCredit());
        rechargeUserCreditLogGrid.getColumnByKey("rechargeUserCreditType").setHeader(userLabelConfig.getRechargeUserCreditType());
        rechargeUserCreditLogGrid.getColumnByKey("rechargeDateTime").setHeader(userLabelConfig.getRechargeDateTime());
        rechargeUserCreditLogGrid.setWidthFull();
        this.setWidthFull();
        add(userEditor,rechargeUserCreditLogGrid);

        // bind using naming convention
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        reset.addClickListener(e -> editUser(userDTO));
        setVisible(false);
    }

    void delete() {
        userResourceClient.deleteUser(userDTO.getId());
        changeHandler.onChange();
    }

    void save() {
        final boolean persisted = userDTO.getId() != null;
        if (persisted) {
            userResourceClient.updateUser(userDTO.getId(), userDTO);
            userCreditResourceClient.newCredit(userDTO, new BigDecimal(userDTO.getCredit()));
        } else {
            userDTO = userResourceClient.addUser(userDTO);
            userCreditResourceClient.newCredit(userDTO, new BigDecimal(userDTO.getCredit()));
        }
        changeHandler.onChange();
    }

    public interface ChangeHandler {
        void onChange();
    }

    public final void editUser(UserDTO userDTO) {
        if (userDTO == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = userDTO.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            this.userDTO = userResourceClient.findById(userDTO.getId());
            UserCreditDTO userCreditDTO = userCreditResourceClient.findById(this.userDTO.getId());
            if(userCreditDTO != null){
                this.userDTO.setCredit(userCreditDTO.getCredit());
            }
            rechargeUserCreditLogGrid.setItems(userCreditResourceClient.findRechargeUserCreditLogByUserId(this.userDTO.getId()));
        }
        else {
            this.userDTO = userDTO;
            rechargeUserCreditLogGrid.setItems(new ArrayList(0));
        }
        reset.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(this.userDTO);

        setVisible(true);

        // Focus first name initially
        name.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        changeHandler = h;
    }
}

package com.NBP;

import com.NBP.dao.ExchangeRateDao;
import com.NBP.dao.ExchangeRateImpl;
import com.NBP.model.ExchangeForRaport;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Raport10 extends VerticalLayout implements View{

    private Button btnback = new Button("powrót");

    private final Label label = new Label();

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateImpl();

    private void configure() {
        btnback.addClickListener(e -> {
            Navigator navigator = UI.getCurrent().getNavigator();
            navigator.navigateTo(Views.VIEW_main.toString());
        });
    }

    private HorizontalLayout createFormButton() {
        btnback.setIcon(VaadinIcons.ARROW_BACKWARD);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(btnback);

        return horizontalLayout;
    }


    private HorizontalLayout createFormLabel() {
        label.setContentMode(com.vaadin.shared.ui.ContentMode.HTML);
        label.setValue(raport());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(label);
        return horizontalLayout;
    }

    private String raport() {
        StringBuilder sb = new StringBuilder("");
        List<ExchangeForRaport> list = new ArrayList<>();//POMOC - nie wiem jak pobrac dane ???
        list.stream().sorted(Comparator.comparingDouble(s -> Double.parseDouble(s.getDifferenceBetweenDays().toString())));
        for (ExchangeForRaport e : list) {
            if (Double.parseDouble(e.getDifferenceBetweenDays().toString()) > 0) {
                sb.append(e.getSymbol() + " - " + e.getCurrency() + " kurs wzrósł o: " + e.getDifferenceBetweenDays() + "<br><br>");
            }
        }
        return sb.toString();
    }

    public Raport10() {
        configure();

        HorizontalLayout buttonsLayout = createFormButton();
        HorizontalLayout labelLayout = createFormLabel();

        addComponent(buttonsLayout);
        addComponent(labelLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println(Arrays.asList(event.getParameters().split("/")));
    }
}

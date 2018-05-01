package com.NBP;

import com.NBP.dao.ExchangeRateDao;
import com.NBP.dao.ExchangeRateImpl;
import com.NBP.model.ExchangeForRaport;
import com.NBP.model.ExchangeForRaport10Day;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Raport10 extends VerticalLayout implements View {

    private Button btnback = new Button("powrót");

    private final Label label = new Label();

    private List<String> listSymbol;

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

        List<ExchangeForRaport10Day> list = exchangeRateDao.raport10Days(listSymbol);

        list = list
                .stream()
                .sorted(Comparator.comparing(ExchangeForRaport10Day::getSymbol))
                .collect(Collectors.toList());

        for (ExchangeForRaport10Day e : list) {

            BigDecimal average = BigDecimal.valueOf(0);
            Double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (int i = 0; i < e.getExchanges().length; i++) {
                average = BigDecimal.valueOf(e.getExchanges()[i]).add(average);
                if (min > e.getExchanges()[i]) {
                    min = e.getExchanges()[i];
                }
                if (max < e.getExchanges()[i]) {
                    max = e.getExchanges()[i];
                }
            }
            average = average.divide(BigDecimal.valueOf(e.getExchanges().length));

            sb.append(e.getSymbol() + " - " + e.getCurrency() + "<br>ostatnie 10 notowań: " + Arrays.toString(e.getExchanges()) +
                    "<br>średnia wartość: " + average + "<br>najwyższe notowanie: " + max + "<br>najniższe notowanie: " + min + "<br><br>");
            }

        return sb.toString();
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        listSymbol = Arrays.asList(event.getParameters().split("/"));
        configure();

        HorizontalLayout buttonsLayout = createFormButton();
        HorizontalLayout labelLayout = createFormLabel();

        addComponent(buttonsLayout);
        addComponent(labelLayout);
    }
}

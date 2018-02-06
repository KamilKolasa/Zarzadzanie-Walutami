package com.NBP;

import com.NBP.dao.ExchangeRateDao;
import com.NBP.dao.ExchangeRateImpl;
import com.NBP.model.ExchangeForRaport;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.navigator.View;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Raport extends VerticalLayout implements View {

    private Button btnback = new Button("powrót");

    private final Label labelIncrease = new Label();
    private final Label labelDecrease = new Label();
    private final Label labelConstant = new Label();

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
        labelIncrease.setContentMode(com.vaadin.shared.ui.ContentMode.HTML);
        labelIncrease.setValue(raportIncrease());
        labelDecrease.setContentMode(com.vaadin.shared.ui.ContentMode.HTML);
        labelDecrease.setValue(raportDecrease());
        labelConstant.setContentMode(com.vaadin.shared.ui.ContentMode.HTML);
        labelConstant.setValue(raportConstant());

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(labelIncrease);
        horizontalLayout.addComponent(labelDecrease);
        horizontalLayout.addComponent(labelConstant);
        return horizontalLayout;
    }

    private String raportIncrease() {
        StringBuilder sb = new StringBuilder("");
        List<ExchangeForRaport> list = exchangeRateDao.raport();

        list = list
                .stream()
                .filter(e -> e.getDifferenceBetweenDays().compareTo(BigDecimal.ZERO) > 0)
                .sorted((s1,s2)->s2.getDifferenceBetweenDays().compareTo(s1.getDifferenceBetweenDays()))
                .collect(Collectors.toList());

        for (ExchangeForRaport e : list) {
                sb.append(e.getSymbol() + " - " + e.getCurrency() + " kurs wzrósł o: " + e.getDifferenceBetweenDays() + "<br><br>");
        }
        return sb.toString();
    }

    private String raportDecrease() {
        StringBuilder sb = new StringBuilder("");
        List<ExchangeForRaport> list = exchangeRateDao.raport();

        list = list
                .stream()
                .filter(e -> e.getDifferenceBetweenDays().compareTo(BigDecimal.ZERO) < 0)
                .sorted((s1,s2)->s2.getDifferenceBetweenDays().compareTo(s1.getDifferenceBetweenDays()))
                .collect(Collectors.toList());

        for (ExchangeForRaport e : list) {
            if (Double.parseDouble(e.getDifferenceBetweenDays().toString()) < 0) {
                sb.append(e.getSymbol() + " - " + e.getCurrency() + " kurs spadł o: " + e.getDifferenceBetweenDays() + "<br><br>");
            }
        }
        return sb.toString();
    }

    private String raportConstant() {
        StringBuilder sb = new StringBuilder("");
        List<ExchangeForRaport> list = exchangeRateDao.raport();

        list = list
                .stream()
                .filter(e -> e.getDifferenceBetweenDays().compareTo(BigDecimal.ZERO) == 0)
                .sorted((s1,s2)->s2.getDifferenceBetweenDays().compareTo(s1.getDifferenceBetweenDays()))
                .collect(Collectors.toList());

        for (ExchangeForRaport e : list) {
            if (Double.parseDouble(e.getDifferenceBetweenDays().toString()) == 0) {
                sb.append(e.getSymbol() + " - " + e.getCurrency() + " kurs bez zmian<br><br>");
            }
        }
        return sb.toString();
    }

    public Raport() {
        configure();

        HorizontalLayout buttonsLayout = createFormButton();
        HorizontalLayout labelLayout = createFormLabel();

        addComponent(buttonsLayout);
        addComponent(labelLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        System.out.println(event.getParameters());
    }
}

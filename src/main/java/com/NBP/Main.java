package com.NBP;

import com.NBP.dao.ExchangeRateDao;
import com.NBP.dao.ExchangeRateImpl;
import com.NBP.model.ExchangeForRaport;
import com.NBP.model.ExchangeForRaport10Day;
import com.NBP.model.ExchangeRate;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends VerticalLayout implements View {
    private final Button btnRefresh = new Button("Odświeżenie");

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateImpl();
    private final Grid<ExchangeRate> exchangeRateGrid = new Grid<>(ExchangeRate.class);

    private final TwinColSelect choseGroup = new TwinColSelect(null, exchangeRateDao.getAllSymbol());//null - tam moge dac tytul


    private final MenuBar.Command command = (MenuBar.Command) menuItem -> Notification.show(menuItem.getText(), Notification.Type.TRAY_NOTIFICATION);//wyswietla w rogu jaka opcje wybralismy
    private final MenuBar.Command commandSaveRaportToFile = (MenuBar.Command) menuItem -> {
        generateRaport();
    };
    private final MenuBar.Command commandDisplayRaport = (MenuBar.Command) menuItem -> {
        Navigator navigator = UI.getCurrent().getNavigator();
        navigator.navigateTo(Views.VIEW_raport.toString());
    };
    private final MenuBar.Command commandSaveRaport10ToFile = (MenuBar.Command) menuItem -> {
        generateRaport10();
    };
    private final MenuBar.Command commandDisplayRaport10 = (MenuBar.Command) menuItem -> {
        Navigator navigator = UI.getCurrent().getNavigator();

        Object[] oo = choseGroup.getSelectedItems().toArray();
        List<String> listSymbols = new ArrayList<>();
        for (Object e : oo) {
            listSymbols.add(e.toString());
        }

        String data = listSymbols.stream().collect(Collectors.joining("/"));
        navigator.navigateTo(Views.VIEW_raport_10.toString() + "/" + data);
    };

    private final MenuBar raport = new MenuBar();
    private final MenuBar.MenuItem createRaport = raport.addItem("Generuj raport", VaadinIcons.USER_CARD, null);
    private final MenuBar.MenuItem saveRaportToFile = createRaport.addItem("Zapis do pliku", VaadinIcons.DISC, commandSaveRaportToFile);
    private final MenuBar.MenuItem displayRaport = createRaport.addItem("Wyświetlenie raportu", VaadinIcons.FILE_TEXT, commandDisplayRaport);

    private final MenuBar raport10Days = new MenuBar();
    private final MenuBar.MenuItem createRaport10 = raport10Days.addItem("Generuj raport z 10 dni", VaadinIcons.USER_CARD, null);
    private final MenuBar.MenuItem saveRaport10ToFile = createRaport10.addItem("Zapis do pliku", VaadinIcons.DISC, commandSaveRaport10ToFile);
    private final MenuBar.MenuItem displayRaport10 = createRaport10.addItem("Wyświetlenie raportu", VaadinIcons.FILE_TEXT, commandDisplayRaport10);

    private final Label txTitle = new Label("Największy wzrost kursu dla walut:");
    private final Label txCurrency1 = new Label();
    private final Label txCurrency2 = new Label();
    private final Label txCurrency3 = new Label();

    private void configureActions() {
        btnRefresh.addClickListener(e -> {
            exchangeRateDao.updateAll();
            exchangeRateGrid.setItems(exchangeRateDao.getAll());
        });
    }

    private HorizontalLayout createFormButtons() {
        btnRefresh.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        btnRefresh.setIcon(VaadinIcons.REFRESH);

        HorizontalLayout menuBar = new HorizontalLayout(raport);

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(btnRefresh);
        layout.addComponent(menuBar);

        return layout;
    }

    private VerticalLayout createTable() {
        exchangeRateGrid.setCaption("Exchange Rate");
        //exchangeRateGrid.setColumnOrder("symbol", "exchangeToday", "exchangeYesterday");// 1 - sposob na wyswietlanie kolumn tabeli (zawsze wszystkie wyswietli chyba ze ponizsza linia) mozna tylko kolejnosc ustalic
        //exchangeRateGrid.getColumn("id").setHidden(true);//rozwiazanie by wszystkich nie wyswietlalo
        exchangeRateGrid.setColumns("symbol", "exchangeToday", "exchangeYesterday");// 2 - sposob wyswietlania kolumn tabeli
        exchangeRateGrid.setItems(exchangeRateDao.getAll());
        exchangeRateGrid.setSizeFull();

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addComponent(exchangeRateGrid);
        return verticalLayout;
    }

    private VerticalLayout searchBiggestIncrease() {
        List<ExchangeRate> list = exchangeRateDao.biggestIncreaseRates();
        txCurrency1.setValue(list.get(0).getSymbol() + " - " + list.get(0).getCurrency());
        txCurrency2.setValue(list.get(1).getSymbol() + " - " + list.get(1).getCurrency());
        txCurrency3.setValue(list.get(2).getSymbol() + " - " + list.get(2).getCurrency());

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(txTitle);
        layout.addComponent(txCurrency1);
        layout.addComponent(txCurrency2);
        layout.addComponent(txCurrency3);

        return layout;
    }

    private HorizontalLayout multiChoosingCurriencies() {
        choseGroup.setRows(8);
        choseGroup.setLeftColumnCaption("Dostępne waluty");
        choseGroup.setRightColumnCaption("Wybrane waluty");

        //choseGroup.setItemCaptionGenerator(item -> "waluta: " + item);//dodaje dodatkowy przypisek przy mojej liscie walut w tym przypadku "waluta: "
        choseGroup.addValueChangeListener(event -> Notification.show("Wybrana waluta: ",
                String.valueOf(event.getValue()),//wybrane wartosci
                Notification.Type.TRAY_NOTIFICATION));//wyswietla okienko co zostalo wybrane

        Layout menuBar = new VerticalLayout(raport10Days);

        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(choseGroup);
        layout.addComponent(menuBar);

        return layout;
    }

    public void generateRaport10() {
        try {
            Object[] oo = choseGroup.getSelectedItems().toArray();
            List<String> listSymbols = new ArrayList<>();
            for (Object e : oo) {
                listSymbols.add(e.toString());
            }

            FileWriter fileWriter = new FileWriter("C:/Users/Żniwi/Downloads/reportWith10Days.txt");//C\Users\Żniwi\Downloads
            PrintWriter writer = new PrintWriter(fileWriter);

            List<ExchangeForRaport10Day> listExchange = exchangeRateDao.raport10Days(listSymbols);
            for (ExchangeForRaport10Day e : listExchange) {

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

                writer.println(e.getSymbol() + " - " + e.getCurrency() + "\nostatnie 10 notowań: " + Arrays.toString(e.getExchanges()) +
                        "\nśrednia wartość: " + average + "\nnajwyższe notowanie: " + max + "\nnajniższe notowanie: " + min);
            }

            writer.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateRaport() {
        try {
            List<ExchangeForRaport> list = exchangeRateDao.raport();
            FileWriter fw = new FileWriter("C:/Users/Żniwi/Downloads/report.txt");
            PrintWriter pw = new PrintWriter(fw);

            for (ExchangeForRaport e : list) {
                if (Double.parseDouble(e.getDifferenceBetweenDays().toString()) > 0) {
                    pw.println(e.getSymbol() + " - " + e.getCurrency() + " wzrost o " + e.getDifferenceBetweenDays());
                } else if (Double.parseDouble(e.getDifferenceBetweenDays().toString()) < 0) {
                    pw.println(e.getSymbol() + " - " + e.getCurrency() + " spadek o " + e.getDifferenceBetweenDays());
                } else {
                    pw.println(e.getSymbol() + " - " + e.getCurrency() + " kurs bez zmian");
                }
            }

            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Main() {
        configureActions();

        HorizontalLayout legendLayoutPlusMultipleSelection = new HorizontalLayout();
        legendLayoutPlusMultipleSelection.addComponent(searchBiggestIncrease());
        legendLayoutPlusMultipleSelection.addComponent(multiChoosingCurriencies());

        addComponent(createFormButtons());
        addComponent(createTable());
        addComponent(legendLayoutPlusMultipleSelection);
    }
}

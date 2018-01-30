package com.NBP;

//        Napisz aplikację WEBOWĄ (osoby, które nie znają jeszcze aplikacji
//        webowych mogą wykonać standardową aplikację desktopową lub przy użyciu
//        Vaadin), która korzystając z zasobów umieszczonych na stronie
//        NBP pobranych przez Ciebie z wykorzystaniem formatu JSON zrealizuje
//        następujący problem:
//
//                *Aplikacja posiada tabelę, w której wyświetlane są kursy wszystkich
//        walut tabeli A z NBP. Należy wyświetlić kurs aktualny oraz kurs
//        z dnia poprzedniego.
//
//                *Wszystkie wiersze z kursami w przypadku których nastąpił spadek
//        wartości w stosunku do dnia poprzedniego, powinny zostać opatrzone
//        specjalnym kolorem.
//
//                *Pod tabelą powinny widnieć symbole oraz pełne nazwy trzech walut,
//        które zanotowały największy wzrost w stosunku do dnia poprzedniego.
//
//                *Aplikacja po naciśnięciu przycisku znajdującego się nad tabelą
//        powinna generować raport, w którym znajdują się symbole walut,
//        ich pełne nazwy oraz różnice kursów pomiędzy kursem aktualnym i z
//        dnia poprzedniego. Dane powinny być posortowane według kursów
//        malejąco. Raport wygeneruj albo do pliku tekstowego lub w postaci
//        nowej strony internetowej.
//
//                *Przygotuj formularz, w którym użytkownik będzie mógł zaznaczyć,
//        które waluty go interesują i na tej podstawie zostanie
//        mu przedstawiony kurs z ostatnich 10 notowań wybranych walut wraz
//        z ich średnią wartością oraz kursem największym i najmniejszym.
//
//                *Przygotuj raporty generowane dla użytkownika na podstawie zasad
//        opisanych w poprzednim punkcie i zapisuj je do specjalnie
//        przygotowanej tabeli w bazie danych z datą generowania raportu
//        (postać tabeli możesz zaimplementować dowolnie).

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI{

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Navigator navigator = new Navigator(this, this);
        navigator.addView(Views.VIEW_raport.toString(), Raport.class);
        navigator.addView(Views.VIEW_raport_10.toString(), Raport10.class);
        navigator.addView(Views.VIEW_main.toString(), Main.class);
        navigator.navigateTo(Views.VIEW_main.toString());
        System.out.println("INFO");
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}

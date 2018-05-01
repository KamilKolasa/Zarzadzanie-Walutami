package com.NBP.dao;

import com.NBP.model.ExchangeForRaport10Day;
import com.NBP.model.ExchangeRate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DownloadDataJSON {

    public static ExchangeForRaport10Day downloadCurrency(String symbol) {
        try {
            JSONParser parser = new JSONParser();
            URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/" + symbol + "/last/10/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));

            JSONObject jsonOb = (JSONObject) parser.parse(in.readLine());
            JSONArray jsonAr = (JSONArray) jsonOb.get("rates");

            String currency = jsonOb.get("currency").toString();

            Double[] rates = new Double[10];
            int i = 0;

            for (Object jsonCurrrency : jsonAr) {
                JSONObject currencyChosen = (JSONObject) jsonCurrrency;
                rates[i] = Double.parseDouble(currencyChosen.get("mid").toString());
                ++i;
            }

            return ExchangeForRaport10Day.builder().currency(currency).symbol(symbol).exchanges(rates).build();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ExchangeRate> getCurrencies() {
        int i = 0;
        List<ExchangeRate> day1 = downloadCurrencies(i);
        List<ExchangeRate> day2 = downloadCurrencies(i + 1);

        while (day1 == null || day2 == null) { //Sprawia ze null'uw nie ma w day1/2
            if (day1 == null) {
                day1 = downloadCurrencies(i);
                day2 = downloadCurrencies(i + 1);
            } else {
                day2 = downloadCurrencies(i + 2);
            }
            ++i;
        }

        day1.stream().sorted();
        day2.stream().sorted();

        List<ExchangeRate> list = new ArrayList<>();
        for (int j = 0; j < day1.size(); j++) {
            list.add(new ExchangeRate(0L, day1.get(j).getCurrency(), day1.get(j).getSymbol(), day1.get(j).getExchangeToday(), day2.get(j).getExchangeToday()));
        }
        return list;
    }

    private static List<ExchangeRate> downloadCurrencies(int day) {//0 - to dzień dziejszy
        try {
            LocalDateTime nowTime = LocalDateTime.now();
            LocalDateTime localDate = nowTime.minusDays(day);
            String data = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

            JSONParser parser = new JSONParser();
            URL url = new URL("http://api.nbp.pl/api/exchangerates/tables/a/" + data + "/");
            URLConnection urlConnection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            List<ExchangeRate> listCurrency = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) {

                JSONArray jsonArray = (JSONArray) parser.parse(line);

                for (Object ob1 : jsonArray) {

                    JSONObject jsonOb = (JSONObject) ob1;
                    JSONArray jsonAr = (JSONArray) jsonOb.get("rates");

                    for (Object currency : jsonAr) {

                        JSONObject currencyChosen = (JSONObject) currency;
                        listCurrency.add(ExchangeRate.builder().currency(currencyChosen.get("currency").toString()).symbol(currencyChosen.get("code").toString()).exchangeToday(Double.parseDouble(currencyChosen.get("mid").toString())).build());
                    }
                }
            }
            return listCurrency;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.NBP.dao;

import com.NBP.database.DbConnection;
import com.NBP.model.ExchangeForRaport;
import com.NBP.model.ExchangeForRaport10Day;
import com.NBP.model.ExchangeRate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExchangeRateImpl implements ExchangeRateDao, Serializable {

    private static Connection connection = DbConnection.getInstance();

    @Override
    public void updateAll() {//trzeba to jeszce sprwadzic bo to na szybko bez jakich kolwiek testow zrobione !!! !!! !!! !!! !!! !!! !!! !!!
        try {
            List<ExchangeRate> oldData = getAll();
            List<ExchangeRate> newData = getCurrencies();

            List<String> repeat = new ArrayList<>();
            for (ExchangeRate er : oldData) {
                String oldSymbol = er.getSymbol();
                for (int i = 0; i < newData.size(); i++) {
                    if (oldSymbol.equals(newData.get(i).getSymbol())) {
                        repeat.add(oldSymbol);
                        //---------------------------------
                        String sql = "UPDATE Exchange_rate SET exchange_today = ?, exchange_yesterday = ? WHERE symbol = ?";
                        PreparedStatement prep = connection.prepareStatement(sql);
                        prep.setDouble(1, newData.get(i).getExchangeToday());
                        prep.setDouble(2, newData.get(i).getExchangeYesterday());
                        prep.setString(3, oldSymbol);
                        prep.execute();
                        //---------------------------------
                    } else if (i == (newData.size() - 1)) {
                        //---------------------------------
                        String sql = "DELETE FROM Person WHERE symbol = ?";
                        PreparedStatement prep = connection.prepareStatement(sql);
                        prep.setString(1, oldData.get(i).getSymbol());
                        prep.execute();
                        //---------------------------------
                    }
                }
            }
            String regex = repeat.toString();
            for (ExchangeRate er : newData) {
                String newSymbol = er.getSymbol();
                if (!newSymbol.matches(regex)) {
                    for (int i = 0; i < oldData.size(); i++) {
                        if (newSymbol.equals(oldData.get(i).getSymbol())) {
                            //---------------------------------
                            String sql = "UPDATE Exchange_rate SET exchange_today = ?, exchange_yesterday = ? WHERE symbol = ?";
                            PreparedStatement prep = connection.prepareStatement(sql);
                            prep.setDouble(1, newData.get(i).getExchangeToday());
                            prep.setDouble(2, newData.get(i).getExchangeYesterday());
                            prep.setString(3, newSymbol);
                            prep.execute();
                            //---------------------------------
                        } else if (i == (oldData.size() - 1)) {
                            //---------------------------------
                            String sql = "INSERT INTO Exchange_rate (currency, symbol, exchange_today, exchange_yesterday) VALUES (?,?,?,?)";
                            PreparedStatement prep = connection.prepareStatement(sql);
                            prep.setString(1, newData.get(i).getCurrency());
                            prep.setString(2, newData.get(i).getSymbol());
                            prep.setDouble(3, newData.get(i).getExchangeToday());
                            prep.setDouble(4, newData.get(i).getExchangeYesterday());
                            prep.execute();
                            //---------------------------------
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> listExchangeRates = null;
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Exchange_rate";
                ResultSet rs = connection.createStatement().executeQuery(sql);

                listExchangeRates = new ArrayList<>();
                while (rs.next()) {
                    listExchangeRates.add(new ExchangeRate(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getDouble(4),
                            rs.getDouble(5)
                    ));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listExchangeRates;
    }

    @Override
    public List<String> getAllSymbol() {
        List<String> listSymbol = null;
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Exchange_rate";
                ResultSet rs = connection.createStatement().executeQuery(sql);

                listSymbol = new ArrayList<>();
                while (rs.next()) {
                    listSymbol.add(
                            rs.getString(3)
                    );
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listSymbol;
    }

    @Override
    public Optional<ExchangeRate> getBySymbol(String symbol) {
        Optional<ExchangeRate> exchangeRateOptional = null;
        if (connection != null) {
            try {
                String sql = "SELECT * FROM Exchange_rate WHERE symbol = ?";
                PreparedStatement prep = connection.prepareStatement(sql);
                prep.setString(1, symbol);

                ResultSet rs = prep.executeQuery();
                if (rs.next()) {
                    exchangeRateOptional = Optional.ofNullable(new ExchangeRate(
                            rs.getLong(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getDouble(4),
                            rs.getDouble(5)
                    ));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exchangeRateOptional;
    }

    @Override
    public List<ExchangeRate> biggestIncreaseRates() {
        List<ExchangeRate> database = getAll();
        ExchangeRate er1 = null;
        ExchangeRate er2 = null;
        ExchangeRate er3 = null;
        List<ExchangeRate> list = new ArrayList<>();
        ExchangeRate substitute = ExchangeRate.builder().symbol("Brak waluty").currency("Nie ma waluty której wzróśł kurs").build();
        Double maxIncrease = Double.MIN_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < database.size(); j++) {
                switch (i) {
                    case 0:

                        Double today = database.get(j).getExchangeToday();
                        Double yesterday = database.get(j).getExchangeYesterday();

                        if ((today - yesterday) > 0 && (today - yesterday) > maxIncrease) {
                            maxIncrease = today - yesterday;
                            er1 = database.get(j);
                        }

                        if (er1 != null) {
                            list.add(0, er1);
                        } else {
                            list.add(0, substitute);
                        }

                        break;
                    case 1:

                        today = database.get(j).getExchangeToday();
                        yesterday = database.get(j).getExchangeYesterday();

                        if ((today - yesterday) > 0 && (today - yesterday) > maxIncrease && !er1.equals(database.get(j))) {
                            maxIncrease = today - yesterday;
                            er2 = database.get(j);
                        }

                        if (er2 != null) {
                            list.add(1, er2);
                        } else {
                            list.add(1, substitute);
                        }

                        break;
                    case 2:

                        today = database.get(j).getExchangeToday();
                        yesterday = database.get(j).getExchangeYesterday();

                        if ((today - yesterday) > 0 && (today - yesterday) > maxIncrease && !er1.equals(database.get(j)) && !er2.equals(database.get(j))) {
                            maxIncrease = today - yesterday;
                            er3 = database.get(j);
                        }

                        if (er3 != null) {
                            list.add(2, er3);
                        } else {
                            list.add(2, substitute);
                        }

                        break;
                }
            }
            maxIncrease = Double.MIN_VALUE;
        }
        return list;
    }

    @Override
    public List<ExchangeForRaport> raport() {
        List<ExchangeRate> list = getAll();
        List<ExchangeForRaport> raport = new ArrayList<>();
        for (ExchangeRate e : list) {
            BigDecimal bd = BigDecimal.valueOf(e.getExchangeToday()).subtract(BigDecimal.valueOf(e.getExchangeYesterday()));
            raport.add(new ExchangeForRaport(e.getCurrency(), e.getSymbol(), bd));
        }
        return raport;
    }

    @Override
    public List<ExchangeForRaport10Day> raport10Days(List<String> symbols) {
        if (symbols == null) {
            throw new NullPointerException("LIST SYMBOLS");
        }
        List<ExchangeForRaport10Day> list = new ArrayList<>();
        for (String s : symbols) {
            list.add(downloadCurrency(s));
        }

        return list;
    }


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

    private static List<ExchangeRate> getCurrencies() {
        int i = 0;
        List<ExchangeRate> day1 = downloadCurrencies(i);
        List<ExchangeRate> day2 = downloadCurrencies(i + 1);

        while (day1 == null || day2 == null) { //Sprawia ze null'uw nie ma w day1/2
            if (day1 == null) {
                ++i;
                day1 = downloadCurrencies(i);
                day2 = downloadCurrencies(i + 1);
            } else {
                day2 = downloadCurrencies(++i + 1);
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

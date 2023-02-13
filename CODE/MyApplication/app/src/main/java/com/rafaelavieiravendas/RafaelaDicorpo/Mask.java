package com.rafaelavieiravendas.RafaelaDicorpo;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class Mask implements TextWatcher {
    private final TextView editTextWeakReference;
    private final Locale locale = Locale.getDefault();

    public Mask(TextView editText) {
        this.editTextWeakReference = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String editText = editTextWeakReference.getText().toString();
        if (editText == null) return;
        editTextWeakReference.removeTextChangedListener(this);

        BigDecimal parsed = parseToBigDecimal(s.toString());
        String formatted = NumberFormat.getCurrencyInstance(locale).format(parsed);
        //Remove o símbolo da moeda e espaçamento pra evitar bug
        String replaceable = String.format("[%s\\s]", getCurrencySymbol());
        String cleanString = formatted.replaceAll(replaceable, "");
        cleanString = cleanString;
        editTextWeakReference.setText(cleanString);
       // editTextWeakReference.set(cleanString.length());
        editTextWeakReference.addTextChangedListener(this);
    }

    private BigDecimal parseToBigDecimal(String value) {
        String replaceable = String.format("[%s,.\\s]", getCurrencySymbol());

        String cleanString = value.replaceAll(replaceable, "");

        try {
            return new BigDecimal(cleanString).setScale(
                    2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } catch (NumberFormatException e) {
            //ao apagar todos valores de uma só vez dava erro
            //Com a exception o valor retornado é 0.00
            return new BigDecimal(0);

        }
    }

    public static String formatPrice(String price) {
        //Ex - price = 2222
        //retorno = 2222.00
        DecimalFormat df = new DecimalFormat("0.00");
        return String.valueOf(df.format(Double.valueOf(price)));

    }

    public static String formatTextPrice(String price) {
        //Ex - price = 3333.30
        //retorna formato monetário em Br = 3.333,30
        //retorna formato monetário EUA: 3,333.30
        //retornar formato monetário de alguns países europeu: 3 333,30
        BigDecimal bD = new BigDecimal(formatPriceSave(formatPrice(price)));
        String newFormat = String.valueOf(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(bD));
        String replaceable = String.format("[%s]", getCurrencySymbol());
        return newFormat.replaceAll(replaceable, "");

    }

    static String formatPriceSave(String price) {
        //Ex - price = $ 5555555
        //return = 55555.55 para salvar no banco de dados
        String replaceable = String.format("[%s,.\\s]", getCurrencySymbol());
        String cleanString = price.replaceAll(replaceable, "");
        StringBuilder stringBuilder = new StringBuilder(cleanString.replaceAll(" ", ""));

        return String.valueOf(stringBuilder.insert(cleanString.length() - 2, '.'));

    }

    public static String getCurrencySymbol() {
        return NumberFormat.getCurrencyInstance(Locale.getDefault()).getCurrency().getSymbol();

    }
}
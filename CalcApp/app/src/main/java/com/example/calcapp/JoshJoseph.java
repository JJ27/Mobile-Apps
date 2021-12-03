package com.example.calcapp;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class JoshJoseph extends AppCompatActivity implements View.OnClickListener {
    EditText text;
    ArrayList<View> numbers;
    boolean clearing;
    boolean initial;
    boolean deg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial = true;
        clearing = true;
        deg = true;

        text = (EditText) findViewById(R.id.text);
        text.setTextIsSelectable(true);
        text.setRawInputType(InputType.TYPE_NULL);
        text.setFocusable(true);
        text.setSelection(text.getText().length());
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text.setSelection(text.getText().length());
                if(text.getText().length() == 0)
                    return;
                if(clearing && text.getText().charAt(0) == '0' && text.getText().length() > 1)
                    text.setText(text.getText().toString().substring(1));
                if(initial){
                    initial = false;
                    text.setText(text.getText().toString().substring(0,1));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                text.setSelection(text.getText().length());
            }
        });
        text.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (keyEvent == null || keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    try {
                        clearing = true;
                        if(calcwithParenthesis(text.getText().toString()).length() > 15)
                            text.setText(calcwithParenthesis(text.getText().toString()).substring(0,15));
                        else
                            text.setText(calcwithParenthesis(text.getText().toString()));
                    } catch (NumberFormatException e) {
                        text.setText("Error");
                    } catch (ArithmeticException e) {
                        text.setText("Error");
                    } catch (IndexOutOfBoundsException e) {
                        text.setText("Error");
                    } catch (Exception e){
                        text.setText("Error");
                    } finally{
                        text.setSelection(text.getText().length());
                    }
                }
                return true;
            }
        });
        numbers = ((TableLayout) findViewById(R.id.table)).getTouchables();
        for(int i = 0; i < numbers.size(); i++){
            numbers.get(i).setOnClickListener(this);
            calculate("rectangle");
            ((Button)numbers.get(i)).setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onClick(View view){
        if(text.getText().toString().contains("Error"))
            text.setText("0");
        switch(view.getId()) {
            case R.id.mode:
                deg = !deg;
                ((Button)findViewById(R.id.mode)).setText((((Button)findViewById(R.id.mode)).getText().equals("DEG")) ? "RAD" : "DEG");
                break;
            case R.id.sqrt:
                if (text.getText().length() > 0 && text.getText().charAt(0) == '0')
                    text.setText(((Button) view).getText() + "");
                else
                    text.setText(text.getText() + "" + ((Button) view).getText());
                break;
            case R.id.clear:
                clearing = false;
                text.setText("0");
                clearing = true;
                break;
            case R.id.equals:
                try {
                    clearing = true;
                    text.setText(calcwithParenthesis(text.getText().toString()));
                } catch(NumberFormatException e){
                    text.setText("Error");
                } catch(ArithmeticException e){
                    text.setText("Error");
                } catch(IndexOutOfBoundsException e) {
                    text.setText("Error");
                } finally {
                    text.setSelection(text.getText().length());
                }
                break;
            default:
                text.setText(text.getText() + "" + ((Button) view).getText());
        }
    }
    public String calculate(String exp){
        if(exp.equalsIgnoreCase("oval")){
            for(int i = 0; i < numbers.size(); i++){
                ((Button)numbers.get(i)).setBackground(getDrawable(R.drawable.roundbutton));
            }
            return "";
        }
        if(exp.equalsIgnoreCase("rectangle")){
            for(int i = 0; i < numbers.size(); i++){
                ((Button)numbers.get(i)).setBackground(getDrawable(R.drawable.rectbutton));
            }
            return "";
        }
        switch(exp.toLowerCase()){
            case "red":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.RED);
                }
                return "";
            case "blue":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.BLUE);
                }
                return "";
            case "cyan":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.CYAN);
                }
                return "";
            case "black":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.BLACK);
                }
                return "";
            case "gray":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.GRAY);
                }
                return "";
            case "green":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.GREEN);
                }
                return "";
            case "magenta":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.MAGENTA);
                }
                return "";
            case "white":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.WHITE);
                }
                return "";
            case "orange":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.parseColor("#FFA500"));
                }
                return "";
            case "yellow":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.YELLOW);
                }
                return "";
            case "transparent":
                for(int i = 0; i < numbers.size(); i++){
                    ((Button)numbers.get(i)).setTextColor(Color.TRANSPARENT);
                }
                return "";
            case "default":
                return calculate("white");
        }
        exp = exp.replaceAll("e", Math.E + "");
        exp = exp.replaceAll("pi", Math.PI + "");
        exp = exp.replaceAll("sin", "®");
        exp = exp.replaceAll("cos", "†");
        exp = exp.replaceAll("tan", "¥");

        if(exp.contains(".."))
            throw new NumberFormatException("Double Decimal");
        StringTokenizer calc = new StringTokenizer(exp, "+-*/√^®†¥", true);
        ArrayList<String> operations = new ArrayList<String>();
        while(calc.hasMoreTokens()) {
            operations.add(calc.nextToken());
        }
        if(operations.size() > 2) {
            if (operations.get(0).equals("-") && !operations.get(2).equals("^"))
                operations.set(0, "-" + operations.remove(1));
        } else {
            if (operations.get(0).equals("-")) {
                operations.set(0, "-" + operations.remove(1));
            }
        }

        if(operations.size() == 1)
            return operations.get(0);

        for(int i = 0; i < operations.size(); i++){
            if(operations.get(i).equals("®")) {
                Double d1 = 0.0;
                if(deg)
                    d1 = Math.toRadians(Double.parseDouble(operations.remove(i + 1)));
                else
                    d1 = Double.parseDouble(operations.remove(i + 1));

                if(Math.abs(Math.sin(d1)) < 0.000000001) {
                    operations.set(i, "0.0");
                } else if(Math.abs(Math.sin(d1) - 0.5) < 0.000000001){
                    operations.set(i, "0.5");
                } else if(Math.abs(Math.sin(d1) - 1) < 0.000000001){
                    operations.set(i, "1.0");
                } else
                    operations.set(i, "" + Math.sin(d1));
                i--;
            }
            if(i < 0)
                continue;
            if(operations.get(i).equals("†")) {
                Double d1 = 0.0;
                if(deg)
                    d1 = Math.toRadians(Double.parseDouble(operations.remove(i + 1)));
                else
                    d1 = Double.parseDouble(operations.remove(i + 1));
                if(Math.abs(Math.cos(d1)) < 0.000000001) {
                    operations.set(i, "0.0");
                } else if(Math.abs(Math.cos(d1) - 0.5) < 0.000000001){
                    operations.set(i, "0.5");
                } else if(Math.abs(Math.cos(d1) - 1) < 0.000000001){
                    operations.set(i, "1.0");
                } else
                    operations.set(i, "" + Math.cos(d1));
                i--;
            }
            if(i < 0)
                continue;
            if(operations.get(i).equals("¥")) {
                Double d1 = 0.0;
                if(deg)
                    d1 = Math.toRadians(Double.parseDouble(operations.remove(i + 1)));
                else
                    d1 = Double.parseDouble(operations.remove(i + 1));
                if(Math.abs(Math.cos(d1)) < 0.000000001)
                    throw new NumberFormatException("undefined tan");
                if(Math.abs(Math.tan(d1)) < 0.000000001) {
                    operations.set(i, "0.0");
                } else if(Math.abs(Math.tan(d1) - 0.5) < 0.000000001){
                    operations.set(i, "0.5");
                } else if(Math.abs(Math.tan(d1) - 1) < 0.000000001){
                    operations.set(i, "1.0");
                } else
                    operations.set(i, "" + Math.tan(d1));
                i--;
            }
        }

        for(int i = 0; i < operations.size(); i++){
            if(operations.get(i).equals(getResources().getString(R.string.sqrt))) {
                Double d1 = 0.0;
                if(i == operations.size()-1) {
                    d1 = Double.parseDouble(operations.remove(i - 1));
                    operations.set(i-1, "" + Math.sqrt(d1));
                } else {
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    operations.set(i, "" + Math.sqrt(d1));
                }
                i--;
            }
            if(operations.size() == 1)
                break;
            if(i < 0)
                continue;
            if(operations.get(i).equals("^")) {
                Double d1;
                if(operations.get(i+1).equals("-")){
                    d1 = Double.parseDouble(operations.remove(i+2));
                    operations.remove(i+1);
                    d1 *= -1;
                } else{
                    d1 = Double.parseDouble(operations.remove(i + 1));
                }
                Double d2 = Double.parseDouble(operations.get(i-1));
                operations.set(i - 1, "" + (Math.pow(d2,d1)));
                operations.remove(i);
                i--;
            }
        }
        for(int i = 0; i < operations.size(); i++){
            if(operations.get(i).equals("*")) {
                Double d1 = 0.0;
                Double d2 = 0.0;
                if(operations.get(i+1).equals("-")){
                    operations.remove(i+1);
                    d1 = Double.parseDouble(operations.remove(i + 1)) * -1;
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 * d1));
                } else {
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 * d1));
                }
                operations.remove(i);
                i--;
            }
            if(operations.size() == 1)
                break;
            if(operations.get(i).equals("/")) {
                Double d1 = 0.0;
                Double d2 = 0.0;
                if(operations.get(i+1).equals("-")){
                    operations.remove(i+1);
                    d1 = Double.parseDouble(operations.remove(i + 1)) * -1;
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 / d1));
                } else {
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 / d1));
                }
                if(d1 == 0)
                    throw new NumberFormatException("Div by Zero");
                operations.remove(i);
                i--;
            }
        }
        addloop:
        for(int i = 0; i < operations.size(); i++){
            if(operations.get(i).equals("+")) {
                Double d1 = 0.0;
                Double d2 = 0.0;
                if(operations.get(i+1).equals("-")){
                    operations.remove(i+1);
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 - d1));
                } else {
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 + d1));
                }
                operations.remove(i);
                i--;
            }
            if(operations.size() == 1)
                break addloop;
            if(operations.get(i).equals("-") && i != 0) {
                Double d1 = 0.0;
                Double d2 = 0.0;
                if(operations.get(i+1).equals("-")){
                    operations.remove(i+1);
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 + d1));
                } else {
                    d1 = Double.parseDouble(operations.remove(i + 1));
                    d2 = Double.parseDouble(operations.get(i - 1));
                    operations.set(i - 1, "" + (d2 - d1));
                }
                operations.remove(i);
                i--;
            }
        }
        operations.set(0, operations.get(0).replaceFirst("^0+(?!$)", ""));
        if(operations.size() == 1) {
            if (Math.abs(Double.parseDouble(operations.get(0))) < 0.000000001) {
                operations.set(0, "0.0");
            } else if ((Math.abs(Double.parseDouble(operations.get(0)) - 0.5)) < 0.000000001) {
                operations.set(0, "0.5");
            } else if ((Math.abs(Double.parseDouble(operations.get(0)) - 1)) < 0.000000001) {
                operations.set(0, "1.0");
            }
        }else {
            if (Math.abs(Double.parseDouble(operations.get(1))) < 0.000000001) {
                operations.set(1, "0.0");
            } else if ((Math.abs(Double.parseDouble(operations.get(1)) - 0.5)) < 0.000000001) {
                operations.set(1, "0.5");
            } else if ((Math.abs(Double.parseDouble(operations.get(1)) - 1)) < 0.000000001) {
                operations.set(1, "1.0");
            }
        }
        try{
            return operations.get(0) + operations.get(1);
        } catch(IndexOutOfBoundsException e) {
            return operations.get(0);
        }
    }
    public String calcwithParenthesis(String exp){
        exp = exp.replaceAll(" ", "");
        switch(exp){
            case "*":
            case "/":
            case "+":
            case "-":
                throw new NumberFormatException("Invalid");
        }
        if((exp.split("\\(", -1).length-1) != (exp.split("\\)", -1).length-1))
            throw new NumberFormatException("Parenthesis Error");
        StringTokenizer calc = new StringTokenizer(exp, "()", true);
        ArrayList<String> operations = new ArrayList<String>();
        while(calc.hasMoreTokens()) {
            operations.add(calc.nextToken());
        }
        while(operations.contains("(")) {
            int i = operations.lastIndexOf("(");

            if(operations.get(i+2).equals(")"))
                operations.remove(i + 2);
            try{
                if(operations.size() > (i+2))
                    if(operations.get(i+2).substring(0,1).equals("^") && operations.get(i+1).substring(0,1).equals("-") && (Double.parseDouble(operations.get(i+2).substring(1)) % 2 == 0))
                        operations.set(i+1, ""+ (Double.parseDouble(operations.get(i+1)) * -1));
                operations.set(i, calculate(operations.get(i + 1)));
            } catch (IndexOutOfBoundsException e){
                if(operations.size() > (i+2))
                    if(operations.get(i+2).substring(0,1).equals("^") && operations.get(i+1).substring(0,1).equals("-") && (Double.parseDouble(operations.get(i+3).substring(1)) % 2 == 0.0)) {
                        operations.set(i + 1, "" + (Double.parseDouble(operations.get(i + 1)) * -1));
                    }
                operations.set(i+1, operations.get(i+1) + operations.remove(i+2));
                operations.set(i, calculate(operations.get(i + 1)));
            }
            operations.remove(i+1);
            exp = "";
            for(String c: operations){
                exp += c;
            }
            operations.clear();
            calc = new StringTokenizer(exp, "()", true);
            while(calc.hasMoreTokens()) {
                operations.add(calc.nextToken());
            }
        }
        while(operations.get(operations.size() - 1).equals(")"))
            operations.remove(operations.size() - 1);
        String re = "";
        for(String s: operations) {
            re += s;
        }
        return calculate(re);
    }
}
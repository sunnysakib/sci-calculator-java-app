package com.example.scicalculator_assign2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    String output,number,operator,newNumber;
    boolean isAns = false;
    TextView  inputText, resultText;
    Button btnMemory, btnClear, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnAdd, btnMinus, btnMulti, btnDivide, btnOpenBracket, btnCloseBracket, btnAC, btnDot, btnEqual,bsin,bcos, btan,blog, bln,bfact,bsqrt,binv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        resultText = findViewById(R.id.resultText);
        btnEqual = findViewById(R.id.btnEqual);
        bfact = findViewById(R.id.bfact);
        bsqrt = findViewById(R.id.bsqrt);
        btnMemory = findViewById(R.id.memory);

        inputText.setMovementMethod(new ScrollingMovementMethod());
        bfact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(inputText.getText().toString());
                int fact = factorial(val);
                inputText.setText(String.valueOf(fact));
                resultText.setText(val+"!");
            }
        });

        bsqrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = inputText.getText().toString();
                double r = Math.sqrt(Double.parseDouble(val));
                inputText.setText(String.valueOf(r));
            }
        });

        btnMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                Set<String> previousExpressions = sharedPreferences.getStringSet("expressions", new HashSet<String>());
                String[] expressionsArray = previousExpressions.toArray(new String[0]);
                Collections.reverse(Arrays.asList(expressionsArray));
                StringBuilder expressionsBuilder = new StringBuilder();

                // iterate over the expressions and add them to the StringBuilder
                for (String expression : expressionsArray) {
                    expressionsBuilder.append(expression);
                    expressionsBuilder.append("\n"); // add a new line after each expression
                }
                // set the text of the TextView field to the expressions string
                String expressionsString = expressionsBuilder.toString();
                inputText.setText(expressionsString);
            }
        });
    }
    public void numberEvent(View view) {
        number = inputText.getText().toString();
        switch (view.getId()){
            case R.id.btn0:
                number += "0";
                break;
            case R.id.btn1:
                number += "1";
                break;
            case R.id.btn2:
                number += "2";
                break;
            case R.id.btn3:
                number += "3";
                break;
            case R.id.btn4:
                number += "4";
                break;
            case R.id.btn5:
                number += "5";
                break;
            case R.id.btn6:
                number += "6";
                break;
            case R.id.btn7:
                number += "7";
                break;
            case R.id.btn8:
                number += "8";
                break;
            case R.id.btn9:
                number += "9";
                break;
            case R.id.btnDot:
                number += ".";
                break;
            case R.id.btnOpenBracket:
                number += "(";
                break;
            case R.id.btnCloseBracket:
                number += ")";
                break;
            case R.id.btnAdd:
                number += "+";
                operator = "+";
                break;
            case R.id.btnMinus:
                number += "-";
                operator = "-";
                break;
            case R.id.btnMulti:
                number += "X";
                operator = "X";
                break;
            case R.id.btnDivide:
                number += "/";
                operator = "/";
                break;
            case R.id.bsin:
                number += "sin";
                break;
            case R.id.bcos:
                number += "cos";
                break;
            case R.id.btan:
                number += "tan";
                break;
            case R.id.bln:
                number += "ln";
                break;
            case R.id.binv:
                number += "^"+"(-1)";
                break;
        }
        inputText.setText(number);

    }
    public void equalEvent(View view) {

        if(inputText.length() == 0){
            Toast.makeText(getApplicationContext(), "Not Valid", Toast.LENGTH_SHORT).show();
        }else{
            String val = inputText.getText().toString();
            String replacedstr = val.replace('/','/').replace('X','*');
            double result = eval(replacedstr);
            inputText.setText(String.valueOf(result));
            resultText.setText(val);

            // save into sharedPref
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            Set<String> previousExpressions = sharedPreferences.getStringSet("expressions", new HashSet<String>());
            previousExpressions.add(String.valueOf(result));
            if (previousExpressions.size() > 10) {
                Iterator<String> iterator = previousExpressions.iterator();
                iterator.next();
                iterator.remove();
            }
            // convert the set to a string and store it in the SharedPreferences file
            String expressionsString = TextUtils.join(",", previousExpressions);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("expressions", previousExpressions);
            editor.apply();

            }
        }
    int factorial(int n)
    {
        return (n==1 || n==0) ? 1 : n*factorial(n-1);
    }
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else if (func.equals("log")) x = Math.log10(x);
                    else if (func.equals("ln")) x = Math.log(x);
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
    public void deleteEvent(View view) {
        switch (view.getId()){
            case R.id.btnAC:
                inputText.setText("");
                resultText.setText("0");
                break;
            case R.id.btnClear:
                inputText.setText("");
                break;
        }
    }
}
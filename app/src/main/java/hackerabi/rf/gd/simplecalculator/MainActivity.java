package hackerabi.rf.gd.simplecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView solutionTextView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solutionTextView = findViewById(R.id.solution_tv);
        resultTextView = findViewById(R.id.result_tv);

        // About Button setup
        Button aboutButton = findViewById(R.id.button_about);
        aboutButton.setOnClickListener(v -> {
            // Open AboutActivity when the button is clicked
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // Number buttons
        setNumberButtonClickListener(R.id.button_0);
        setNumberButtonClickListener(R.id.button_1);
        setNumberButtonClickListener(R.id.button_2);
        setNumberButtonClickListener(R.id.button_3);
        setNumberButtonClickListener(R.id.button_4);
        setNumberButtonClickListener(R.id.button_5);
        setNumberButtonClickListener(R.id.button_6);
        setNumberButtonClickListener(R.id.button_7);
        setNumberButtonClickListener(R.id.button_8);
        setNumberButtonClickListener(R.id.button_9);
        setDotButtonClickListener(R.id.button_dot);

        // Operation buttons
        setOperationButtonClickListener(R.id.button_plus, "+");
        setOperationButtonClickListener(R.id.button_minus, "-");
        setOperationButtonClickListener(R.id.button_multiply, "*");
        setOperationButtonClickListener(R.id.button_divide, "/");

        // Other buttons
        setFunctionButtonClickListener(R.id.button_c, false); // Backspace
        setFunctionButtonClickListener(R.id.button_ac, true); // All Clear
        setFunctionButtonClickListener(R.id.button_open_bracket, false); // Open Bracket
        setFunctionButtonClickListener(R.id.button_close_bracket, false); // Close Bracket

        // Equals button
        setEqualButtonClickListener(R.id.button_equals); // Assuming your equals button has this ID
    }

    private void setNumberButtonClickListener(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            String number = ((Button) v).getText().toString();
            solutionTextView.append(number);
            updateResult();  // Update the result automatically
        });
    }

    private void setDotButtonClickListener(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            String currentText = solutionTextView.getText().toString();
            if (!currentText.endsWith(".") && !currentText.isEmpty()) {
                solutionTextView.append(".");
                updateResult();  // Update the result automatically
            }
        });
    }

    private void setOperationButtonClickListener(int buttonId, String operator) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            String currentText = solutionTextView.getText().toString();
            if (!currentText.isEmpty()) {
                char lastChar = currentText.charAt(currentText.length() - 1);
                if (isOperator(lastChar)) {
                    // Replace the last operator with the new one
                    solutionTextView.setText(currentText.substring(0, currentText.length() - 1) + operator);
                } else {
                    // Append the operator
                    solutionTextView.append(operator);
                }
            }
        });
    }

    private void setFunctionButtonClickListener(int buttonId, boolean isClear) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            if (isClear) {
                // Clear all
                solutionTextView.setText("");
                resultTextView.setText("0");
            } else {
                String currentText = solutionTextView.getText().toString();
                if (buttonId == R.id.button_c) {
                    // Backspace functionality
                    if (!currentText.isEmpty()) {
                        solutionTextView.setText(currentText.substring(0, currentText.length() - 1));
                        updateResult();  // Update the result automatically
                    }
                } else if (buttonId == R.id.button_open_bracket) {
                    solutionTextView.append("(");
                } else if (buttonId == R.id.button_close_bracket) {
                    solutionTextView.append(")");
                }
            }
        });
    }

    // Method to handle the equals button (=)
    private void setEqualButtonClickListener(int buttonId) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            String expression = solutionTextView.getText().toString();
            if (!expression.isEmpty() && isValidExpression(expression)) {
                try {
                    String result = evaluateExpression(expression);
                    solutionTextView.setText(result);  // Set result at the top
                    resultTextView.setText("");        // Clear the result at the bottom
                } catch (Exception e) {
                    resultTextView.setText("Error");
                }
            }
        });
    }

    // Method to evaluate and display the result
    private void updateResult() {
        String expression = solutionTextView.getText().toString();
        if (!expression.isEmpty() && isValidExpression(expression)) {
            try {
                String result = evaluateExpression(expression);
                resultTextView.setText(result);
            } catch (Exception e) {
                resultTextView.setText("Error");
            }
        } else {
            resultTextView.setText(""); // Show empty or maintain the previous result
        }
    }

    // Check if the expression is valid (no trailing operators and balanced brackets)
    private boolean isValidExpression(String expression) {
        // Check if the last character is not an operator or an open bracket
        char lastChar = expression.charAt(expression.length() - 1);
        if (isOperator(lastChar) || lastChar == '(') {
            return false;
        }

        // Check for balanced brackets
        int openBrackets = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') openBrackets++;
            if (c == ')') openBrackets--;
            // If at any point brackets are unbalanced, return false
            if (openBrackets < 0) return false;
        }
        return openBrackets == 0;
    }

    // Check if a character is an operator
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private String evaluateExpression(String expression) {
        try {
            // Use exp4j to evaluate the expression
            Expression exp = new ExpressionBuilder(expression).build();
            double result = exp.evaluate();

            // Format the result to remove trailing ".0" if it's a whole number
            if (result == (long) result) {
                return String.valueOf((long) result);
            } else {
                return String.valueOf(result);
            }
        } catch (Exception e) {
            return "Error";
        }
    }
}

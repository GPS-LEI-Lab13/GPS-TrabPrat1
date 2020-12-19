package sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public boolean checkUserPasswordRules(String password){
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,25}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
}

package Network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternsAndFonts {

    String[] useSplitterPattern(String str){
        Pattern splitter = Pattern.compile(":");
        String[] returnedStrings = splitter.split(str);;
        return returnedStrings;
    }

    String[] useMessageCommandPattern(String message){
        Pattern commandPattern = Pattern.compile("^(\\W\\w+)\\s(.+)", Pattern.MULTILINE);

        String[] returnedString = new String[2];
        Matcher matcher = commandPattern.matcher(message);
        returnedString[0] = "/message ";
        returnedString[1] = message;
        if (matcher.matches()){
            String command = matcher.group(1);
            String content = matcher.group(2);

            returnedString[0] = command;
            returnedString[1] = content;
        }

        return returnedString;
    }

    public String[] useFromToMessagePattern(String content){
        Pattern MESSAGE_PATTERN = Pattern.compile("^(\\w+) (.+)", Pattern.MULTILINE);

        String[] returnedString = new String[2];
        Matcher matcher = MESSAGE_PATTERN.matcher(content);

        if (matcher.matches()) {
            String userTo = matcher.group(1);
            String message = matcher.group(2);
            returnedString[0] = userTo;
            returnedString[1] = message;
        }

        return returnedString;
    }

}

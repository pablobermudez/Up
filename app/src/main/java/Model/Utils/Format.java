package Model.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Format {

    public static String convertInputStreamToString(InputStreamReader inputStreamReader) throws IOException {
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer stringBuffer = new StringBuffer();
        String result;
        while((result = reader.readLine())!= null){
            stringBuffer.append(result);
        }
        return stringBuffer.toString();
    }

}

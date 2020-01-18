package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class DefualtURLFecher implements URLFetcher {

    @Override
    public StringBuilder returnStringBuilder(String URL) {
        InputStream is = null;
        try {
            is =  new URL(URL).openStream();
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }

        if (is == null){
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        StringBuilder JSONStr = new StringBuilder();
        String innerStr;
        try{
            while ((innerStr = in.readLine()) != null){
                System.out.println(innerStr);
                JSONStr.append(innerStr);
            }
            in.close();

            if (JSONStr.length() == 0){
                return null;
            }
        }catch (Exception e){
            System.out.println(e.toString());
            return null;
        }

        return JSONStr;
    }

}

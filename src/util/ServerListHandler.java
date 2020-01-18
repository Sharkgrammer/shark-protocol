package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ServerListHandler {

    private final String URL = "http://www.sharkie.ie/servers.JSON";
    private URLFetcher fetcher;

    public ServerListHandler(){
        fetcher = new DefualtURLFecher();
        readJSONFromURL();
    }

    public ServerListHandler(URLFetcher fetcher){
        this.fetcher = fetcher;
        readJSONFromURL();
    }

    private List<JSONDataHolder> readJSONFromURL(){
        StringBuilder JSONStr = fetcher.returnStringBuilder(URL);

        Type collectionType = new TypeToken<List<JSONDataHolder>>(){}.getType();
        List<JSONDataHolder> JSONList = new Gson().fromJson(JSONStr.toString(), collectionType);

        for (int x = 0; x < JSONList.size(); x++){
            System.out.println(JSONList.get(x).getIp());
        }

        return JSONList;
    }
}

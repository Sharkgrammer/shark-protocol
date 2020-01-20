package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerListHandler {

    private final String URL = "http://www.sharkie.ie/servers.JSON";
    private URLHandler fetcher;

    public ServerListHandler() {
        fetcher = new URLUtil();
    }

    public ServerListHandler(DataHolder data) {
        this.fetcher = data.getUrl();
    }


    public void run(){
        List<JSONDataHolder> JSONList = returnNumRandServers(3);

        for (int x = 0; x < JSONList.size(); x++) {
            System.out.println(JSONList.get(x).getIp());
        }
    }

    private List<JSONDataHolder> readJSONFromURL() {
        StringBuilder JSONStr = fetcher.returnStringBuilder(URL);

        Type collectionType = new TypeToken<List<JSONDataHolder>>() {
        }.getType();

        return new Gson().fromJson(JSONStr.toString(), collectionType);
    }

    public List<JSONDataHolder> returnNumRandServers(int num) {
        List<JSONDataHolder> list = readJSONFromURL(), newList = new ArrayList<JSONDataHolder>();
        int sizeInt = list.size(), randInt, pastInt = 0;
        Random rand = new Random();

        for (int x = num; x > 0; x--) {
            randInt = rand.nextInt(sizeInt);

            if (pastInt == randInt) {
                x++;
            } else {
                newList.add(list.get(randInt));
                pastInt = randInt;
            }
        }

        return newList;
    }
}

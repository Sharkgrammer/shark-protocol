package util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ServerListHandler {

    private final String URL = "http://www.sharkie.ie/servers.JSON";
    private URLHandler fetcher;
    private Base64Handler base64;
    private int num;
    private List<JSONDataHolder> ServerList = new ArrayList<>();
    private List<JSONDataHolder> ServerListFull = new ArrayList<>();
    private  JSONDataHolder Server;

    public ServerListHandler(DataHolder data, int num) {
        if (num != 0) {
            this.num = num;
        } else {
            this.num = 3;
        }
        this.fetcher = data.getUrl();
        this.base64 = data.getBase64();
        this.Server = null;
    }

    // REF https://github.com/google/gson/blob/master/UserGuide.md#TOC-Collections-Examples
    private List<JSONDataHolder> readJSONFromURL() {
        StringBuilder JSONStr = fetcher.returnStringBuilder(URL);

        Type collectionType = new TypeToken<List<JSONDataHolder>>() {
        }.getType();

        return new Gson().fromJson(JSONStr.toString(), collectionType);
    }

    private JSONDataHolder returnRandomServer(){

        List<JSONDataHolder> list = getServerListFull();
        JSONDataHolder temp;
        int sizeInt = list.size();
        Random rand = new Random();

        temp = list.get(rand.nextInt(sizeInt));

        return temp;

    }

    private List<JSONDataHolder> returnNumRandServers() {
        List<JSONDataHolder> list = getServerListFull(), newList = new ArrayList<JSONDataHolder>();
        JSONDataHolder temp;
        int sizeInt = list.size(), randInt, pastInt = 0;
        Random rand = new Random();

        for (int x = num; x > 0; x--) {
            randInt = rand.nextInt(sizeInt);

            if (pastInt == randInt) {
                x++;
            } else {
                temp = list.get(randInt);
                temp.createSocket();
                newList.add(temp);
                pastInt = randInt;
            }
        }

        return newList;
    }

    public List<JSONDataHolder> getServerList() {
        if (ServerList.isEmpty()) {
            ServerList = returnNumRandServers();
        }

        return ServerList;
    }

    public List<JSONDataHolder> getServerList(int num) {
        this.num = num;
        return getServerList();
    }

    public List<JSONDataHolder> getServerListFull() {
        if (ServerListFull.isEmpty()) {
            ServerListFull = readJSONFromURL();
        }

        return ServerListFull;
    }

    public JSONDataHolder getSingleServer() {
        if (Server == null) {
            Server = returnRandomServer();
        }

        return Server;
    }
    public void run() {
        /*List<JSONDataHolder> JSONList = readJSONFromURL();

        for (int x = 0; x < JSONList.size(); x++) {
            System.out.println(JSONList.get(x).getIp());
            //System.out.println(Arrays.toString(JSONList.get(x).getKey(base64)));
        }*/

        JSONDataHolder h = getSingleServer();
        System.out.println(h.getIp());
    }
}

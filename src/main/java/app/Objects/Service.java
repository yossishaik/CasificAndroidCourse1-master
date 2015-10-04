package app.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Service {

    public String id;
    public String title;
    public int duration;
    public ArrayList<Giver> givers;

    public Service(String id, String title, int duration, ArrayList<Giver> givers){
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.givers = givers;
    }

    public void addGiver(String id, String title){
        if (this.givers == null){
            givers = new ArrayList<Giver>();
        }

        givers.add(new Giver(id, title));
    }

    public ArrayList<Giver> getGivers() {
        return givers;
    }

    public void addGiversFromSet(HashMap<String, String> giversSet) {
        Iterator iterator = giversSet.keySet().iterator();
        while(iterator.hasNext()) {
            String key=(String)iterator.next();
            String value=(String)giversSet.get(key);
            addGiver(key,value);
        }
    }
}



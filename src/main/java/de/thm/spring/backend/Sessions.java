package de.thm.spring.backend;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 10/2/16.
 */
public class Sessions {

    private Map<String, Session> sessions;
    private static Sessions instance = new Sessions();

    public static Sessions getInstance(){
        return instance;
    }

    private Sessions() {
        sessions = new HashMap<>();
    }


    public void addSession(String key, Path file){
        if(sessions.containsKey(key)){ //only renew the file and keep the rest:
            sessions.put(key, new Session(file,key, sessions.get(key).getDate()));

        } else {

            Session session = new Session(file,key, new Date());
            StatisticsCollector.getInstance().addSessionC();
            sessions.put(key, session);
        }
    }

    public Path getFile(String key){
        if(sessions.containsKey(key))
            return sessions.get(key).getFile();
        return null;
    }

    private void cleanUp(){
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);

        for(String key: sessions.keySet()){
            Session session = sessions.get(key);

            if(session.getDate().compareTo(tomorrow.getTime()) < 1){ //TODO check
                sessions.remove(key);
                session.delete();
            }
        }
    }


}

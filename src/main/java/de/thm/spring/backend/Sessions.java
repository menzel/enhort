package de.thm.spring.backend;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the list of known session objects
 *
 * Created by Michael Menzel on 10/2/16.
 */
public class Sessions {

    private static final Sessions instance = new Sessions();
    private final Map<String, Session> sessions;

    private Sessions() {
        sessions = new HashMap<>();
    }

    public static Sessions getInstance(){
        return instance;
    }

    /**
     * Creates a session and adds it to the list of known sessions.
     *
     * @param key - http session key
     * @param file - input file of the user
     *
     * @return new Session object
     */
    public Session addSession(String key, Path file){
        if(sessions.containsKey(key)){ //only renew the file and keep the rest:
            sessions.put(key, new Session(file,key, sessions.get(key).getDate()));

        } else {

            Session session = new Session(file,key, new Date());
            StatisticsCollector.getInstance().addSessionC();
            sessions.put(key, session);
        }

        return sessions.get(key);
    }

    /**
     * Returns session by given http session key
     *
     * @param key - http session key
     * @return Session from session list. Returns new session if the given key does not exist.
     */
    public Session getSession(String key){
         if(!sessions.containsKey(key))
             sessions.put(key, new Session(key, new Date()));
         return sessions.get(key);
    }

    /**
     * Removes a session from the list by the given key.
     * If the key does not exists this method has no effect.
     *
     * @param key - http session key to identify session to delete
     */
    public void clear(String key){
        Session session = sessions.get(key);
        if(sessions.containsKey(key)){
            sessions.remove(key);
            session.delete();
        }
    }


    /**
     * Deletes all old Sessions
     * Old means older than one day
     */
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

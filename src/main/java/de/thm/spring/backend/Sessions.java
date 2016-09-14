package de.thm.spring.backend;

import java.nio.file.Path;
import java.util.*;

/**
 * Holds the list of known session objects
 * <p>
 * Created by Michael Menzel on 10/2/16.
 */
public final class Sessions {

    private static final Sessions instance = new Sessions();
    private final Map<String, Session> sessions;

    private Sessions() {
        sessions = new HashMap<>();
    }

    public static Sessions getInstance() {
        instance.cleanUp();
        return instance;
    }

    /**
     * Creates a session and adds it to the list of known sessions.
     *
     * @param key  - http session key
     * @param file - input file of the user
     * @return new Session object
     */
    public Session addSession(String key, Path file) {
        if (sessions.containsKey(key)) { //only renew the file and keep the rest:

            Session renew = new Session(file, key, sessions.get(key).getDate(), sessions.get(key).getCustomTracks());
            sessions.put(key, renew);


        } else {

            Session session = new Session(file, key, new Date(), new ArrayList<>());
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
    public Session getSession(String key) {
        if (!sessions.containsKey(key))
            sessions.put(key, new Session(key, new Date()));
        return sessions.get(key);
    }

    /**
     * Removes a session from the list by the given key.
     * If the key does not exists this method has no effect.
     *
     * @param key - http session key to identify session to delete
     */
    public void clear(String key) {
        Session session = sessions.get(key);
        if (sessions.containsKey(key)) {
            sessions.remove(key);
            session.delete();
        }
    }


    /**
     * Deletes all old Sessions
     * Old means older than one day
     */
    private void cleanUp() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        List<String> removes = new ArrayList<>(); // stores the keys of sessions to remove later

        //check each session
        for (String key : sessions.keySet()) {
            Session session = sessions.get(key);

            //check the age of each session
            if (session.getDate().compareTo(yesterday.getTime()) < 0) {
                removes.add(key);
                session.delete();
            }
        }


        // remove from listof keys
        removes.forEach(sessions::remove);
    }


    public int count() {
        return this.sessions.size();
    }

    public Collection<Session> getSessions() {
        return this.sessions.values();
    }
}

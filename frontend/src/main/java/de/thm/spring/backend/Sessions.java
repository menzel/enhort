// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.spring.backend;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        // Clean up the sessions every 3 hours
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(new Thread(this::cleanUp),3L,3L, TimeUnit.HOURS);
    }

    public static Sessions getInstance() {
        return instance;
    }

    /**
     * Creates a session and adds it to the list of known sessions.
     *
     * @param key  - http session key
     * @return new Session object
     */
    public Session addSession(String key) {
        if (sessions.containsKey(key)) { //only renew the file and keep the rest:

            Session renew = new Session(key, sessions.get(key).getDate(), sessions.get(key).getCustomTracks());
            sessions.put(key, renew);


        } else {

            Session session = new Session(key, new Date(), new ArrayList<>());
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
        Calendar threehoursbefore = Calendar.getInstance();
        threehoursbefore.add(Calendar.HOUR_OF_DAY,-3);

        List<String> removes = new ArrayList<>(); // stores the keys of sessions to remove later

        //check each session
        for (String key : sessions.keySet()) {
            Session session = sessions.get(key);

            //check the age of each session
            if (session.getDate().compareTo(threehoursbefore.getTime()) < 0) {
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

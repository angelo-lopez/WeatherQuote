package com.angelosoft.angelo_romel.weatherquote;

import android.util.Log;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * Created by Angelo Romel Lopez
 * B00285812
 */
public class FavLocationList {
    private ArrayList<FavLocation> locations;//<> represents a type parameter/variable.
    //private static final String LOG_TAG = "FavLocationList";

    public FavLocationList() {
        locations = new ArrayList<FavLocation>();
    }

    public void addLocation(String location) {
        this.locations.add(0, new FavLocation(location));
    }

    public String[] getLocations() {
        String[] arrLocations = new String[this.locations.size()];
        ListIterator<FavLocation> it = this.locations.listIterator();

        for(int x = 0; x < this.locations.size(); x ++) {
            if(it.hasNext()) {
                arrLocations[x] = it.next().getLocation();
            }
        }
        return arrLocations;
    }

    public boolean isLocationExists(String location) {
        boolean found = false;
        ListIterator<FavLocation> it = this.locations.listIterator();

        while(it.hasNext() && !found) {
            if(it.next().getLocation().equals(location)) {
                found = true;
            }
        }
        return found;
    }

    public int getItemPosition(String location) {
        int pos = -1;
        int counter = -1;
        boolean found = false;
        ListIterator<FavLocation> it = this.locations.listIterator();

        while(it.hasNext() && !found) {
            counter ++;
            if(it.next().getLocation().equals(location)) {
                pos = counter;
                found = true;
            }
        }
        return pos;
    }

    public boolean isFileExists(Context c, String uid){
        try{
            File file = new File(c.getFilesDir(), getUserFileName(uid));
            if(file.exists()){
                return true;
            }
            else{
                return false;
            }//end if
        }
        catch(Exception ex){
            //Log.i(LOG_TAG, "Error: " + ex.toString());
            return false;
        }//end catch
    }

    public void writeFile(Context c, String uid) {
        try {
            FileOutputStream fileOut = c.openFileOutput(getUserFileName(uid),
                    Context.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.locations);
            objectOut.close();
            fileOut.close();
        }
        catch(Exception ex) {
            //Log.i(LOG_TAG, "Error: " + ex.toString());
        }
    }

    public void readFile(Context c, String uid) {
        if(isFileExists(c, uid)) {
            try {
                FileInputStream fileIn = c.openFileInput(getUserFileName(uid));
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                this.locations = (ArrayList) objectIn.readObject();
                fileIn.close();
                objectIn.close();
            }
            catch(Exception ex) {;
                //Log.i(LOG_TAG, "Error: " + ex.toString());
            }
        }
    }

    public void deleteFile(Context c, String uid) {
        File file = new File(c.getFilesDir(), getUserFileName(uid));
        file.delete();
    }

    public String getUserFileName(String uid) {
        return uid.substring(0, 9);
    }

    public void updateLocation(String[] loc) {
        this.locations.clear();
        if(loc != null) {
            for (int i = 0; i < loc.length; i++) {
                addLocation(loc[i]);
            }
        }
    }

    public void removeLocation(String location) {
        if(isLocationExists(location)) {
            ListIterator<FavLocation> it = this.locations.listIterator();
            int locPosition =0;

            for(int x = 0; x < this.locations.size(); x ++) {
                if(it.hasNext()) {
                    if(it.next().getLocation().equals(location)) {
                        locPosition = x;
                    }
                }
            }
            locations.remove(locPosition);
        }
    }

    public boolean isListEmpty() {
        if(locations.isEmpty()) {
            return true;
        }
        else {
            return false;
        }
    }

    public Object getLocationListObj() {
        return (Object)this.locations;
    }
}

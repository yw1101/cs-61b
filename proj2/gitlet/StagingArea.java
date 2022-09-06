package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class StagingArea implements Serializable{
    /** Record the file was added, path: sha-1 id.*/
    private HashMap<String, String> addMap;
    /** Record the file removed, sha-1 id */
    private HashSet<String> removeSet;
    /** The file tracked */
    private HashMap<String, String> trackedMap;

    /** Constructor */
    public StagingArea(){
        addMap = new HashMap<>();
        removeSet = new HashSet<>();
        trackedMap = new HashMap<>();
    }

    /** Set up the tracked files */
    public void setTrackedMap(HashMap<String, String> fileMap){
        trackedMap = fileMap;
    }

    /** All the records are empty or not */
    public boolean isClean(){
        return addMap.isEmpty() && removeSet.isEmpty();
    }

    /** Make the records empty */
    public void clean(){
        addMap.clear();
        removeSet.clear();
    }

    /** Add the file */
    public boolean add(File file){
        Blob blob = new Blob(file); // Generate the blob.
        String idBlob = blob.getId(); // Get the sha1-id according to the blob.
        String path = file.getPath(); // Get the path of the file added.
        String trackedId = trackedMap.get(path);
        // Return the sha1-id if the file has been tracked otherwise null.

        if(trackedId != null){
            /** if the file is being tracked*/
            if(trackedId.equals(idBlob)){
                /** file is in addMap */
                if(addMap.remove(path) != null){
                    return true;
                }else{
                    /** file should not be removed */
                    return removeSet.remove(path);
                }
            }
        }
        String id_front = addMap.put(path, idBlob);
        if(id_front != null && id_front.equals(idBlob)){
            return false;
        }
        if (!blob.getTheFile().exists()){
            blob.save();
        }

        return removeSet.remove(path);
    }

    /** Remove the file */
    public boolean remove(File file){
        String path = file.getPath();
        String tracked = trackedMap.get(path);
        String changed = addMap.remove(path);

        if(tracked != null){
            /** tracked */
            return removeSet.add(path);
        }else{
            /** not tracked and not added before */
            if(changed == null){
                return false;
            }else{
                /** need to be added in the removeSet.*/
                removeSet.add(path);
            }
        }
        return true;
    }

    /** Commit the stage area. */
    public HashMap<String, String> commit(){
        trackedMap.putAll(addMap);
        for (String path: removeSet){
            trackedMap.remove(path);
        }
        clean();
        return trackedMap;
    }

    /** Get the stage area file */
    public static StagingArea get(){
        return Utils.readObject(Repository.STAGINGAREA, StagingArea.class);
    }

    /** Save the stage area file */
    public void save(){
        Utils.writeObject(Repository.STAGINGAREA, this);
    }

    /** Get the add map */
    public HashMap<String,String> getAddMap(){
        return addMap;
    }

    /** Get the remove set */
    public HashSet<String> getRemoveSet(){
        return removeSet;
    }

    /** Get the tracked map */
    public HashMap<String,String> getTrackedMap(){
        return trackedMap;
    }


}


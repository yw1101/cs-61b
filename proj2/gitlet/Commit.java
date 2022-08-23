package gitlet;

// TODO: any imports you need here

import java.util.Date; // TODO: You'll likely use this in this class
import java.io.Serializable;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
  /**
    * TODO: add instance variables here.
    *
    * List all instance variables of the Commit class here with a useful
    * comment above them describing what that variable represents and how that
    * variable is used. We've provided one example for `message`.
    */

  /** The message of this Commit. */
  private final String message;

  /** The date of this Commit. */
  private final Date date;

  /** The parent of this Commit. */
  private final List<String> parent;

  /** sha-1 id of this Commit. */
  private final String id;

  /** File needs to be tracked. */
  private final Map<String, String> trackedF;

  /** path of file. */
  private final File file;

  /* TODO: fill in the rest of this class. */

  public Commit(String message, List<String> parent, Map<String, String> trackedF){
    this.message = message;
    this.parent = parent;
    this.trackedF = trackedF;
    this.date = new Date();
    this.id = generateId();
    this.file = Utils.join(Repository.COMMITS_DIR, this.id);
  }

  /** initial */
  public Commit(){
    this.message = 'initial message';
    this.parent = new ArrayList<>();
    this.trackedF = new HashMap<>();
    this.date = new Date(0);
    this.id = generateId();
    this.file = Utils.join(Repository.COMMITS_DIR, this.id);
  }

  public void save(){
    Utils.writeObject(file, this);
  }

  public static Commit fromFile(String id){
    File commFile = Utils.join(Repository.COMMITS_DIR, this.id);
    return Utils.readObject(commFile, Commit.class);
  }

  /** generate id*/
  private String generateId(){
    return Utils.sha1(this.message, getTime(), this.parent.toString(), this.trackedF.toString());
  }

  /** need to get the time*/
  /** week month day hour: minute: second year timezone*/
  public String getTime(){
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH: mm: ss yyyy Z", Locale.ENGLISH);
    return dateFormat.format(date);
  }

  /** get the message*/
  public String getMessage(){
    return this.message;
  }

  /** get sha1 id*/
  public String getId(){
    return this.id;
  }

  public Map<String, String> getTrackedF(){
    return this.trackedF;
  }

  public List<String> getParent(){
    return this.parent;
  }

  public String getLog(){
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append("===").append("\n");
    logBuilder.append("commit").append(" ").append(id).append("\n");
    if(parent.size() > 1){
      logBuilder.append("Merge:");
      for(String p : parent){
        logBuilder.append(" ").append(p, 0, 7);
      }
      logBuilder.append("\n");
    }
    logBuilder.append("Date:").append(" ").append(getTime()).append("\n");
    logBuilder.append(message).append("\n");
    return logBuilder.toString();
  }

  public boolean restoreTracked(String filePath){
    String blobId = trackedF.get(filePath);

    if(blobId == null){
      return false;
    }

    Blob.fromFile(blobId).writeContentToSource();
    return true;
  }

  public void restoreTrackedAll(){
    for(String blobId : trackedF.values()){
      Blob.fromFile(blobId).writeContentToSource();
    }
  }
}

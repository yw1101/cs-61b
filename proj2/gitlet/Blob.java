package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/** As the file object. */
public class Blob implements Serializable{

    /** Its sha-1 id and contents cannot be changed. */
    private final String id; // The sha-1 id.
    private final File theFile; // Blob file.
    private final File source; // Source file, should be read as contents stored.
    private final byte[] contents; // The content of the file.

    /** Blob constructor. */
    public Blob(File source){
        this.source = source;
        this.contents = Utils.readContents(source);
        String path = source.getPath();
        this.id = Utils.sha1(path, this.contents);
        this.theFile = UtilsPlus.getObjectFile(this.id);
    }

    /** Save the blob. */
    public void save(){
        UtilsPlus.saveObjectFile(theFile, this);
    }

    /** Get the blob object by the sha-1 id. */
    public static Blob fromId(String id){
        return Utils.readObject(UtilsPlus.getObjectFile(id), Blob.class);
    }

    /** Get the source file. */
    public File getSource(){
        return source;
    }

    /** Get the blob file.*/
    public File getTheFile(){
        return theFile;
    }

    /** Get the sha-1 id of the blob object. */
    public String getId(){
        return id;
    }

    /** Get the contents of the blob as string. */
    public String getContentsInString(){
        return new String(contents, StandardCharsets.UTF_8);
    }

    /** Write the contents to the source file. */
    public void writeContentsForSource(){
        Utils.writeContents(source, contents);
    }

}

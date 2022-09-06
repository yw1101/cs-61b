package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;

public class UtilsPlus {
    public static File getObjectFile(String id){
        String dirName = id.substring(0, 2);
        String fileName = id.substring(2);
        return Utils.join(Repository.OBJECTS_DIR, dirName, fileName);
    }

    public static File stagingF(String id){
        String dirName = id.substring(0, 2);
        String fileName = id.substring(2);
        return Utils.join(Repository.STAGINGAREA, dirName, fileName);
    }

    public static void saveObjectFile(File file, Serializable object){
        File parent = file.getParentFile();
        if(!parent.exists()){
            parent.mkdirs();
        }
        Utils.writeObject(file, object);
    }

    public static String id_generate(File file){
        String path = file.getPath();
        byte[] contents = Utils.readContents(file);
        return Utils.sha1(path, contents);
    }

    public static File getFile_CWD(String name){
        if(Paths.get(name).isAbsolute()) {
            return new File(name);
        } else{
            return Utils.join(Repository.CWD, name);
        }
    }

    public static void exit(String message){
        System.out.println(message);
        System.exit(0);
    }
}

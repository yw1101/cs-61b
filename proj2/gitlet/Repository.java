package gitlet;

import java.io.File;
import static gitlet.Utils.*;


// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
  /**
   * TODO: add instance variables here.
   *
   * List all instance variables of the Repository class here with a useful
   * comment above them describing what that variable represents and how that
   * variable is used. We've provided two examples for you.
   */

  public static final int ID_LENGTH = 40;
  /** The current working directory. */
  public static final File CWD = new File(System.getProperty("user.dir"));
  /** The .gitlet directory. */
  public static final File GITLET_DIR = join(CWD, ".gitlet");

  /** The .gitlet/objects directory to store blobs commits. */
  public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
  public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

  public static final File REFS_DIR = join(GITLET_DIR, "refs");

  public static final File BRANCH_DIR = join(GITLET_DIR, "branches");
  public static final File STAGINGAREA = join(GITLET_DIR, "staging area");

  public static final File HEAD = join(GITLET_DIR, "HEAD.txt");
  public static StagingArea stagingArea = STAGINGAREA.exists()? StagingArea.fromFile(): new StagingArea();

  /* TODO: fill in the rest of this class. */
  public static void init(){
    if(GITLET_DIR.exists()){
      System.out.println("A gitlet version-control system already exists in the current directory");
    } else{
      GITLET_DIR.mkdir();
      OBJECTS_DIR.mkdir();
      COMMITS_DIR.mkdir();
      REFS_DIR.mkdir();
      BRANCH_DIR.mkdir();

      try{
        HEAD.createNewFile();
      }
    }
  }





}

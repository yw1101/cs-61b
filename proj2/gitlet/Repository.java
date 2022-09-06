package gitlet;

import lab9.Map61B;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

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
   * <p>
   * List all instance variables of the Repository class here with a useful
   * comment above them describing what that variable represents and how that
   * variable is used. We've provided two examples for you.
   */

  public static final int ID_LENGTH = 40;
  /**
   * The current working directory.
   */
  public static final File CWD = new File(System.getProperty("user.dir"));
  /**
   * The .gitlet directory.
   */
  public static final File GITLET_DIR = join(CWD, ".gitlet");

  /**
   * The .gitlet/objects directory to store blobs, commits.
   */
  public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
  public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

  public static final File REFS_DIR = join(GITLET_DIR, "refs");

  public static final File BRANCH_DIR = join(GITLET_DIR, "branches");

  public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

  public static final File ACTIVE_BRANCH_DIR = join(GITLET_DIR, "Active_Branch.txt");
  public static final File STAGINGAREA = join(GITLET_DIR, "staging area");

  public static final File HEAD = join(GITLET_DIR, "HEAD.txt");
  public static StagingArea stagingArea = STAGINGAREA.exists() ? StagingArea.get() : new StagingArea();

  /* TODO: fill in the rest of this class. */


  private static void checkoutCommit(Commit checkoutComm) {
    stagingArea.clean();
    stagingArea.save();
    checkoutComm.restoreTrackedAll();
  }

  private static void headPointer(String headCommitId) {
    Utils.writeContents(HEAD, headCommitId);
  }

  private static List<File> getCWDFiles() {
    List<File> files = new ArrayList<>();
    for (String name : plainFilenamesIn(CWD)) {
      File file = Utils.join(CWD, name);
      files.add(file);
    }
    return files;
  }

  private static void createInitialStagingArea() {
    StagingArea initialStage = new StagingArea();
    initialStage.save();
  }

  private static void updateActiveBranch(Commit commit) {
    Branch activeBranch = new Branch(commit, Branch.activeBranchName());
    activeBranch.save();
    activeBranch.activeBranch_save();
  }

  private static String getBranchStatus() {
    StringBuilder branch = new StringBuilder();
    branch.append("===Branches====\n");

    List<String> allNames = Utils.plainFilenamesIn(BRANCH_DIR);

    assert allNames != null;
    Collections.sort(allNames);

    for (String name : allNames) {
      Branch b = Branch.fromBranchName(name);
      if (b.getBranchName().equals(Branch.activeBranchName())) {
        branch.append("*").append(b).append("\n");
      } else {
        branch.append(b).append("\n");
      }
    }
    return branch.toString();
  }

  private static String getAllCommId(String subid) {
    String allId = "";
    boolean find = false;

    for (String commId : plainFilenamesIn(COMMITS_DIR)) {
      if (commId.contains(subid)) {
        allId = commId;
        find = true;
      }
    }

    if (!find) {
      UtilsPlus.exit("No such commit exists");
    }
    return allId;
  }

  private static void fileName_append(StringBuilder builder, Collection<String> pathsCollection) {
    List<String> paths = new ArrayList<>(pathsCollection);
    List<String> names = new ArrayList<>();
    for (String path : paths) {
      String name = Paths.get(path).getFileName().toString();
      names.add(name);
    }
    names.sort(String::compareTo);

    for (String name : names) {
      builder.append(name).append("\n");
    }
  }

  private static String getStagedStatus() {
    StringBuilder stagedstatus = new StringBuilder();
    stagedstatus.append("===Staged Files===\n");
    Collection<String> pathCollection = stagingArea.getAddMap().keySet();
    fileName_append(stagedstatus, pathCollection);
    return stagedstatus.toString();
  }

  private static String getRemovedStatus() {
    StringBuilder removedstatus = new StringBuilder();
    removedstatus.append("===Removed Files===\n");
    Collection<String> pathCollection = stagingArea.getRemoveSet();
    fileName_append(removedstatus, pathCollection);
    return removedstatus.toString();
  }

  private static void checkUntrackedFile(Commit comm) {
    for (String name : plainFilenamesIn(CWD)) {
      File source = Utils.join(CWD, name);
      String path = source.getPath();
      Blob blob = new Blob(source);
      String blobId = blob.getId();

      Map<String, String> checkoutTracked = comm.getTrackedF();
      Map<String, String> currTracked = getHeadComm().getTrackedF();

      boolean curr = currTracked.containsKey(path);
      boolean checkeout = checkoutTracked.containsKey(path);

      if (!curr && checkeout && (!blobId.equals(checkoutTracked.get(path)))) {
        UtilsPlus.exit("This is an untracked file; delete it or add and commit it.");
      }
      if (curr && checkeout) {
        if (!blobId.equals(checkoutTracked.get(path)) && !blobId.equals(currTracked.get(path))) {
          UtilsPlus.exit("This is an untracked file; delete it or add and commit it.");
        }
      }
    }
  }

  private static void deleteUntrackedFile(Commit comm) {
    Map<String, String> currTracked = getHeadComm().getTrackedF();
    Map<String, String> checkoutTracked = comm.getTrackedF();
    for (File file : getCWDFiles()) {
      boolean curr = currTracked.containsKey(file.getPath());
      boolean checkout = checkoutTracked.containsKey(file.getPath());

      if (curr && !checkout) {
        file.delete();
      }
    }
  }

  private static byte[] addContents(byte[] added, byte[] newContents) {
    byte[] results = new byte[added.length + newContents.length];
    System.arraycopy(added, 0, results, 0, added.length);
    System.arraycopy(newContents, 0, results, added.length, newContents.length);
    return results;
  }

  /**
   * The commands
   */
  public static void init() {
    if (GITLET_DIR.exists()) {
      System.out.println("A gitlet version-control system already exists in the current directory");
    } else {
      GITLET_DIR.mkdir();
      OBJECTS_DIR.mkdir();
      COMMITS_DIR.mkdir();
      REFS_DIR.mkdir();
      BRANCH_DIR.mkdir();

      try {
        HEAD.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
      Commit initialCommit = new Commit();
      initialCommit.save();
      String initialCommitId = initialCommit.getId();

      Branch.pointer(HEAD, initialCommitId);
      Branch master = new Branch(initialCommit);
      master.save();

      /** savecreate an initial stage */
      createInitialStagingArea();
    }
  }


  public static void add(String fileName) {
    File file = UtilsPlus.getFile_CWD(fileName);
    if (!file.exists()) {
      System.out.println("File does not exist");
      System.exit(0);
    }
    boolean changed = stagingArea.add(file);
    if (changed) {
      stagingArea.save();
    }
  }


  public static void remove(String fileName) {
    File file = UtilsPlus.getFile_CWD(fileName);
    boolean changed = stagingArea.remove(file);

    if (!changed) {
      UtilsPlus.exit("No reason to remove the file.");
    } else {
      stagingArea.save();
    }
  }

  public static void log() {
    Commit headComm = getHeadComm();
    Commit currComm = headComm;

    StringBuilder log = new StringBuilder();
    do {
      log.append(currComm.getLog()).append("\n");
      List<String> parent = currComm.getParent();
      if (parent.size() == 0) {
        break;
      }

      String nextCommId = parent.get(0);
      Commit nextComm = Commit.fromId(nextCommId);
      currComm = nextComm;
    } while (true);
    System.out.print(log);
  }

  public static void globalLog() {
    StringBuilder globalLog = new StringBuilder();

    List<String> allNames = Utils.plainFilenamesIn(Repository.COMMITS_DIR);
    for (String commId : allNames) {
      Commit commit = Commit.fromId(commId);
      globalLog.append(commit.getLog()).append("\n");
    }
    System.out.print(globalLog);
  }

  public static void find(String message) {
    StringBuilder commMessage = new StringBuilder();
    List<String> allNames = plainFilenamesIn(Repository.COMMITS_DIR);
    for (String commId : allNames) {
      Commit commit = Commit.fromId(commId);
      if (commit.getMessage().equals(message)) {
        commMessage.append(commId).append("\n");
      }
    }
    if (commMessage.length() == 0) {
      UtilsPlus.exit("Found no commits with that message");
    } else {
      System.out.print(commMessage);
    }
  }

  public static void status() {
    StringBuilder status = new StringBuilder();
    status.append(getBranchStatus()).append("\n");
    status.append(getStagedStatus()).append("\n");
    status.append(getRemovedStatus());

    System.out.print(status);
  }

  public static void checkout(String name) {
    String path = UtilsPlus.getFile_CWD(name).getPath();
    if (!getHeadComm().trackedExists(path)) {
      UtilsPlus.exit("File does not exist in that commit.");
    }
  }

  public static void checkout(String commId, String name) {
    String allCommId = getAllCommId(commId);
    String path = UtilsPlus.getFile_CWD(name).getPath();
    if (!Commit.fromId(allCommId).trackedExists(path)) {
      UtilsPlus.exit("File does not exist in that commit.");
    }
  }

  public static void checkoutBranch(String branchName) {
    File branchFile = Utils.join(BRANCH_DIR, branchName);
    if (!branchFile.exists()) {
      UtilsPlus.exit("No such branch exists.");
    }
    // the branch is the current active branch.
    if (branchName.equals(Branch.activeBranchName())) {
      UtilsPlus.exit("No need to checkout the current branch.");
    }
    Branch currBranch = Branch.fromBranchName(Branch.activeBranchName());
    Branch checkoutBranch = Branch.fromBranchName(branchName);

    checkUntrackedFile(checkoutBranch.getHeadComm());
    deleteUntrackedFile(checkoutBranch.getHeadComm());
    headPointer(checkoutBranch.getHeadCommId());

    checkoutBranch.activeBranch_save();
  }

  public static void branch(String branchName) {
    File branchFile = Utils.join(BRANCH_DIR, branchName);
    if (branchFile.exists()) {
      UtilsPlus.exit("A branch with the name already exists.");
    }
    Branch branch = new Branch(getHeadComm(), branchName);
    branch.save();
  }

  public static void removeBranch(String branchName) {
    File branchFile = Utils.join(BRANCH_DIR, branchName);
    if (!branchFile.exists()) {
      UtilsPlus.exit("A branch with that name does not exist.");
    }

    if (branchName.equals(Branch.activeBranchName())) {
      UtilsPlus.exit("Cannot remove the current branch.");
    }
    branchFile.delete();
  }

  public static void reset(String commId) {
    commId = getAllCommId(commId);
    Commit comm = Commit.fromId(commId);

    checkUntrackedFile(comm);
    deleteUntrackedFile(comm);
    checkoutCommit(comm);
    headPointer(commId);
  }

  public static void commit(String message, String nextParent) {
    if (stagingArea.isClean()) {
      UtilsPlus.exit("No changes added to the commit.");
    }
    Map<String, String> nextTrackedMap = stagingArea.commit();
    stagingArea.save();

    List<String> parent = new ArrayList<>();
    Commit headCommit = getHeadComm(); // Get the current head commit.
    parent.add(headCommit.getId()); // Prepare for the commit.
    if (nextTrackedMap != null) {
      parent.add(nextParent);
    }
    Commit commit = new Commit(message, parent, nextTrackedMap);
    commit.save();

    // When commit, only the avtiveBranch and HEAD is moving.
    headPointer(commit.getId());
    updateActiveBranch(commit);
  }

  public static String getHeadCommId() {
    return Utils.readContentsAsString(HEAD);
  }

  public static Commit getHeadComm() {
    String headCommId = getHeadCommId();
    return Commit.fromId(headCommId);
  }

  // find the split point, which is actually the latest common branch head
  public static String splitFinder(Branch given, Branch curr){
    // head commit id for these two branches
    Commit headGiven = given.getHeadComm();
    Commit headCurr = curr.getHeadComm();
    // create a HashMap to store the two branches's traversal data
    // key: commitId -> value: depth
    HashMap<String, Integer> mapGiven = new HashMap<>();
    HashMap<String, Integer> mapCurr = new HashMap<>();
    // get the parents of the commit
    List<String> givenPar = headGiven.getParent();
    List<String> currPar = headCurr.getParent();
    // create the queue to mark if the id was visited
    Queue<String> givenIdtobeVisited = new LinkedList<>();
    Queue<String> currIdtobeVisited = new LinkedList<>();
    // put the head commit id in the queue
    givenIdtobeVisited.addAll(givenPar);
    currIdtobeVisited.addAll(currPar);
    // record the visited commit
    HashSet<Commit> givenCommitVisited = new HashSet<>();
    HashSet<Commit> currCommitVisited = new HashSet<>();
    int depth = 1;
    while(!givenIdtobeVisited.isEmpty()){
      int size = givenIdtobeVisited.size();
      while(size-->0){
        String visiting = givenIdtobeVisited.poll();
        Commit visitingComm = Commit.fromId(visiting);
        if(!givenCommitVisited.contains(visitingComm)){
          mapGiven.put(visiting, depth);
          givenIdtobeVisited.addAll(visitingComm.getParent());
          givenCommitVisited.add(visitingComm);
        }
      }
      depth += 1;
    }
    while(!currIdtobeVisited.isEmpty()) {
      int size = currIdtobeVisited.size();
      while (size-- > 0) {
        String visiting = currIdtobeVisited.poll();
        Commit visitingComm = Commit.fromId(visiting);
        if (!currCommitVisited.contains(visitingComm)) {
          mapCurr.put(visiting, depth);
          currIdtobeVisited.addAll(visitingComm.getParent());
          currCommitVisited.add(visitingComm);
        }
      }
      depth += 1;
    }
    List<String> givenList = new ArrayList<>(mapGiven.keySet());
    int minVal = 100000;
    String minKey = new String();
    for(String givenId: givenList){
      if(mapCurr.containsKey(givenId)) {
        int val = mapGiven.get(givenId);
        if(minVal > val){
          minVal = val;
          minKey = givenId;
        }
      }
    }
    return minKey;
  }

  /** Tell whether can be merged or not */
  public static boolean can_be_merged(String branchName) {
    boolean recall = false;
    Commit headCommit = getHeadComm();
    Branch mergedBranch = Branch.fromBranchName(branchName);
    // check whether the stage is empty or not.
    if (!stagingArea.getAddMap().isEmpty() || !stagingArea.getRemoveSet().isEmpty()) {
      System.out.println("You have uncommitted changes.");
      recall = true;
      return recall;
    } else if (!Utils.join(BRANCH_DIR, branchName).exists()) {
      System.out.println("A branch with that name does not exist.");
      recall = true;
      return recall;
    } else if (mergedBranch.getHeadComm().equals(headCommit)){
      System.out.println("Cannot merge a branch with itself.");
      recall = true;
      return recall;
    }
    return recall;
  }

  // merge the file
  public static void mergeGenerate(String fileName, String blobName) {
    File merge = Utils.join(CWD, fileName);
    File blob = Utils.join(BLOB_DIR, blobName);
    byte[] newContents = addContents("<<<<<<Head\n".getBytes(StandardCharsets.UTF_8), Utils.readContents(blob));
    newContents = addContents(newContents, "========\n".getBytes(StandardCharsets.UTF_8));
    newContents = addContents(newContents, ">>>>>>\n".getBytes(StandardCharsets.UTF_8));
    Utils.writeContents(merge, newContents);
  }

  /** Get the contents */
  public static void mergeWrite(String blobName, String fileName, String mergedBlobName) {
    File merge = Utils.join(CWD, fileName);
    File blob = Utils.join(BLOB_DIR, blobName);
    File mergedBlob = Utils.join(BLOB_DIR, mergedBlobName + ".txt");
    byte[] newContents = addContents("<<<<<<Head\n".getBytes(StandardCharsets.UTF_8), Utils.readContents(blob));
    newContents = addContents(newContents, "========\n".getBytes(StandardCharsets.UTF_8));
    newContents = addContents(newContents, Utils.readContents(mergedBlob));
    newContents = addContents(newContents, ">>>>>>\n".getBytes(StandardCharsets.UTF_8));
    Utils.writeContents(merge, newContents);
  }


  public static void mergeHelper(Commit currComm, Commit givenComm, Commit split, String branchName){
    Map<String, String> currMap = currComm.getTrackedF();
    Map<String, String> givenMap = givenComm.getTrackedF();
    Map<String, String> splitMap = split.getTrackedF();
    boolean conflict = false;
    for(String path: currMap.keySet()){
      if(splitMap.containsKey(path) && givenMap.containsKey(path)){
        if(splitMap.get(path).equals(currMap.get(path)) && !splitMap.get(path).equals(givenMap.get(path))){
          checkout(givenComm.getId(), path);
          stagingArea.getAddMap().put(path, givenMap.get(path));
        }else if(!splitMap.get(path).equals(currMap.get(path)) && splitMap.get(path).equals(givenMap.get(path))){

        }else if(!splitMap.get(path).equals(currMap.get(path)) && givenMap.get(path).equals(currMap.get(path))){
          // merge conflict
          conflict = true;
          mergeWrite(Blob.fromId(currMap.get(path)).getTheFile().getName(), CWD.getName(), Blob.fromId(givenMap.get(path)).getTheFile().getName());
        }
      }else if(splitMap.containsKey(path) && !givenMap.containsKey(path)){
        if(!currMap.get(path).equals(splitMap.get(path))){
          conflict = true;
          mergeGenerate(CWD.getName(), Blob.fromId(currMap.get(path)).getTheFile().getName());
        }
      }else if(!splitMap.containsKey(path) && !givenMap.containsKey(path)){
        // do nothing
      }
    }

    for(String path: givenMap.keySet()){
      if(!currMap.containsKey(path) && !givenMap.containsKey(path)){
        checkout(givenComm.getId(), path);
        stagingArea.getAddMap().put(path, givenMap.get(path));
      }else if(!currMap.containsKey(path) && splitMap.containsKey(path)){
        // do nothing
      }else if(splitMap.containsKey(path) && !currMap.containsKey(path)){
        if(!splitMap.get(path).equals(givenMap.get(path))){
          conflict = false;
          mergeGenerate(CWD.getName(), Blob.fromId(givenMap.get(path)).getTheFile().getName());
        }
      }
    }

    for(String path: splitMap.keySet()){
      if(!currMap.containsKey(path) && !givenMap.containsKey(path)){
        Blob.fromId(splitMap.get(path)).getTheFile().delete();
      }
    }
    if(!conflict){
      commit("Merged" + Branch.fromBranchName(Branch.activeBranchName()) + "with" + branchName + ".", Branch.fromBranchName(branchName).getHeadCommId());
    }else{
      System.out.println("Encountered a merge conflict");
    }
  }

  public static void merge(String branchName) {
    File branchFile = Utils.join(BRANCH_DIR, branchName);
    boolean conflict = false;
    if (can_be_merged(branchName)) {
      return;
    }


    ArrayList<File> files = new ArrayList<>();
    HashMap<String, String> commitTree = new HashMap<>();

    Branch givenBranch = Branch.fromBranchName(branchName);
    String givenHeadId = givenBranch.getHeadCommId();

    Branch currBranch = Branch.fromBranchName(Branch.activeBranchName());
    String currHeadId = currBranch.getHeadCommId();



    if(branchName.equals("master")){
      /**
       * current branch' last commit is the split point in the given branch
       * so we need to set the given branch's last to current branch's last
       */
      currHeadId = givenHeadId;
      System.out.println("Current branch fast-forward");
    }else if(splitFinder(givenBranch, currBranch).equals(currBranch.getHeadCommId())){
      System.out.println("Given branch is an ancestor of the current branch.");
    }else{
      // actually merge
      Commit givenComm = Commit.fromId(givenHeadId);
      Commit currComm = Commit.fromId(currHeadId);
      checkUntrackedFile(givenComm);

      mergeHelper(currComm, givenComm, Commit.fromId(splitFinder(givenBranch, currBranch)), branchName);
    }

  }

}


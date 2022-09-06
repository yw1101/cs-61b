package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Branch implements Serializable{
    private String branchName;
    private String headCommId; // Can be 2 but only one is active.
    private Commit headComm; // The commit.

    /** Initial branch constructor. */
    public Branch(Commit initialCommit){
        this.branchName = "master";
        this.headComm = initialCommit;
        this.headCommId = headComm.getId();
    }

    /** Branch constructor. */
    public Branch(Commit headComm, String nextBranchName){
        this.branchName = nextBranchName;
        this.headComm = headComm; // The head/current commit.
        this.headCommId = headComm.getId(); // The sha-1 id of the head commit.
    }

    /** Get the branch's head commit*/
    public Commit getHeadComm(){
        return headComm;
    }

    /** Get the branch name.*/
    public String getBranchName(){
        return branchName;
    }

    /** Get the sha-1 id of head commit.*/
    public String getHeadCommId(){
        return headCommId;
    }

    /** Get a branch object from branchName*/
    public static Branch fromBranchName(String branchName){
        File file = Utils.join(Repository.BRANCH_DIR, branchName);
        if (!file.exists()){
            return null;
        }
        return Utils.readObject(file, Branch.class);
    }

    /** Write the object to file.*/
    public void save(){
        File file = Utils.join(Repository.BRANCH_DIR, this.branchName);
        Utils.writeObject(file, this);
    }

    /** Get the active branch name.*/
    public static String activeBranchName(){
        return Utils.readContentsAsString(Repository.ACTIVE_BRANCH_DIR);
    }

    /** Write the contents to file.*/
    public void activeBranch_save(){
        Utils.writeContents(Repository.ACTIVE_BRANCH_DIR, this.branchName);
    }

    /** Get the head pointer as a string.*/
    public static String getHead(){

        return Utils.readContentsAsString(Repository.HEAD);
    }

    /** Pointer set up.*/
    public static void pointer(File file, String commitId){
        if(!file.exists()){
            try{
                file.createNewFile();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        Utils.writeContents(file, commitId);
    }
}

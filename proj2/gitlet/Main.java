package gitlet;

import static gitlet.UtilsAdd.*
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
  public static void main(String[] args) {
    // TODO: what if args is empty?
    if(args.length == 0){
      exit("Please enter a command.");
    }

    String firstArg = args[0];
    int l = args.length;
    switch(firstArg) {
      case "init": Repository.init();
            // TODO: handle the `init` command

      case "add": {
        String fileName = args[1];
        Repository.add(fileName);
      }
      // TODO: handle the `add [filename]` command
      // TODO: FILL THE REST IN
      case "commit": {
        String message = args[1];
        String secondParent = null;
        if(l == 3){
          secondParent = args[2];
        }
        Repository.commit(message, secondParent);
      }

      case "rm": Repository.remove(args[1]);

      case "log": Repository.log();

      case "global-log": Repository.globalLog();

      case "find": {
        String m = args[1];
        Repository.find(m);
      }

      case "status": Repository.status();

      case "checkout": {
        switch(args.length){
          case "3": {
            if(!args[1].equals("--")){
              UtilsAdd.exit("Incorrect operands.");
            }
            String fileName = args[2];
            Repository.checkout(fileName);
          }

          case "4": {
            if(!args[2].equals("--")){
              UtilsAdd.exit("Incorrect operands.");
            }
            String fileName = args[3];
            String commitId = args[1];
            Repository.checkout(commitId, fileName);
          }

          case "2": {
            Repository.checkoutBranch(args[1]);
          }

          default: UtilsAdd.exit("Incorrect operands.");
        }
      }

      case "branch": Repository.branch(args[1]);

      case "rm-branch": {
        String branchName = args[1];
        Repository.removeBranch(branchName);
      }

      case "reset": Repository.reset(args[1]);

      case "merge": {

      }


  }
}

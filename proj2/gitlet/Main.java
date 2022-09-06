package gitlet;

import static gitlet.UtilsPlus.*;
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
      // handle the `init` command
      case "init": Repository.init();

      // handle the `add [filename]` command
      case "add": {
        String fileName = args[1];
        Repository.add(fileName);
      }

      // FILL THE REST IN
      // handle the `commit [message]` command
      case "commit": {
        String message = args[1];
        String secondParent = null;
        if(l == 3){
          secondParent = args[2];
        }
        Repository.commit(message, secondParent);
      }
      // handle the `rm [filename]` command
      case "rm": Repository.remove(args[1]);

      // handle the `add` command
      case "log": Repository.log();

      // handle the `global-log` command
      case "global-log": Repository.globalLog();

      // handle the `find [commit message]` command
      case "find": {
        String m = args[1];
        Repository.find(m);
      }
      // handle the `status` command
      case "status": Repository.status();

      // handle the `checkout command
      case "checkout": {
        switch(args.length){
          // handle the `checkout -- [filename]` command
          case 3: {
            if(!args[1].equals("--")){
              UtilsPlus.exit("Incorrect operands.");
            }
            String fileName = args[2];
            Repository.checkout(fileName);
          }

          // handle the `checkout [commit id] -- [file name]` command
          case 4: {
            if(!args[2].equals("--")){
              UtilsPlus.exit("Incorrect operands.");
            }
            String fileName = args[3];
            String commitId = args[1];
            Repository.checkout(commitId, fileName);
          }

          // handle the 'checkout [branch name]' command
          case 2: {
            Repository.checkoutBranch(args[1]);
          }

          default: UtilsPlus.exit("Incorrect operands.");
        }
      }

      // handle the 'branch [branch name]' command
      case "branch": Repository.branch(args[1]);

      // handle the 'rm-branch [branch name]' command
      case "rm-branch": {
        String branchName = args[1];
        Repository.removeBranch(branchName);
      }

      // handle the 'reset [commit id]' command
      case "reset": Repository.reset(args[1]);


      // handle the 'merge [branch name]' command
      case "merge": Repository.merge(args[1]);
    }
  }
}

package command.later.good.command;


/**
 * 这个就是  啥也不做
 * 在初始化的时候很有用
 */
public class NoCommand implements Command {
    @Override
    public void execute() {
    }

    @Override
    public void undo() {
    }
}


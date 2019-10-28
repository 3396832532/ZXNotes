package command.later.good.command;


import command.later.good.furniture.TV;

/*** 电视关闭的Command */
public class TVOffCommand implements Command{

    private TV tv;

    public TVOffCommand(TV tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.off();
    }

    @Override
    public void undo() {
        tv.on();
    }
}

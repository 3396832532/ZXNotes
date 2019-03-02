package command.later.good.command;


import command.later.good.furniture.TV;

public class TVOnCommand implements Command{

    private TV tv;

    public TVOnCommand(TV tv) {
        this.tv = tv;
    }

    @Override
    public void execute() {
        tv.on();
    }

    @Override
    public void undo() {
        tv.off();
    }
}

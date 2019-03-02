package command.later.good.command;

import command.later.good.furniture.Light;

public class LightOffCommand implements Command{
    private Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on(); //原来是关掉的，现在就是打开
    }
}

package command.later.good;

import command.later.good.command.LightOffCommand;
import command.later.good.command.LightOnCommand;
import command.later.good.command.TVOffCommand;
import command.later.good.command.TVOnCommand;
import command.later.good.furniture.Light;
import command.later.good.furniture.TV;

public class Test {
    public static void main(String[] args) {
        Light light = new Light();
        TV tv = new TV();

        LightOnCommand lightOnCommand = new LightOnCommand(light);
        LightOffCommand lightOffCommand = new LightOffCommand(light);

        TVOffCommand tvOffCommand = new TVOffCommand(tv);
        TVOnCommand tvOnCommand = new TVOnCommand(tv);

        Control control = new Control();
        control.setOnCommand(1, lightOnCommand);
        control.setOffCommand(1, lightOffCommand);
        control.setOnCommand(2, tvOnCommand);
        control.setOffCommand(2, tvOffCommand);

        control.on(1);
        control.on(2);
        control.undo();
        control.off(1);
    }
}

package command.later.good;

import command.later.good.command.Command;
import command.later.good.command.NoCommand;
import java.util.Stack;

//控制类
public class Control {
    private Command[] onCommands; //一列的 开启按钮
    private Command[] offCommands; //一列 关闭按钮
    private final int slotNum = 10;

    //为了实现undo操作
    private Stack<Command> stack = new Stack<>();

    public Control() {
        //初始化
        offCommands = new Command[slotNum]; //10排
        onCommands = new Command[slotNum];

        //下面就是NoCommand的作用, 并不是每一个按钮都对应着家电，有可能是空的,这样下面就不要判断是不是空了
        Command noCommand = new NoCommand();
        for (int i = 0; i < onCommands.length; i++) {
            onCommands[i] = noCommand;
            offCommands[i] = noCommand;
        }
    }

    //遥控器并不知道绑定的是什么家具   解耦合
    //把命令对象设置到遥控器上 : 很重要 , 把命令封装成类 作为参数命令传进来，绑定到某个插槽
    public void setOnCommand(int slot, Command onCommand) {
        onCommands[slot] = onCommand;
    }
    public void setOffCommand(int slot,Command offCommand) {
        offCommands[slot] = offCommand;
    }

    // 下面是三个控制器执行的方法
    public void on(int slot){
        onCommands[slot].execute();
        stack.push(onCommands[slot]);//记录
    }
    public void off(int slot){
        offCommands[slot].execute();
        stack.push(offCommands[slot]);
    }
    public void undo(){
        stack.pop().undo(); //具体的回退   要回退的话，首先要记住按了哪些按钮， 可以使用栈的结构
    }
}

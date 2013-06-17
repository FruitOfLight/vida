package ui;

import java.io.PrintStream;

import enums.ToolTarget;
import enums.ToolType;

public class Tool {
    static boolean[] autoTarget = new boolean[ToolTarget.values().length];
    static boolean[] autoType = new boolean[ToolType.values().length];
    public ToolType type;
    public ToolTarget target;
    public int value;

    public Tool(ToolType type, ToolTarget target, int value) {
        this.type = type;
        this.target = target;
        this.value = value;
    }

    public Tool(ToolType type, ToolTarget target) {
        this(type, target, 0);
    }

    public Tool(ToolType type) {
        this(type, ToolTarget.any, 0);
    }

    public Tool() {
        this(ToolType.any, ToolTarget.any, 0);
    }

    public boolean compatible(ToolType type) {
        return type == this.type || (this.type == ToolType.any && autoType[type.ordinal()]);
    }

    public boolean compatible(ToolTarget target) {
        return target == this.target
                || (this.target == ToolTarget.any && autoTarget[target.ordinal()]);
    }

    public void print(PrintStream out) {
        out.print(type.name() + " " + target.name() + " " + value + "\n");
    }
}
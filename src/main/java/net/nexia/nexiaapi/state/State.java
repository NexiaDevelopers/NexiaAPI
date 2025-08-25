package net.nexia.nexiaapi.state;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class State {

    private Runnable _enterFunc = null;
    private Runnable _exitFunc = null;

    public List<Runnable> onEnter = new ArrayList<>();
    public List<Runnable> onExit = new ArrayList<>();

    public State() {

    }

    public State(Runnable func) {
        _enterFunc = func;
    }

    public State(Runnable enter, Runnable exit) {
        _enterFunc = enter;
        _exitFunc = exit;
    }

    public State setEnter(Runnable func) {
        _enterFunc = func;
        return this;
    }

    public State setExit(Runnable func) {
        _exitFunc = func;
        return this;
    }

    public void start() {
        if (_enterFunc != null) {
            _enterFunc.run();
            onEnter.forEach(Runnable::run);
        }
    }

    public void end() {
        if (_exitFunc != null) {
            _exitFunc.run();
            onExit.forEach(Runnable::run);
        }
    }

}

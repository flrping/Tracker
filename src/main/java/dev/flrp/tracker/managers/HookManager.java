package dev.flrp.tracker.managers;

import dev.flrp.espresso.hook.stacker.StackerProvider;
import dev.flrp.tracker.Tracker;

public class HookManager {

    private final StackerProvider stackerProvider;

    public HookManager(
            Tracker plugin,
            StackerProvider stackerProvider
    ) {
        this.stackerProvider = stackerProvider;
    }
    public StackerProvider getStackerProvider() {
        return stackerProvider;
    }

}

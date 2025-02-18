package org.aytsan_lex.twitchbot.managers;

public abstract class ConfigFileBasedManager implements IManager
{
    @Override
    public boolean initialize() { return this.onInitialize(); }

    @Override
    public void shutdown() { this.onShutdown(); }

    public abstract void readFile();
    public abstract void saveFile();

    protected abstract boolean onInitialize();
    protected abstract void onShutdown();
}

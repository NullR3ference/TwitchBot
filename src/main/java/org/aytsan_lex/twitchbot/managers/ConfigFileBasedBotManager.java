package org.aytsan_lex.twitchbot.managers;

public abstract class ConfigFileBasedBotManager implements IBotManager
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

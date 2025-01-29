package org.aytsan_lex.twitchbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotGlobalState
{
    public static class VotingContext
    {
        private final String content;
        private final int targetVotes;
        private int currentVotes;

        public VotingContext(String content, int target)
        {
            this.content = content;
            this.targetVotes = target;
            this.currentVotes = 0;
        }

        public String getContent() { return this.content; }
        public int getTargetVotes() { return this.targetVotes; }
        public int getCurrentVotes() { return this.currentVotes; }
        public void addVote() { this.currentVotes++; }
        public boolean isComplete() { return this.currentVotes >= this.targetVotes; }

        @Override
        public String toString()
        {
            return "VotingContext{'%s'; %d/%d; complete=%b}"
                    .formatted(this.content, this.currentVotes, this.targetVotes, isComplete());
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BotGlobalState.class);
    private static VotingContext currentVotingContext = null;

    public static void startVoting(String content, int target)
    {
        currentVotingContext = new VotingContext(content, target);
        LOGGER.info("Voting started: {}", currentVotingContext);
    }

    public static void stopVoting()
    {
        LOGGER.info("Voting stopped: {}", currentVotingContext);
        currentVotingContext = null;
    }

    public static boolean votingIsActive()
    {
        return currentVotingContext != null;
    }

    public static VotingContext getCurrentVotingContext()
    {
        return currentVotingContext;
    }
}

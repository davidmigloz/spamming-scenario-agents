# Spamming scenario

## Introduction 

The aim of this experiment is to test characteristics / boundaries of messaging in the [JADE](http://jade.tilab.com/) agents platform. 

In agent-based systems, groups of agents coordinate their actions to achieve a common goal. To do this, they exchange messages. The number of messages exchanged can be huge. 

We want to test the message exchange capabilities of JADE platform under different situations. The method to achieve this goal is to implement and test a "spamming scenario".

## Scenario

The scenario is designed to flood the system with messages. It has three types of agents:

- **Spammer Agent (SA):** sends N messages of size M to all MCS's when it receives a `START` message from EMA. 
- **Message Consuming Agent (MCA):** receives and processes the messages sent by SA's. When all messages have been processed, it sends message `DONE` to the EMA. It knows how many messages from each SA should receive. 
- **Experiment Master Agent (EMA):** initializes the experiment sending `START`  message to all SA's and measures the total time of processing all messages by all MCA's. 


## Testing

### Part 1

### Part 2

## Conclusions

## References


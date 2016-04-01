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

### Varying message size (M)

We run the experiment with 3 pairs of agents (SA-MCA) each one in a different container and EMA in the Main Container. The number of messages sent by each SA was 50.

| Message Size (M) 	| Execution time (ms) 	|
|------------------	|---------------------	|
| 10               	| 1577                	|
| 50               	| 1984                	|
| 100              	| 2416                	|
| 500              	| 6208                	|
| 1000             	| 11037               	|
| 5000             	| 49241               	|


### Varying number of messages sent by SA's (N)

We run the experiment with 3 pairs of agents (SA-MCA) each one in a different container and EMA in the Main Container. The message size was 300 ASCII characters.

| Number of messages (N) 	| Execution time (ms) 	|
|------------------------	|---------------------	|
| 10                     	| 904                 	|
| 50                     	| 4352                	|
| 100                    	| 8645                	|
| 500                    	| 43075               	|
| 1000                   	| 85873               	|
| 5000                   	| 427583              	|


### Part 2

## Conclusions

## References


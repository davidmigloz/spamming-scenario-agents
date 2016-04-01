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

![enter image description here](https://raw.githubusercontent.com/davidmigloz/spamming-scenario-agents/master/doc/img/scenario.jpg "Scenario")

Each container hosts a pair of agents SA-MCA. 

EMA is located in the Main Container.

## Testing

#### Environment
The tests was performed in a Toshiba Satellite L850-150 laptop, with an Intel i7-3610 QM processor running at 2.30GHz and 8Gb of RAM.

### Part 1

Three types of tests were performed, each one modifying one variable.

### A) Varying number of containers
We run the experiment sending 50 messages of 300 ASCII characters. Each container contains a pair of agents (SA-MCA) and EMA in the Main Container.

| Number of containers 	| Execution time (ms) 	| Time per message (ms) |
|----------------------	|---------------------	| --------------------- |
| 1                    	| 470                 	| 10.51                 |
| 2                    	| 1867                	| 13.23                 |
| 3                    	| 4355                	| 16.10                 |
| 4                    	| 7753                	| 41.39                 |
| 5                    	| 12067               	| 73.58                 |
| 6                    	| 17862               	| 328.27                |

![enter image description here](https://raw.githubusercontent.com/davidmigloz/spamming-scenario-agents/master/doc/img/1a.jpg)

### B) Varying message size (M)

We run the experiment with 3 pairs of agents (SA-MCA) each one in a different container and EMA in the Main Container. The number of messages sent by each SA was 50.

| Message Size (M) 	| Execution time (ms) 	| Time per message (ms) |
|------------------	|---------------------	| --------------------- |
| 10               	| 1577                	| 30.13                 |
| 50               	| 1984                	| 29.01                 |
| 100              	| 2416                	| 57.63                 |
| 500              	| 6208                	| 287.17                |
| 1000             	| 11037               	| 572.48                |
| 5000             	| 49241               	| 2850.53               |

![enter image description here](https://github.com/davidmigloz/spamming-scenario-agents/blob/master/doc/img/1b.jpg)

### C) Varying number of messages sent by SA's (N)

We run the experiment with 3 pairs of agents (SA-MCA) each one in a different container and EMA in the Main Container. The message size was 300 ASCII characters.

| Number of messages (N) 	| Execution time (ms) 	| Time per message (ms) |
|------------------------	|---------------------	| --------------------- |
| 10                     	| 904                 	| 30.13                 |
| 50                     	| 4352                	| 29.01                 |
| 100                    	| 8645                	| 28.82                 |
| 500                    	| 43075               	| 28.72                 |
| 1000                   	| 85873               	| 28.62                 |
| 5000                   	| 427583              	| 28.50                 |

![enter image description here](https://raw.githubusercontent.com/davidmigloz/spamming-scenario-agents/master/doc/img/1c.jpg)

### Part 2

In this second part, the way that Message Consuming Agent (MCA) consumes the messages was modified: 

It looks for a message from a specific Spammer Agent (SA) first, and when there are none, it processes the remaining messages in FIFO order.

We repeated the same tests with these modification, but giving priority to the messages of the `SA1`.

### A) Varying number of containers
We run the experiment sending 50 messages of 300 ASCII characters. Each container contains a pair of agents (SA-MCA) and EMA in the Main Container.

| Number of containers 	| Execution time (ms) 	| Difference (ms) |
|----------------------	|---------------------	| --------------- |
| 1                    	| 456                 	| -14             |
| 2                    	| 1867                	| 0               |
| 3                    	| 4189                	| -166            |


### B) Varying message size (M)

We run the experiment with 3 pairs of agents (SA-MCA) each one in a different container and EMA in the Main Container. The number of messages sent by each SA was 50.

| Message Size (M) 	| Execution time (ms) 	| Difference (ms) |
|------------------	|---------------------	| --------------- |
| 10               	| 1611                	| +34             |
| 50               	| 1924                	| -60             |
| 100              	| 2386                	| -30             |


### C) Varying number of messages sent by SA's (N)

We run the experiment with 3 pairs of agents (SA-MCA) each one in a different container and EMA in the Main Container. The message size was 300 ASCII characters.

| Number of messages (N) 	| Execution time (ms) 	| Difference (ms) |
|------------------------	|---------------------	| --------------- |
| 10                     	| 868                 	| -36             |
| 50                     	| 4304                	| -48             |
| 100                    	| 8551                	| -94             |

## Conclusions

#### Part 1

- Increasing the number of pairs SA-MCA in new containers increases the time per message in a linear way. 
- Increasing the size of the messages increases the time per message in a linear way.
- Increasing the number of messages in the platform doesn't seem to affect the time per message.

#### Part 2

- Looking for the messages of a specific SA before processing the rest of messages doesn't seem to vary the execution time, at least with the amount of messages and sizes that we tested. 

#### General

- As we scale up the system the processing time increase no longer than in a linear way.

## References

[1]: Krzysztof Chmiel, Maciej Gawinecki, Pawel Kaczmarek, Michal Szymczak, Marcin  Paprzycki, Efficiency of JADE Agent Platform. Scientific Programming vol. 13, no. 2, 2005, 159-172.

[2]: Krzysztof Chmiel, Dominik Tomiak, Maciej Gawinecki, Pawel Kaczmarek, Michal Szymczak, Marcin  Paprzycki, Testing the Efficiency of JADE Agent Platform. In: Proceedings of the ISPDC 2004 Conference, IEEE COomputer Society Press, Los Alamitos, CA, 2004 49-57.

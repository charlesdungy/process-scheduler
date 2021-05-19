# Process Scheduler

This program simulates a scheduler scheduling a set of jobs on a uniprocessor. The program implements three scheduling algorithms: First Come First Serve, Shortest Process Next, and Highest Response Ratio Next.     

## Description

A Java program reads job information from an input file (name, arrival, service). This program creates an output file that holds when each job will begin execution based on each scheduling algorithm. A Python program then reads this file and creates plots for each algorithm, visualizing each job's execution with respect to time.

### How the Algorithms Work

**First Come First Serve** is a straightforward, simple scheduling algorithm. Jobs are executed based on when they arrive. So when a job arrives, it sits in a queue until it is its turn to run. 

**Shortest Process Next** selects jobs based on their expected runtime (service time). So as jobs arrive for execution and the processor is busy, jobs go into a priority queue. Then once the processor is ready, the next job executes.

**Highest Response Ratio Next** selects jobs based on their normalized turnaround time. So as jobs arrive for execution and the processor is busy, they are placed into a list. Once the processor is ready, a calculation on each job in the list is performed. The calculation is the time spent waiting plus total runtime divided by total runtime. The job with the highest turnaround time runs next.

#### FWIW

Excluding the Python implementation, this was a school project. No source files were given.

## Visualizations

![FCFS](https://github.com/charlesdungy/process-scheduler/blob/main/plots/first_come_first_serve.png?raw=true)

![SPN](https://github.com/charlesdungy/process-scheduler/blob/main/plots/shortest_process_next.png?raw=true)

![HRRN](https://github.com/charlesdungy/process-scheduler/blob/main/plots/highest_response_ratio_next.png?raw=true)

## License

MIT

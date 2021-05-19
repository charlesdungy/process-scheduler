import java.util.*;
import java.io.*;
import java.nio.file.Files;

public class Scheduler {
    
    /**
     * Inner Job Class
     */
    private static class Job {
        public char name;
        public int arrivalTime;
        public int serviceTime;

        public Job(char name, int arrivalTime, int serviceTime) {
            this.name = name;
            this.arrivalTime = arrivalTime;
            this.serviceTime = serviceTime;
        }
    } /* end Job */

    /**
     * main function reads file input and calls different scheduling functions
     */
    public static void main(String[] args) throws Exception {
        File file = new File("../../data/raw/jobs.txt");
        if (!file.exists()) {
            System.out.println("Source file " + file.getName() + " does not exists. -- exiting");
            System.exit(0);
        }

        // copy raw data to processed data folder
        File jobsInput = new File("../../data/processed/jobs.txt");
        Files.copy(file.toPath(), jobsInput.toPath());

        // init list of Jobs
        List<Job> jobs = readInput(jobsInput);

        FCFS(jobs, jobsInput);
        SPN(jobs, jobsInput);
        HRRN(jobs, jobsInput);
    } /* end main */

    /**
     * Reads input file, puts Job objects into list
     * 
     * @param inputFile read in main
     * @return list of Jobs objects
     */
    public static List<Job> readInput(File inputFile) {
        List<Job> jobs = new ArrayList<>();
        try (
            Scanner readInput = new Scanner(inputFile);
        ) {
            int idx = 0;
            while (readInput.hasNext()) {
                char name = 0;
                char temp = readInput.next().charAt(0);

                // check first Job name is A
                if (idx == 0)
                    checkFirstLetter(temp);

                // check case of Job names (checkCase exit if false)
                if (checkCase(temp))
                    name = temp;
                
                int arrivalTime = readInput.nextInt();
                int serviceTime = readInput.nextInt();

                // add new Job object to list
                jobs.add(new Job(name, arrivalTime, serviceTime));

                idx += 1;
            }
        } catch (Exception e) {
            System.out.println("Error with reading input file!");
            System.exit(1);
        }

        // ensure arrival times are in order
        checkArrivalTime(jobs);

        // ensure job names are in order
        checkJobOrder(jobs);

        return jobs;
    } /* end readInput */

    /**
     * Ensure Job names are in order
     * 
     * Check if previous Job name is greater than current index Job name
     * Exit if true
     * @param jobs
     */
    public static void checkJobOrder(List<Job> jobs) {
        for (int i = 1; i < jobs.size(); i++) {
            if (jobs.get(i - 1).name > jobs.get(i).name) {
                System.out.println("Job names not in order - exiting");
                System.exit(1);
            }
        }
    } /* end checkJobOrder */

    /**
     * Ensure Job arrival times are in order
     * 
     * Check if previous Job arrival time is greater than current index Job arrival time
     * Exit if true
     * @param jobs
     */
    public static void checkArrivalTime(List<Job> jobs) {
        for (int i = 1; i < jobs.size(); i++) {
            if (jobs.get(i - 1).arrivalTime > jobs.get(i).arrivalTime) {
                System.out.println("Job arrival time not in order - exiting");
                System.exit(1);
            }
        }
    } /* end checkArrivalTime */

    /**
     * Ensure first Job name is letter A
     * 
     * Exit if not
     * @param ch
     */
    public static void checkFirstLetter(char ch) {
        if (ch != 'A') {
            System.out.println("First Job Name Not A - exiting");
            System.exit(1);
        }
    } /* end checkFirstLetter */

    /**
     * Ensure Job name is capital letter [A-Z]
     * 
     * @param ch
     * @return true, exit if false
     */
    public static boolean checkCase(char ch) {
        boolean result = false;
        if (Character.isUpperCase(ch))
            result = true;
        else {
            System.out.println("Job Name not A-Z - exiting");
            System.exit(1);
        }
        return result;
    } /* end checkCase */

    /**
     * First come first serve algorithm
     * 
     * Iterates through Job list, storing beginning execution times in list and appends details to file
     * @param jobs list of Job objects
     */
    public static void FCFS(List<Job> jobs, File jobsInput) throws Exception {
        int time = 0;
        List<Integer> startTimes = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            startTimes.add(time);
            for (int j = 0; j < jobs.get(i).serviceTime; j++) {
                time += 1;
           }
        }
        appendToJobs(jobsInput, startTimes);
    } /* end FCFS */

    /**
     * Shortest process next algorithm
     * 
     * @param jobs list of Job objects
     * @param jobsInput file to append to
     */
    public static void SPN(List <Job> jobs, File jobsInput) throws Exception {
        // create priority queue based on Job object service time
        Queue<Job> jobQueue = new PriorityQueue<>(jobs.size(), new Comparator<Job> () {

            @Override
            public int compare(Job x, Job y) {
                return x.serviceTime > y.serviceTime ? 1 : -1;
            }

        });

        // init hashMap with Job object name and 0 
        Map<Character, Integer> hashMap = initHashMap(jobs);
        jobQueue.offer(jobs.get(0));

        /** 
         * While the queue is not empty, store job beginning execution time in hashmap
         * Observe at time X, Jobs are added to the queue (as they arrive), and an index is incremented
         */

        int idx = 1;
        int time = 0;
        while (!jobQueue.isEmpty()) {
            Job currentJob = jobQueue.remove();
            hashMap.put(currentJob.name, time);
            for (int i = 0; i < currentJob.serviceTime; i++) {
                time += 1;
                if (idx < jobs.size()) {
                    if (time == jobs.get(idx).arrivalTime) {
                        jobQueue.offer(jobs.get(idx));
                        idx += 1;
                    }
                }
            }
        }

        // Storing execution times in order to append to file
        List<Integer> startTimes = new ArrayList<>();
        hashMap.forEach((key, value) -> startTimes.add(value));

        appendToJobs(jobsInput, startTimes);
    } /* end SPN */

    /**
     * High response ratio next algorithm
     * 
     * @param list of Job objects
     * @param jobsInput file to append to
     */
    public static void HRRN(List<Job> jobs, File jobsInput) throws Exception {        
        // to store Job objects
        List<Job> queuedJobs = new ArrayList<>();

        // init hashMap with Job object name and 0
        Map<Character, Integer> hashMap = initHashMap(jobs);
        queuedJobs.add(jobs.get(0));

        /** 
         * While the queue is not empty, store job beginning execution time in hashmap
         * Observe at time X, Jobs are added to the list (as they arrive), and an index is incremented
         * Observe the next job to run is determined in removeQueuedJob(queuedJobs, time), which calculates the normalized turnaround time
         */

        int idx = 1;
        int time = 0;
        while (!queuedJobs.isEmpty()) {
            Job currentJob = removeQueuedJob(queuedJobs, time);
            hashMap.put(currentJob.name, time);
            for (int i = 0; i < currentJob.serviceTime; i++) {
                time += 1;
                if (idx < jobs.size()) {
                    if (time == jobs.get(idx).arrivalTime) {
                        queuedJobs.add(jobs.get(idx));
                        idx += 1;
                    }
                }
            }
        }

        // Storing execution times in order to append to file
        List<Integer> startTimes = new ArrayList<>();
        hashMap.forEach((key, value) -> startTimes.add(value));

        appendToJobs(jobsInput, startTimes);
    } /* end HRRN */

    /**
     * Appends job beginning execution times to jobs.txt
     * 
     * @param jobsInput file to append to
     * @param startTimes list of beginning execution times for each job
     */
    public static void appendToJobs(File jobsInput, List<Integer> startTimes) throws Exception {
        try (
            BufferedReader br = new BufferedReader(new FileReader(jobsInput));
            FileWriter fileWriter = new FileWriter("jobs_append.txt");
            PrintWriter output = new PrintWriter(fileWriter);
        ) {
            int idx = 0;
            String line = "";
            while ((line = br.readLine()) != null) {
                Integer startTime = startTimes.get(idx);
                int i = startTime.intValue();
                line = line + "\t" + i;
                output.println(line);
                idx += 1;
            }

            File file = new File("jobs_append.txt");
            file.renameTo(jobsInput);

        } catch (Exception e) {
            System.out.println("Error with appending data!");
            System.exit(1);
        }
    } /* end appendToJobs */

    /**
     * Calculates normalized turnaround time of Job objects in list
     * 
     * @param queuedJobs list of jobs to be checked
     * @param time current time in process
     * @return next job to run (removed from queuedJobs list)
     */
    public static Job removeQueuedJob(List<Job> queuedJobs, int time) {
        double max = 0.0, r = 0.0;
        int w = 0, s = 0, removeIndex = 0;

        /**
         * Observe that for each Job object, turnaround time is calculated
         * See that the index to remove what jobs is stored based on the max turnaround time
         */
        for (int i = 0; i < queuedJobs.size(); i++) {
            Job job = queuedJobs.get(i);
            w = time - job.arrivalTime;
            s = job.serviceTime;
            r = (w + s) / s;
            if (r > max) {
                max = r;
                removeIndex = i;
            }
        }

        return queuedJobs.remove(removeIndex);
    } /* end removeQueuedJob */

    /**
     * Initializes hashmap with Job object name and 0
     * 
     * @param jobs list of Jobs objects
     * @return hashMap containing Jobs objects name and 0
     */
    public static Map<Character, Integer> initHashMap(List<Job> jobs) {
        Map<Character, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < jobs.size(); i++) {
            hashMap.put(jobs.get(i).name, 0);
        }
        return hashMap;
    } /* end initHashMap */
} /* end Scheduler
import java.io.*;
import java.util.*;

public class OS {
    private static final String programsDir = "programs/";
    public static final String READY = "0", RUNNING = "1", FINISHED = "2";
    private static final int PCB_SIZE = 5, INSTRUCTIONS_SIZE = 100, VARIABLES_SIZE = 100;
    private static final int indexOfStartIndex = 0,indexOfEndIndex = 1,indexOfProcessID = 2,indexOfProcessState = 3,indexOfPC = 4;
    private static final int PROCESS_MEMORY_SIZE = PCB_SIZE + INSTRUCTIONS_SIZE + VARIABLES_SIZE;
    private static final String NULL = "null";
    private int numberOfReadyProcesses;
    private int nextProcessID;
    private String[] memory;

    public OS() {
        numberOfReadyProcesses = 0;
        nextProcessID = 0;
        memory = new String[(int) 1e5];
        Arrays.fill(memory,NULL);
    }

    public void addProgram(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        Vector<String> programInstructions = new Vector<>();
        while (reader.ready())
            programInstructions.add(reader.readLine());

        int startIndex = nextProcessID * PROCESS_MEMORY_SIZE;
        int endIndex = (nextProcessID + 1) * PROCESS_MEMORY_SIZE - 1;
        int processID = nextProcessID++;
        String processState = READY;
        int PC = 0;

        writePCBToMemory(startIndex, endIndex, processID, processState, PC);
        writeInstructionsToMemory(startIndex, programInstructions);

        numberOfReadyProcesses++;
    }

    public void writePCBToMemory(int startIndex, int endIndex, int processID, String processState, int PC) {
        memory[startIndex+ indexOfStartIndex] = startIndex+"";
        memory[startIndex+indexOfEndIndex] = endIndex+"";
        memory[startIndex+indexOfProcessID] = processID+"";
        memory[startIndex+indexOfProcessState] = processState+"";
        memory[startIndex+indexOfPC] = PC+"";
    }

    public void updatePCBInMemory(int startIndex, String processState, int PC) {
        memory[startIndex+indexOfProcessState] = processState+"";
        memory[startIndex+indexOfPC] = PC+"";
    }

    public void writeInstructionsToMemory(int startIndex, Vector<String> programInstructions) {
        for(int i =0,id =startIndex+PCB_SIZE; i<programInstructions.size(); i++,id++){
            memory[id] = programInstructions.get(i);
        }
    }

    public Vector<String> readInstructionsFromMemory(int startIndex) {
        Vector <String> ret = new Vector<>();
        for(int id =startIndex+PCB_SIZE; memory[id]!=NULL; id++){
           ret.add(memory[id]);
        }
        return ret;
    }

    public void addVariableToMemory(int startindex , int processID, String varName, String varValue) {
        int id = startindex+PCB_SIZE+INSTRUCTIONS_SIZE;
        while (memory[id]!=NULL) id++;
        memory[id] = varName+" , "+ varValue;

    }

    public int getVariableIndex(int startindex ,int processID, String varName) {
        int id = startindex+PCB_SIZE+INSTRUCTIONS_SIZE;
        while (id<startindex+PROCESS_MEMORY_SIZE){
            if(memory[id].split(" , ")[0].equals(varName)) return id;
        }
        return -1;
        // if not in memory return -1
    }

    public String getNextInstruction(int startIndex, int PC) {
        return memory[startIndex + PCB_SIZE + PC];
    }

    /**
     * reads the PCB of process and creates new Process Object
     */
    public Process readPCB(int processID) {
        int sI = processID * PROCESS_MEMORY_SIZE;
        return new Process(Integer.parseInt(memory[sI]), Integer.parseInt(memory[sI + 1]),
                Integer.parseInt(memory[sI + 2]), memory[sI + 3], Integer.parseInt(memory[sI]), this);
    }

    public void executeReadyProcesses(int QSize) throws Exception {
        int curProcessID = 0;
        while (numberOfReadyProcesses > 0) {
            Process p = readPCB(curProcessID);
            if (p.getProcessState() == OS.READY) {
                p.run(QSize);
            }
            if (p.getProcessState() == OS.FINISHED) {
                numberOfReadyProcesses--;
            }
            curProcessID = (curProcessID + 1) % nextProcessID;
        }
    }

    public String getVariableOrString(Process process, String varName) {
        int processID = process.getProcessID();
        int startIndex = process.getStartIndex();
        int variableIndex = getVariableIndex(startIndex,processID, varName);
        if (variableIndex == -1)
            return varName;
        return (memory[variableIndex].split(" , "))[1];
    }

    public void assignVariable(Process process, String varName, String varValue) {
        int processID = process.getProcessID();
        int startIndex = process.getStartIndex();
        int variableIndex = getVariableIndex(startIndex,processID, varName);
        if (variableIndex == -1)
            addVariableToMemory(startIndex,processID, varName, varValue);
        else
            memory[variableIndex] = varName + " , " + varValue;
    }

    public void print(String output) {
        System.out.println(output);
    }

    public String input() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();
        return input;
    }

    public static void main(String[] args) throws Exception {
        OS newOS = new OS();
        newOS.addProgram(programsDir + "Program 1.txt");
        newOS.addProgram(programsDir + "Program 2.txt");
        newOS.addProgram(programsDir + "Program 3.txt");
        newOS.executeReadyProcesses(2);
    }
}

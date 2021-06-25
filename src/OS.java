import java.io.*;
import java.util.*;

public class OS {
    private static final String programsDir = "programs/";
    public static final String READY = "0", RUNNING = "1", FINISHED = "2";
    private static final int PCB_SIZE = 5, INSTRUCTIONS_SIZE = 100, VARIABLES_SIZE = 100;
    private static final int PROCESS_MEMORY_SIZE = PCB_SIZE + INSTRUCTIONS_SIZE + VARIABLES_SIZE;
    private int numberOfReadyProcesses;
    private int nextProcessID;
    private String[] memory;

    public OS() {
        numberOfReadyProcesses = 0;
        nextProcessID = 0;
        memory = new String[(int) 1e5];
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

    }

    public void updatePCBInMemory(int startIndex, String processState, int PC) {

    }

    public void writeInstructionsToMemory(int startIndex, Vector<String> programInstructions) {

    }

    public Vector<String> readInstructionsFromMemory(int startIndex) {

    }

    public void addVariableToMemory(int processID, String varName, String varValue) {

    }

    public int getVariableIndex(int processID, String varName) {
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
        int variableIndex = getVariableIndex(processID, varName);
        if (variableIndex == -1)
            return varName;
        return (memory[variableIndex].split(" , "))[1];
    }

    public void assignVariable(Process process, String varName, String varValue) {
        int processID = process.getProcessID();
        int variableIndex = getVariableIndex(processID, varName);
        if (variableIndex == -1)
            addVariableToMemory(processID, varName, varValue);
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

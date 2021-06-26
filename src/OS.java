import java.io.*;
import java.util.*;

public class OS {
    private static final String programsDir = "programs/";
    public static final String READY = "0", RUNNING = "1", FINISHED = "2";
    private static final int PCB_SIZE = 5, INSTRUCTIONS_SIZE = 100, VARIABLES_SIZE = 100;
    private static final int indexOfStartIndex = 0, indexOfEndIndex = 1, indexOfProcessID = 2, indexOfProcessState = 3, indexOfPC = 4;
    private static final int PROCESS_MEMORY_SIZE = PCB_SIZE + INSTRUCTIONS_SIZE + VARIABLES_SIZE;
    public static final String NULL = "null";
    private int numberOfReadyProcesses;
    private int nextProcessID;
    private final String[] memory;

    public OS() {
        numberOfReadyProcesses = 0;
        nextProcessID = 0;
        memory = new String[(int) 1e5];
        Arrays.fill(memory, NULL);
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
        memory[startIndex + indexOfStartIndex] = startIndex + "";
        memory[startIndex + indexOfEndIndex] = endIndex + "";
        memory[startIndex + indexOfProcessID] = processID + "";
        memory[startIndex + indexOfProcessState] = processState + "";
        memory[startIndex + indexOfPC] = PC + "";
        for (int i = startIndex; i<startIndex+ PCB_SIZE; i++) printWriteMemory(i);
    }

    public void updatePCBInMemory(int startIndex, String processState, int PC) {
        memory[startIndex + indexOfProcessState] = processState + "";
        memory[startIndex + indexOfPC] = PC + "";
        printWriteMemory(startIndex + indexOfProcessState);
        printWriteMemory(startIndex + indexOfPC);
    }

    public void writeInstructionsToMemory(int startIndex, Vector<String> programInstructions) {
        for (int i = 0, id = startIndex + PCB_SIZE; i < programInstructions.size(); i++, id++) {
            memory[id] = programInstructions.get(i);
            printWriteMemory(id);

        }
    }


    public void addVariableToMemory(int startindex, String varName, String varValue) {
        int id = startindex + PCB_SIZE + INSTRUCTIONS_SIZE;
        while (!memory[id].equals(NULL)) id++;
        memory[id] = varName + " , " + varValue;
        printWriteMemory(id);

    }

    public int getVariableIndex(int startindex, int processID, String varName) {
        int id = startindex + PCB_SIZE + INSTRUCTIONS_SIZE;
        while (id < startindex + PROCESS_MEMORY_SIZE) {
            if (memory[id].split(" , ")[0].equals(varName)) return id;
            id++;
        }
        return -1;
        // if not in memory return -1
    }

    public String getNextInstruction(int startIndex, int PC) {
        printReadMemory(startIndex + PCB_SIZE + PC);
        return memory[startIndex + PCB_SIZE + PC];
    }

    /**
     * reads the PCB of process and creates new Process Object
     */
    public Process readPCB(int processID) {
        int sI = processID * PROCESS_MEMORY_SIZE;
        for (int i = sI; i<sI+PCB_SIZE; i++) printReadMemory(i);
        return new Process(Integer.parseInt(memory[sI]), Integer.parseInt(memory[sI + 1]),
                Integer.parseInt(memory[sI + 2]), memory[sI + 3], Integer.parseInt(memory[sI + 4]), this);
    }

    public void executeReadyProcesses(int QSize) throws Exception {
        int curProcessID = 0;
        while (numberOfReadyProcesses > 0) {
            Process p = readPCB(curProcessID);
            if (p.getProcessState().equals(OS.READY)) {
                System.out.printf("The scheduler chooses the program : %d\n",curProcessID+1);
                System.out.println("-----------------------------------------------------------------------------------------");
                System.out.println("-----------------------------------------------------------------------------------------");
                System.out.println();
                p.run(QSize);
                if (p.getProcessState().equals(OS.FINISHED)) {
                    System.out.printf("The program %d ends and it took %d quanta \n",curProcessID+1,(p.getPC()+1)/2);
                    System.out.println("-----------------------------------------------------------------------------------------");
                    System.out.println("-----------------------------------------------------------------------------------------");
                    System.out.println();
                    numberOfReadyProcesses--;
                }
            }
            curProcessID = (curProcessID + 1) % nextProcessID;
        }
    }

    public String getVariableOrString(Process process, String varName) {
        int processID = process.getProcessID();
        int startIndex = process.getStartIndex();
        int variableIndex = getVariableIndex(startIndex, processID, varName);
        if (variableIndex == -1)
            return varName;
        printReadMemory(variableIndex);
        return (memory[variableIndex].split(" , "))[1];
    }

    public void assignVariable(Process process, String varName, String varValue) {
        int processID = process.getProcessID();
        int startIndex = process.getStartIndex();
        int variableIndex = getVariableIndex(startIndex, processID, varName);
        if (variableIndex == -1)
            addVariableToMemory(startIndex, varName, varValue);
        else{
            memory[variableIndex] = varName + " , " + varValue;
            printWriteMemory(variableIndex);
        }
    }

    public void print(String output) {
        System.out.println("*****************************************");
        System.out.println("****  "+output+"  ****");
        System.out.println("*****************************************");
        System.out.println();

    }

    public String input() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    void printReadMemory(int index){
        System.out.printf("The memory is accessed at index %d : the word which is read is : %s\n",index , memory[index]);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }
    void printWriteMemory(int index){
        System.out.printf("The memory is accessed at index %d : the word which is written is : %s\n",index , memory[index]);
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        OS newOS = new OS();
        newOS.addProgram(programsDir + "Program 1.txt");
        newOS.addProgram(programsDir + "Program 2.txt");
        newOS.addProgram(programsDir + "Program 3.txt");
        newOS.executeReadyProcesses(2);
    }
}

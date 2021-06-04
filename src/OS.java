import java.io.*;
import java.util.*;

public class OS {
    private static final String programsDir = "programs/";
    private Vector<Process> currentProcesses;

    public OS() {
        currentProcesses = new Vector<>();
    }

    public void executeProgram(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        Vector<String> programInstructions = new Vector<>();
        while(reader.ready())
            programInstructions.add(reader.readLine());

        Process newProcess = new Process(programInstructions, this);
        currentProcesses.add(newProcess);

        newProcess.run();

        currentProcesses.remove(newProcess);
    }

    public String getVariableOrString(Process process, String varName) {
        if(process.getProcessVariables().containsKey(varName))
            return process.getProcessVariables().get(varName);
        return varName;
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
        newOS.executeProgram(programsDir + "Program 1.txt");
        newOS.executeProgram(programsDir + "Program 2.txt");
        newOS.executeProgram(programsDir + "Program 3.txt");
    }
}

import java.util.*;
import java.io.*;

public class Process {
    private Vector<String> processInstructions;
    private HashMap<String, String> processVariables;
    private OS parentOS;

    public Process(Vector<String> processInstructions, OS parentOS) {
        this.processInstructions = processInstructions;
        this.parentOS = parentOS;
        this.processVariables = new HashMap<>();
    }

    public HashMap<String, String> getProcessVariables() {
        return processVariables;
    }

    public void run() throws Exception {
        for(String instruction : processInstructions) {
            String[] instructionParts = instruction.split(" ");
            if(instructionParts[0].equals("print")) {
                String output = getVariableOrString(instructionParts[1]);
                print(output);
            } else {
                // rest here
            }
        }
    }

    public String getVariableOrString(String varName) {
        return parentOS.getVariableOrString(this, varName);
    }

    public void print(String output) {
        parentOS.print(output);
    }

    public String input() throws Exception {
        return parentOS.input();
    }
}

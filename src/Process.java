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
        for (String instruction : processInstructions) {
            String[] instructionParts = instruction.split(" ");
            if (instructionParts[0].equals("print")) {
                String output = getVariableOrString(instructionParts[1]);
                print(output);
            } else if (instructionParts[0].equals("assign")) {
                String variableName = instructionParts[1];
                String source = instructionParts[2];
                String fileName = source.equals("readFile") ? instructionParts[3] : null;
                assign(variableName, source, fileName);
            }
        }
    }

    private void assign(String variableName, String source, String fileName) throws Exception {
        if (source.equals("input")) {
            processVariables.put(variableName, input());
        } else if (source.equals("readFile")) {
            processVariables.put(variableName, readFile(fileName));
        }
    }

    private String readFile(String fileName) {
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

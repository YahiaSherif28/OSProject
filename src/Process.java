import java.util.*;
import java.io.*;

public class Process {
    private Vector<String> processInstructions;
    private int processID;
    private OS parentOS;

    public Process(int processID, Vector<String> processInstructions, OS parentOS) {
        this.processID = processID;
        this.processInstructions = processInstructions;
        this.parentOS = parentOS;
    }

    public int getProcessID() {
        return processID;
    }

    public void run() throws Exception {
        for (int i = 0; i < processInstructions.size(); i++) {
            String instruction = processInstructions.get(i);
            String[] instructionParts = instruction.split(" ");
            if (instructionParts[0].equals("print")) {
                String output = getVariableOrString(instructionParts[1]);
                print(output);
            } else if (instructionParts[0].equals("assign")) {
                String variableName = instructionParts[1];
                String source = instructionParts[2];
                String fileName = source.equals("readFile") ? instructionParts[3] : null;
                assign(variableName, source, fileName);
            } else if (instructionParts[0].equals("writeFile")) {
                String fileName = instructionParts[1];
                String data = instructionParts[2];
                writeFile(fileName, data);
            } else if (instructionParts[0].equals("add")) {
                String a = instructionParts[1];
                String b = instructionParts[2];
                add(a, b);
            }
        }
    }

    private void assign(String variableName, String source, String fileName) throws Exception {
        if (source.equals("input")) {
            parentOS.assignVariable(this, variableName, input());
        } else if (source.equals("readFile")) {
            parentOS.assignVariable(this, variableName, readFile(fileName));
        }
    }

    private String readFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(parentOS.getVariableOrString(this, fileName)));
            String input = reader.readLine();
            return input;
        } catch (Exception e) {
            System.out.println("File Not Found");
            return null;
        }
    }

    private void writeFile(String fileName, String data) {
        try {
            FileWriter fw = new FileWriter(parentOS.getVariableOrString(this, fileName));
            fw.write(parentOS.getVariableOrString(this, data));
            fw.close();
        } catch (Exception e) {
            System.out.println("File Not Found");
            return;
        }
    }

    private void add(String a, String b) {
        int vala = Integer.parseInt(parentOS.getVariableOrString(this, a));
        int valb = Integer.parseInt(parentOS.getVariableOrString(this, b));
        int sum = vala + valb;
        parentOS.assignVariable(this, a, sum + "");
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

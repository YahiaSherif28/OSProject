import java.util.*;
import java.io.*;

public class Process {
    private Vector<String> processInstructions;
    private OS parentOS;

    public Process(Vector<String> processInstructions, OS parentOS) {
        this.processInstructions = processInstructions;
        this.parentOS = parentOS;
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
            else if (instructionParts[0].equals("writeFile")) {
                String fileName = instructionParts[1];
                String data = instructionParts[2];
                writeFile(fileName,data);
            }
            else if (instructionParts[0].equals("add")) {
                String a = instructionParts[1];
                String b = instructionParts[2];
                add(a,b);
            }
        }
    }

    private void assign(String variableName, String source, String fileName) throws Exception {
        if (source.equals("input")) {
            parentOS.variables.put(variableName, input());
        } else if (source.equals("readFile")) {
            parentOS.variables.put(variableName, readFile(fileName));
        }
    }

    private String readFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(parentOS.variables.getOrDefault(fileName,"")));
            String input = reader.readLine();
            return input;
        }
        catch (Exception e){
            System.out.println("File Not Found");
            return null;
        }
    }

    private void writeFile(String fileName , String data) {
        try {
          FileWriter fw = new FileWriter(parentOS.variables.getOrDefault(fileName,""));
          fw.write(parentOS.variables.getOrDefault(data,"NODATA"));
          fw.close();

        }
        catch (Exception e){
            System.out.println("File Not Found");
            return ;
        }
    }

    private void add (String a , String b){
        int vala = Integer.parseInt(parentOS.variables.getOrDefault(a,"0"));
        int valb = Integer.parseInt(parentOS.variables.getOrDefault(b,"0"));
        int sum  = vala+valb;
        parentOS.variables.put(a,sum+"");
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

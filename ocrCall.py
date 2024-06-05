import subprocess
import os

def ocrFromJava(file_path):

    if not os.path.exists('target/classes'):
        # Define Maven compile command
        compile_command = ['mvn', 'compile']

        # Compile Java code using Maven
        compile_process = subprocess.run(compile_command, capture_output=True, text=True)

        # Check if compilation was successful
        if compile_process.returncode == 0:
            print("Java code compiled successfully.")
        else:
            print("Error: Java code compilation failed.")
            print(compile_process.stderr)  # Print compilation error output

    exec_command = ['mvn', 'exec:java', '-Dexec.mainClass=io.github.jonathanlink.test',f'-Dexec.args="{file_path}"']

    exec_process = subprocess.run(exec_command, capture_output=True, text=True)

    # Check if execution was successful
    if exec_process.returncode == 0:
        print("Java application executed successfully.")
        print(exec_process.stdout)  # Print output of Java application
    else:
        print("Error: Java application execution failed.")
        print(exec_process.stderr)  # Print error output
       
       
folder = "/home/rahul/textstripper/PDFLayoutTextStripper/samples/"

file_lst = os.listdir(folder)
        
for file in file_lst:
    ocrFromJava(folder+file)
    
                        
                    

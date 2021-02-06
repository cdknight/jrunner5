package tv.ramesh;
import org.joor.Reflect;
import java.lang.reflect.ReflectPermission
import java.security.Permission


class ReflectionUtil {

    fun evalProblemSolution(inputFunction: String, inputFunctionName: String, solutionFunction: String, functionArgs: Array<String>): tv.ramesh.Response {

        var runInputs = "Object inputFnOutput = null; Object solutionFnOutput = null;"; // Instantiate objects earlier
        for (arg in functionArgs) {
            runInputs += "inputFnOutput = $inputFunctionName($arg);\n" // Try user input function
            runInputs += "solutionFnOutput = solution($arg);\n" // Try solution function
            runInputs += "matches.add(inputFnOutput.equals(solutionFnOutput));\n" // Compare both of them
            runInputs += "inputFnOutputs.add(inputFnOutput.toString());\n" // Add to input function output list
            runInputs += "solutionFnOutputs.add(solutionFnOutput.toString());\n\n" // Add to solution function output list
        }

        val java = """
package sandboxed;
import java.util.ArrayList;

public class JavaWrappedClass {
    public tv.ramesh.Response runProblem() {
        ArrayList<String> inputFnOutputs = new ArrayList<>();
        ArrayList<String> solutionFnOutputs = new ArrayList<>();
        ArrayList<Boolean> matches = new ArrayList<>();

        $runInputs


        return new tv.ramesh.Response(inputFnOutputs, solutionFnOutputs, matches);
    }
    $inputFunction
    $solutionFunction
}
""".trimIndent();

        // println("DEBUG! Compiled java code is \n$java")


        val ref = Reflect.compile("sandboxed.JavaWrappedClass", java).create();
        val classInst: Any = ref.get()

        return classInst.javaClass.getMethod("runProblem").invoke(classInst) as tv.ramesh.Response;

    }
}
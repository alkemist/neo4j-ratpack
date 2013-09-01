package groovywebconsole

import com.jayway.restassured.path.json.JsonPath
//import org.ratpackframework.test.ScriptAppSpec

class ScriptExecutionSpec { // extends ScriptAppSpec {

    def "captures output"() {
        when:
        request.param "script", "println 'hello world'"
        post "execute"

        then:
        with(new JsonResponse()) {
            outputText == "hello world\n"
            executionResult == ""
            stacktraceText == ""
        }
    }

    def "captures result"() {
        when:
        request.param "script", "1\n3"
        post "execute"

        then:
        with(new JsonResponse()) {
            outputText == ""
            executionResult == "3"
            stacktraceText == ""
        }
    }

    def "captures errors"() {
        when:
        request.param "script", "throw new Exception('bang')"
        post "execute"

        then:
        with(new JsonResponse()) {
            outputText == ""
            executionResult == ""
            stacktraceText.contains "java.lang.Exception"
            stacktraceText.contains "bang"
        }
    }

    class JsonResponse {
        private JsonPath path = ScriptExecutionSpec.this.response.jsonPath()

        String getOutputText() {
            path.outputText
        }

        String getExecutionResult() {
            path.executionResult
        }

        String getStacktraceText() {
            path.stacktraceText
        }
    }
}

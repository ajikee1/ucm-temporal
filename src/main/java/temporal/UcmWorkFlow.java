package temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

@WorkflowInterface
public interface UcmWorkFlow {

    @WorkflowMethod
    void initiateWorkFlow(List<String> l);;
}

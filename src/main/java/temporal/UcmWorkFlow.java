package temporal;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface UcmWorkFlow {

    @WorkflowMethod
    void initiateWorkFlow();
}

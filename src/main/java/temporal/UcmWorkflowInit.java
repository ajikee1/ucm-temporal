package temporal;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

public class UcmWorkflowInit {

    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue("JENKINS_TASK_QUEUE").build();
        UcmWorkFlow workflow = client.newWorkflowStub(UcmWorkFlow.class, options);

        WorkflowExecution we = WorkflowClient.start(workflow::initiateWorkFlow);

    }

}

package temporal;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.util.ArrayList;
import java.util.List;

public class UcmWorkflowInit {

    public static void main(String[] args) {
        String issueId = "DIS-1";

        WorkflowServiceStubsOptions wfOptions = WorkflowServiceStubsOptions.newBuilder().setTarget("63.141.224.130:7233").build();

        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(wfOptions);
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder().setWorkflowId(issueId).setTaskQueue("JENKINS_TASK_QUEUE").build();
        UcmWorkFlow workflow = client.newWorkflowStub(UcmWorkFlow.class, options);

        List<String> jobList = new ArrayList<>();
        jobList.add("nodeTest");
        jobList.add("temporal_demo");

        WorkflowExecution we = WorkflowClient.start(workflow::initiateWorkFlow, jobList, issueId);
    }

}

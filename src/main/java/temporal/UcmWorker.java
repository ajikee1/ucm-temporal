package temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import temporal.Jenkins.JenkinsActivityImp;
import temporal.Jira.JiraActivityImp;

public class UcmWorker {

    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("JENKINS_TASK_QUEUE");

        worker.registerWorkflowImplementationTypes(UcmWorkFlowImp.class);
        worker.registerActivitiesImplementations(new JenkinsActivityImp(), new JiraActivityImp());
        factory.start();
    }
}

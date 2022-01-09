# ucm-temporal-Demo-Steps

1. Run temporal on remote server:
```bash
cd /home/customer/temporal/docker_code/docker-compose
docker-compose -f docker-compose-mysql.yml up
```
Temporal running at: http://XX.XX.XX.XX:8088


2. Start Jenkins server on remote server: 
```bash
sudo systemctl start jenkins
```
Jenkins running at: http://XX.XX.XX.XX:8080


3. Pull the latest code, build, and run the services on remote server:

`cd /home/customer/code/ucm-temporal/`
- `git fetch`
- `git pull`
- `mvn clean install`
- `mvn exec:java@spring-bootApp` : Runs the SpringBoot App
- `mvn exec:java@temporal_worker` : Runs the Temporal Worker


4. Trigger UcmWorkflow
- Request Type: `POST`
- Request EndPoint: `http://XX.XX.XX.XX:9090/initiateWorkFlow/`
- Request body:

```json
{
    "jiraTicketId": "DIS-1",
    "jobList": [
        {
            "jobName": "Jenkins_job_one",
            "buildNumber": "1"
        },
        {
            "jobName": "Jenkins_job_two",
            "buildNumber": "2"
        },
        {
            "jobName": "Jenkins_job_three",
            "buildNumber": "2"
        },
        {
            "jobName": "Jenkins_job_four",
            "buildNumber": "2"
        },
        {
            "jobName": "Jenkins_job_five",
            "buildNumber": "2"
        },
                {
            "jobName": "jenkins_job_six_fail",
            "buildNumber": "5"
        }
    ]
}
```


***JIRA ticket that triggers the Jenkins build: https://ajirh.atlassian.net/browse/DIS-1 ***

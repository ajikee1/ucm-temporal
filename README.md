# ucm-temporal

1. Run temporal on remote server:
```bash
cd /home/customer/temporal/docker_code/docker-compose
docker-compose -f docker-compose-mysql.yml up
```
Temporal running at: http://XX.XX.XX.XX:8088

2. Start Jenkins server on remote host: 
```bash
sudo systemctl start jenkins
```
Jenkins running at: http://XX.XX.XX.XX:8080

3. Clone this git repo to local
```bash
https://github.com/ajikee1/ucm-temporal.git
```
5. Run 'SpringApp.java' and 'UcmWorker.java'

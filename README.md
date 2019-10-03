# test_task_october
Autotests for API methods of vk.com social network

USAGE

1. Create API token via implicit flow (https://vk.com/dev/implicit_flow_user)

2. Run all tests - mvn mvn clean test -Dvk.testUser.userId=testuserid -Dvk.testUser.accessKey=acces_key

To run specified test group use variable -DtestGroup. Possible values - likes.add, likes.delete, likes.get, likes.isLiked

Allure report file will be generated after test run, located in \allure-reports

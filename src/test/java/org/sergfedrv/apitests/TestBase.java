package org.sergfedrv.apitests;

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.sergfedrv.testdata.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Test
public class TestBase {

    protected VkApiClient vk;
    protected UserActor userActor;
    protected Logger logger = LoggerFactory.getLogger(TestBase.class);
    @BeforeClass(alwaysRun = true)
    @Step("Create vk client and user actor objects")
    public void beforeClass(){
        userActor = getUserActor();
        vk = createApiClient();
    }

    private VkApiClient createApiClient(){
        logger.info("Creation of new VK API client");
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient);
    }

    private UserActor getUserActor(){
        logger.info("Creation of test user");
        int userId = Integer.parseInt(System.getProperty("vk.testUser.userId"));
        String userAccessToken = System.getProperty("vk.testUser.accessKey");
        return new UserActor(userId, userAccessToken);
    }

    public <T> T performRequest(AbstractQueryBuilder query) throws ClientException, ApiException, InterruptedException {
        logger.info("Sending request without error validation");
        Thread.sleep(350); //sleep to avoid ToManyRequests error while running the whole suite
        Object response = query.execute();
        logger.info("Response successfully returned");
        return (T) response;
    }

    public <T> T testRequest(AbstractQueryBuilder query, TestData testData) {
        logger.info("Sending test request via VK API");
        Allure.step("Send test request to VK ");
        Object response = null;
        try {
            Thread.sleep(350); //sleep to avoid ToManyRequests error while running the whole suite
            response = query.execute();
            logger.info("Response successfully returned");
        } catch (ApiException e) {
            if (testData.getExpectedErrorMessage() != null) {
                logger.info("Exception returned. Comparing returned exception message with expected");
                Allure.step("Check that returned exception message is equal to expected");
                Allure.addAttachment("Actual error message", e.getMessage());
                Assert.assertEquals(e.getMessage(), testData.getExpectedErrorMessage(), "Expected and actual error messages are different");
                logger.info("Check passed");
                return null;
            } else{
                logger.error("Unexpected API exception returned");
                Assert.fail("Unexpected API exception occurred. " + e.getMessage());
            }
        } catch (ClientException e) {
            Assert.fail("Unexpected client exception occurred. " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Allure.addAttachment("Returned response", response.toString());

        return (T) response;
    }
}

package org.sergfedrv.apitests;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.responses.GetListResponse;
import com.vk.api.sdk.queries.likes.LikesGetListQuery;
import com.vk.api.sdk.queries.likes.LikesType;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.sergfedrv.testdata.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GetLikesListTest extends TestBase {

    @BeforeClass(groups = {"likes.get"})
    @Step("Like post of opened user.")
    public void addLikes() throws ClientException, ApiException, InterruptedException {
        logger.info("GetLikesTest beforeClass");
        performRequest(vk.likes().add(userActor, LikesType.POST, TestData.OPENED_POST_ID).ownerId(TestData.OPENED_PROFILE_USER_ID));
    }

    @DataProvider
    public Object[][] getLikesTestDataProvider() {
        return new Object[][]{
                {
                        TestData.builder()
                                .withDescription("Check that when user like item, he or she exist in getLikesList of this item")
                                .withItemId(TestData.OPENED_POST_ID)
                                .withLikesType(LikesType.POST)
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .build()
                },
                {
                        TestData.builder()
                                .withDescription("Try to get likes list from photo, that closed by privacy settings")
                                .withErrorMessage("Access denied (15): Access denied: access restriction")
                                .withItemId(TestData.CLOSED_PHOTO_ID)
                                .withLikesType(LikesType.PHOTO)
                                .withOwnerId(TestData.PRIVATE_PROFILE_USER_ID)
                                .build()
                }
        };
    }

    @Test(dataProvider = "getLikesTestDataProvider", groups = {"likes.get"})
    @Description("Check 'likes.get' API method")
    public void getLikesTest(TestData testData) {
        Allure.description(testData.getTestDescription());
        LikesGetListQuery testQuery = createQuery(testData);
        GetListResponse response = testRequest(testQuery, testData);
        if (response != null) {
            Assert.assertTrue(response.getItems().contains(userActor.getId()), "If test user like something, he or she should be in getLikesList query");
        }
    }

    private LikesGetListQuery createQuery(TestData testData) {
        Allure.step("Create test query according to passed test data");
        LikesGetListQuery testQuery = vk.likes().getList(userActor, testData.getLikesType());
        if (testData.getOwnerId() != null) testQuery.ownerId(testData.getOwnerId());
        if (testData.getItemId() != null) testQuery.itemId(testData.getItemId());
        if (testData.getLikesGetListFilterValue() != null) testQuery.filter(testData.getLikesGetListFilterValue());
        return testQuery;
    }
}

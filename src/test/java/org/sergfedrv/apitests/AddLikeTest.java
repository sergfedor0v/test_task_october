package org.sergfedrv.apitests;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.base.BoolInt;
import com.vk.api.sdk.objects.likes.responses.AddResponse;
import com.vk.api.sdk.objects.likes.responses.IsLikedResponse;
import com.vk.api.sdk.queries.likes.LikesAddQuery;
import com.vk.api.sdk.queries.likes.LikesType;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.sergfedrv.testdata.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

@Test
public class AddLikeTest extends TestBase {


    @DataProvider
    public Object[][] addLikesTestDataProvider() {
        return new Object[][]{
                {
                        TestData.builder()
                                .withLikesType(LikesType.POST)
                                .withItemId(TestData.OPENED_POST_ID)
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .withDescription("Check that current user can like post of other user")
                                .build(),
                },
                {
                        TestData.builder()
                                .withLikesType(LikesType.PHOTO)
                                .withItemId(TestData.OPENED_POST_ID)
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .withDescription("Pass wrong LikesType to query and validate error message")
                                .withErrorMessage("One of the parameters specified was missing or invalid (100): One of the parameters specified was missing or invalid: object not found")
                                .build()
                },
                {
                        TestData.builder()
                                .withLikesType(LikesType.POST)
                                .withItemId(-1)
                                .withDescription("Pass non-existing itemId to query and validate message id")
                                .withErrorMessage("One of the parameters specified was missing or invalid (100): One of the parameters specified was missing or invalid: item_id should be positive")
                                .build()
                },
                {
                        TestData.builder()
                                .withLikesType(LikesType.PHOTO)
                                .withItemId(TestData.CLOSED_PHOTO_ID)
                                .withOwnerId(TestData.PRIVATE_PROFILE_USER_ID)
                                .withDescription("Try to like photo that closed by privacy settings. Request should fail with expected error message")
                                .withErrorMessage("Access denied (15): Access denied: this profile is private")
                                .build()
                }
        };
    }

    @Test(dataProvider = "addLikesTestDataProvider", groups = {"likes.add"})
    @Description("Check likes.add method")
    public void addLikesTest(TestData testData) throws Exception {
        Allure.description(testData.getTestDescription());
        LikesAddQuery testQuery = createQuery(testData);
        AddResponse response = testRequest(testQuery, testData);
        if (response != null) {
            IsLikedResponse isLiked = performRequest(vk.likes()
                    .isLiked(userActor, testData.getLikesType(), testData.getItemId())
                    .ownerId(testData.getOwnerId()));
            Assert.assertTrue(isLiked.isLiked(), "Test user should like it!");
        }
    }

    private LikesAddQuery createQuery(TestData testData) {
        Allure.step("Create test query according to passed test data");
        LikesAddQuery testQuery = vk.likes().add(userActor, testData.getLikesType(), testData.getItemId());
        if (testData.getOwnerId() != null) testQuery.ownerId(testData.getOwnerId());
        return testQuery;
    }
}

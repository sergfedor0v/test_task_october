package org.sergfedrv.apitests;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.responses.DeleteResponse;
import com.vk.api.sdk.objects.likes.responses.IsLikedResponse;
import com.vk.api.sdk.queries.likes.LikesDeleteQuery;
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
public class DeleteLikeTest extends TestBase {

    @BeforeClass(groups = {"likes.delete"})
    @Step("Like opened user post just to delete this like in test")
    public void addLikes() throws ClientException, ApiException, InterruptedException {
        performRequest(vk.likes().add(userActor, LikesType.POST, TestData.OPENED_POST_ID).ownerId(TestData.OPENED_PROFILE_USER_ID));
    }

    @DataProvider
    public Object[][] deleteLikeTestDataProvider() {
        return new Object[][]{
                {
                    TestData.builder()
                        .withDescription("Delete like from post of user with userId " + TestData.OPENED_PROFILE_USER_ID)
                        .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                        .withLikesType(LikesType.POST)
                        .withItemId(TestData.OPENED_POST_ID)
                        .build()
                },
                {
                        TestData.builder()
                                .withDescription("Delete like from post of user with userId " + TestData.OPENED_PROFILE_USER_ID + " once again")
                                .withErrorMessage("Access denied (15): Access denied")
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .withLikesType(LikesType.POST)
                                .withItemId(TestData.OPENED_POST_ID)
                                .build()
                },
                {
                        TestData.builder()
                                .withDescription("Pass wrong LikesType to query and validate error message")
                                .withErrorMessage("One of the parameters specified was missing or invalid (100): One of the parameters specified was missing or invalid: object not found")
                                //todo ask why actual message is Access Denied in this case?
                                .withItemId(TestData.OPENED_POST_ID)
                                .withLikesType(LikesType.PHOTO_COMMENT)
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .build()
                },
                {
                        TestData.builder()
                                .withDescription("Pass invalid itemId to query and validate error message")
                        .withErrorMessage("One of the parameters specified was missing or invalid (100): One of the parameters specified was missing or invalid: item_id should be positive")
                        .withLikesType(LikesType.PHOTO_COMMENT)
                        .withItemId(-1)
                        .build()
                },
                {
                    TestData.builder()
                        .withDescription("Try to delete like from photo that closed by privacy settings. Request should fail with expected error message")
                            .withErrorMessage("Access denied (15): Access denied: this profile is private")
                        .withItemId(TestData.CLOSED_PHOTO_ID)
                        .withOwnerId(TestData.PRIVATE_PROFILE_USER_ID)
                        .withLikesType(LikesType.PHOTO)
                        .build()

                }
        };
    }

    @Test(dataProvider = "deleteLikeTestDataProvider", groups = {"likes.delete"})
    @Description("Check 'likes.delete' API method")
    public void deleteLikeTest(TestData testData) throws Exception {
        Allure.description(testData.getTestDescription());
        LikesDeleteQuery testQuery = createQuery(testData);
        DeleteResponse response = testRequest(testQuery, testData);
        if (response != null) {
            IsLikedResponse isLiked = performRequest(vk.likes()
                    .isLiked(userActor, testData.getLikesType(), testData.getItemId())
                    .ownerId(testData.getOwnerId()));
            Assert.assertFalse(isLiked.isLiked(), "Test user should NOT like it!");
        }
    }

    private LikesDeleteQuery createQuery(TestData testData) {
        Allure.step("Create test query according to passed test data");
        LikesDeleteQuery testQuery = vk.likes().delete(userActor, testData.getLikesType(), testData.getItemId());
        if (testData.getOwnerId() != null) testQuery.ownerId(testData.getOwnerId());
        return testQuery;
    }
}

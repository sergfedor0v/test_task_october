package org.sergfedrv.apitests;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.base.BoolInt;
import com.vk.api.sdk.objects.likes.responses.IsLikedResponse;
import com.vk.api.sdk.queries.likes.LikesIsLikedQuery;
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
public class IsLikedTest extends TestBase {

    private Integer testNoteItemId;

    @BeforeClass(groups = {"likes.isLiked"})
    @Step("Like post of opened user")
    public void addLikes() throws ClientException, ApiException, InterruptedException {
        logger.info("IsLikedTest beforeClass");
        performRequest(vk.likes().add(userActor, LikesType.POST, TestData.OPENED_POST_ID).ownerId(TestData.OPENED_PROFILE_USER_ID));
    }

    @DataProvider
    public Object[][] isLikedTestDataProvider() {
        return new Object[][]{
                {
                        TestData.builder()
                                .withDescription("Check that if user like post, isLiked request should return true if request parameter userId == testUserId")
                                .withItemId(TestData.OPENED_POST_ID)
                                .withLikesType(LikesType.POST)
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .build(),
                        true
                },
                {
                        TestData.builder()
                                .withDescription("Check that if user does not like post, isLiked request should return false if request parameter userId == testUserId")
                                .withItemId(TestData.OPENED_PHOTO_ID)
                                .withLikesType(LikesType.PHOTO)
                                .withOwnerId(TestData.OPENED_PROFILE_USER_ID)
                                .build(),
                        false
                },
                {
                        TestData.builder()
                                .withDescription("Check that you can not get isLiked for private profile")
                                .withErrorMessage("Access denied (15): Access denied: this profile is private")
                                .withItemId(TestData.OPENED_POST_ID)
                                .withLikesType(LikesType.NOTE)
                                .withUserId(TestData.PRIVATE_PROFILE_USER_ID)
                                .build(),
                        false
                }
        };
    }

    @Test(dataProvider = "isLikedTestDataProvider", groups = {"likes.isLiked"})
    @Description("Check 'likes.isLiked' API method")
    public void isLikedTest(TestData testData, boolean isLiked) {
        Allure.description(testData.getTestDescription());
        LikesIsLikedQuery testQuery = createQuery(testData);
        IsLikedResponse response = testRequest(testQuery, testData);
        if (response != null) {
            String errorMessage = isLiked ? "User like item " + testData.getItemId() + " , but isLiked request returned false" :
                    "User does not like item " + testData.getItemId() + " , but isLiked request returned true";
            Assert.assertEquals(response.isLiked(), isLiked, errorMessage);
        }
    }

    private LikesIsLikedQuery createQuery(TestData testData) {
        Allure.step("Create test query according to test data");
        LikesIsLikedQuery query = vk.likes().isLiked(userActor, testData.getLikesType(), testData.getItemId());
        if (testData.getOwnerId() != null) query.ownerId(testData.getOwnerId());
        if (testData.getUserId() != null) query.userId(testData.getUserId());
        return query;
    }
}

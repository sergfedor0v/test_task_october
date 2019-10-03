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

@Test
public class IsLikedTest extends TestBase {

    private Integer testNoteItemId;

    @BeforeClass(groups = {"likes.isLiked"})
    @Step("Create test note item on behalf of test user and like it by test user")
    public void addLikes() throws ClientException, ApiException, InterruptedException {
        logger.info("IsLikedTest beforeClass");
        testNoteItemId = performRequest(vk.notes().add(userActor,
                "TestNote",
                "This is note for testing purposes"));
        performRequest(vk.likes().add(userActor, LikesType.NOTE, testNoteItemId));
    }

    @DataProvider
    public Object[][] isLikedTestDataProvider() {
        return new Object[][]{
                {
                        TestData.builder()
                                .withDescription("Check that when user like his own item, than isLiked request for this user should return true")
                                .withItemId(testNoteItemId)
                                .withLikesType(LikesType.NOTE)
                                .build(),
                        new ExpectedResponse(BoolInt.YES, BoolInt.NO)
                },
                {
                        TestData.builder()
                                .withDescription("Check that you can not get isLiked for private profile")
                                .withErrorMessage("Access denied (15): Access denied: this profile is private")
                                .withItemId(testNoteItemId)
                                .withLikesType(LikesType.NOTE)
                                .withUserId(TestData.PRIVATE_PROFILE_USER_ID)
                                .build(),
                        null
                }
        };
    }

    @Test(dataProvider = "isLikedTestDataProvider", groups = {"likes.isLiked"})
    @Description("Check 'likes.isLiked' API method")
    public void isLikedTest(TestData testData, ExpectedResponse expectedResponse) {
        Allure.description(testData.getTestDescription());
        LikesIsLikedQuery testQuery = createQuery(testData);
        IsLikedResponse response = testRequest(testQuery, testData);
        if (response != null) {
            Assert.assertEquals(response.isLiked(), expectedResponse.isLiked(), "response.isLiked from actual response should be equal to expected");
            Assert.assertEquals(response.isCopied(), expectedResponse.isCopied(), "response.copied from actual response be equal to expected");
        }
    }

    private LikesIsLikedQuery createQuery(TestData testData) {
        Allure.step("Create test query according to test data");
        LikesIsLikedQuery query = vk.likes().isLiked(userActor, testData.getLikesType(), testData.getItemId());
        if (testData.getOwnerId() != null) query.ownerId(testData.getOwnerId());
        if (testData.getUserId() != null) query.userId(testData.getUserId());
        return query;
    }

    private static class ExpectedResponse {
        private BoolInt liked;
        private BoolInt copied;

        ExpectedResponse(BoolInt liked, BoolInt copied) {
            this.liked = liked;
            this.copied = copied;
        }

        private boolean isLiked() {
            return liked == BoolInt.YES;
        }

        private boolean isCopied() {
            return copied == BoolInt.YES;
        }
    }
}

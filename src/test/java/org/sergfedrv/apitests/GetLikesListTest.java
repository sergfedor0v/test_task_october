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

public class GetLikesListTest extends TestBase {

    private Integer testNoteItemId;

    @BeforeClass(groups = {"likes.get"})
    @Step("Create test note item on behalf of test user and like it by test user")
    public void addLikes() throws ClientException, ApiException, InterruptedException {
        logger.info("GetLikesTest beforeClass");
        testNoteItemId = performRequest(vk.notes().add(userActor,
                "TestNote",
                "This is note for testing purposes"));
        performRequest(vk.likes().add(userActor, LikesType.NOTE, testNoteItemId));

    }

    @DataProvider
    public Object[][] getLikesTestDataProvider() {
        return new Object[][]{
                {
                        TestData.builder()
                                .withDescription("Check that when user like item, he or she exist in getLikesList of this item")
                                .withItemId(testNoteItemId)
                                .withLikesType(LikesType.NOTE)
                                .withOwnerId(userActor.getId())
                                .build(),
                        new ExpectedResponse(1, Collections.singletonList(userActor.getId()))
                },
                {
                        TestData.builder()
                                .withDescription("Send wrong LikeType to query and validate error message")
                                .withErrorMessage("")
                                .withItemId(testNoteItemId)
                                .withLikesType(LikesType.PHOTO_COMMENT)
                                .withOwnerId(userActor.getId())
                                .build(),
                        new ExpectedResponse(0, Collections.<Integer>emptyList())
                },
                {
                        TestData.builder()
                                .withDescription("Try to get likes list from photo, that closed by privacy settings")
                                .withErrorMessage("Access denied (15): Access denied: access restriction")
                                .withItemId(TestData.CLOSED_PHOTO_ID)
                                .withLikesType(LikesType.PHOTO)
                                .withOwnerId(TestData.PRIVATE_PROFILE_USER_ID)
                                .build(),
                        null
                }
        };
    }

    @Test(dataProvider = "getLikesTestDataProvider", groups = {"likes.get"})
    @Description("Check 'likes.get' API method")
    public void getLikesTest(TestData testData, ExpectedResponse expectedResponse) {
        Allure.description(testData.getTestDescription());
        LikesGetListQuery testQuery = createQuery(testData);
        GetListResponse response = testRequest(testQuery, testData);
        if (response != null) {
            Assert.assertEquals(response.getCount(), expectedResponse.getCount(), "Counts of likes should be the same");
            Assert.assertEquals(response.getItems(), expectedResponse.getItems(), "List of userIds should be the same");
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

    private static class ExpectedResponse{
        private Integer count;
        private List<Integer> items;

        ExpectedResponse(int count, List<Integer> items){
            this.count = count;
            this.items = items;
        }
        private Integer getCount() {
            return count;
        }
        private List<Integer> getItems() {
            return items;
        }
    }
}

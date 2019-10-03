package org.sergfedrv.apitests;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.likes.responses.AddResponse;
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

@Test
public class AddLikeTest extends TestBase {

    private int testNoteItemId;

    @BeforeClass
    @Step("Create new note for tests")
    public void createNoteForLike() throws ClientException, ApiException {
        testNoteItemId = vk.notes().add(userActor,
                "TestNote",
                "This is note for testing purposes").execute();
    }

    @DataProvider
    public Object[][] addLikesTestDataProvider() {
        return new Object[][]{
                {
                        TestData.builder()
                                .withLikesType(LikesType.NOTE)
                                .withItemId(testNoteItemId)
                                .withDescription("Check that current user can like note that was created in BeforeClass method")
                                .build(),
                },
                {
                        TestData.builder()
                                .withLikesType(LikesType.POST)
                                .withItemId(testNoteItemId)
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
                                .withItemId(430126411)
                                .withOwnerId(12438100)
                                .withDescription("Try to like photo that closed by privacy settings. Request should fail with expected error message")
                                .withErrorMessage("Access denied (15): Access denied: this profile is private")
                                .build()
                }
        };
    }

    @Test(dataProvider = "addLikesTestDataProvider")
    @Description("Check that it is possible to add like via API")
    public void addLikesTest(TestData testData) throws Exception {
        Allure.description(testData.getTestDescription());
        LikesAddQuery testQuery = createQuery(testData);
        AddResponse response = testRequest(testQuery, testData);
        if (response != null){
            Assert.assertEquals(response.getLikes(), new Integer(1), "Only one like should be added to note");
        }
    }

    private LikesAddQuery createQuery(TestData testData) {
        Allure.step("Create test query according to passed test data");
        LikesAddQuery testQuery = vk.likes().add(userActor, testData.getLikesType(), testData.getItemId());
        if (testData.getOwnerId() != null) testQuery.ownerId(testData.getOwnerId());
        return testQuery;
    }
}

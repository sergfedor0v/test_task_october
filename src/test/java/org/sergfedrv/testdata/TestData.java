package org.sergfedrv.testdata;

import com.vk.api.sdk.queries.likes.LikesGetListFilter;
import com.vk.api.sdk.queries.likes.LikesType;

public class TestData {
    public static Integer OPENED_PROFILE_USER_ID = 563917499; //opened to the world vk profile
    public static Integer PRIVATE_PROFILE_USER_ID = 12438100; //closed vk profile ID

    public static Integer OPENED_POST_ID = 5;
    public static Integer CLOSED_PHOTO_ID = 456239017;
    private Integer userId;
    private LikesType likesType;
    private Integer ownerId;
    private Integer itemId;
    private String expectedErrorMessage;
    private String testDescription;

    public LikesGetListFilter getLikesGetListFilterValue() {
        return likesGetListFilterValue;
    }

    private LikesGetListFilter likesGetListFilterValue;

    public Integer getUserId() {
        return userId;
    }

    public LikesType getLikesType() {
        return likesType;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getExpectedErrorMessage() {
        return expectedErrorMessage;
    }

    public String getTestDescription() {
        return testDescription;
    }

    public TestData(Integer userId, LikesType likesType, Integer ownerId, Integer itemId, String expectedErrorMessage, String testDescription, LikesGetListFilter likesGetListFilter) {
        this.userId = userId;
        this.likesType = likesType;
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.expectedErrorMessage = expectedErrorMessage;
        this.testDescription = testDescription;
        this.likesGetListFilterValue = likesGetListFilter;
    }

    public static Builder builder(){
        return new Builder();
    }

    @Override
    public String toString() {
        return "TestData{" +
                "userId=" + userId +
                ", likesType=" + likesType +
                ", ownerId=" + ownerId +
                ", itemId=" + itemId +
                ", expectedErrorMessage='" + expectedErrorMessage + '\'' +
                ", testDescription='" + testDescription + '\'' +
                '}';
    }

    public static class Builder{
        private Integer userId;
        private LikesType likesType;
        private Integer ownerId;
        private Integer itemId;
        private LikesGetListFilter likesGetListFilter;
        private String expectedErrorMessage;
        private String testDescription;

        public Builder withUserId(Integer userId){
            this.userId = userId;
            return this;
        }

        public Builder withLikesType(LikesType type){
            this.likesType = type;
            return this;
        }

        public Builder withOwnerId(Integer ownerId){
            this.ownerId = ownerId;
            return this;
        }

        public Builder withItemId(Integer itemId){
            this.itemId = itemId;
            return this;
        }

        public Builder withErrorMessage(String errorMessage){
            this.expectedErrorMessage = errorMessage;
            return this;
        }

        public Builder withDescription(String testDescription){
            this.testDescription = testDescription;
            return this;
        }

        public Builder withLikesGetListFilter(LikesGetListFilter likesGetListFilter){
            this.likesGetListFilter = likesGetListFilter;
            return this;
        }

        public TestData build(){
            return new TestData(this.userId, this.likesType, this.ownerId, this.itemId,this.expectedErrorMessage, this.testDescription, this.likesGetListFilter);
        }
    }
}

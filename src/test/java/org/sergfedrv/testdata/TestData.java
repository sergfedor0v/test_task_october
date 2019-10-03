package org.sergfedrv.testdata;

import com.vk.api.sdk.queries.likes.LikesType;

public class TestData {
    private Integer userId;
    private LikesType likesType;
    private Integer ownerId;
    private Integer itemId;
    private String expectedErrorMessage;
    private String testDescription;

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

    public TestData(Integer userId, LikesType likesType, Integer ownerId, Integer itemId, String expectedErrorMessage, String testDescription) {
        this.userId = userId;
        this.likesType = likesType;
        this.ownerId = ownerId;
        this.itemId = itemId;
        this.expectedErrorMessage = expectedErrorMessage;
        this.testDescription = testDescription;
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

        public TestData build(){
            return new TestData(this.userId, this.likesType, this.ownerId, this.itemId,this.expectedErrorMessage, this.testDescription);
        }
    }
}

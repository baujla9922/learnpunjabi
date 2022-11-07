package com.example.application.data.entity;

import javax.persistence.Entity;

@Entity
public class FlashCard extends AbstractEntity {

    private Integer lessonNumber;
    private Integer cardNumber;
    private String frontText;
    private String backText;

    public Integer getLessonNumber() {
        return lessonNumber;
    }
    public void setLessonNumber(Integer lessonNumber) {
        this.lessonNumber = lessonNumber;
    }
    public Integer getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(Integer cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getFrontText() {
        return frontText;
    }
    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }
    public String getBackText() {
        return backText;
    }
    public void setBackText(String backText) {
        this.backText = backText;
    }

}

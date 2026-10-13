package com.example.smartcampusrecruitment.model.dto;

import com.example.smartcampusrecruitment.Adapter.LocalDateTimeAdapter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("senderType")
    private SenderType senderType;

    @JsonProperty("senderPhone")
    private String senderPhone;

    @JsonProperty("receiverPhone")
    private String receiverPhone;

    @JsonProperty("content")
    private String content;

    @JsonProperty("touxiang")
    private String touxiang;

    @JsonProperty("sentAt")
    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime  sentAt;
    public enum SenderType {
        RECRUITER,
        JOB_SEEKER
    }

    public ChatMessageDTO() {
    }

    public ChatMessageDTO(SenderType senderType, String senderPhone, String receiverPhone, String content, String touxiang) {
        this.senderType = senderType;
        this.senderPhone = senderPhone;
        this.receiverPhone = receiverPhone;
        this.content = content;
        this.touxiang = touxiang;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public LocalDateTime  getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime  sentAt) {
        this.sentAt = sentAt;
    }
}
